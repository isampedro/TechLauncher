package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedCommentDTO;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedPostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Path("posts")
@Component
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);


    @Autowired
    private PostService ps;

    @Autowired
    private FrameworkService fs;

    @Autowired
    private PostCommentService commentService;

    @Autowired
    private PostTagService pts;

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    private final String START_PAGE = "1";
    private final long POSTS_PAGE_SIZE = 7;
    private final long COMMENTS_PAGE_SIZE = 5;
    private final int UP_VOTE_VALUE = 1;
    private final int DOWN_VOTE_VALUE = -1;

    private Response pagination(UriInfo uriInfo, int page, int pages, Object dto) {
        Response.ResponseBuilder response = Response.ok(dto)
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", pages).build(), "last");
        if (page < pages)
            response = response.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");
        if (page != 1)
            response = response.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        response = response.cacheControl(cc);
        return response.build();
    }

    private static Response.ResponseBuilder setCacheHeaders(Object resource) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        return Response.ok(resource).cacheControl(cc);
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response posts(@QueryParam("page") @DefaultValue(START_PAGE) Integer postsPage) {
        final double pages = Math.ceil(((double) ps.getPostsAmount()) / POSTS_PAGE_SIZE);
        List<Post> postsList = ps.getAll(postsPage, POSTS_PAGE_SIZE);
        PostsDTO list = new PostsDTO();
        Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PostDTO> posts = new ArrayList<>();
        for (Post post : postsList) {
            PostDTO postDTO = PostDTO.fromPost(post, uriInfo);
            user.ifPresent(value -> postDTO.setLoggedVote(value.getVoteForPost(post.getPostId())));
            posts.add(postDTO);
        }
        list.setPosts(posts);
        list.setPageSize((int) POSTS_PAGE_SIZE);
        list.setAmount(ps.getPostsAmount());
        list.setCurrentPage(postsPage);
        return pagination(uriInfo, postsPage, (int) pages, list);
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response post(@PathParam("id") long id) {
        Optional<Post> post = ps.findById(id);
        if (post.isPresent()) {
            LOGGER.info("Post {}: Requested and found, retrieving data", id);
            PostDTO dto = PostDTO.fromPost(post.get(), uriInfo);

            final Optional<User> optionalUser = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            optionalUser.ifPresent(value -> dto.setLoggedVote(value.getVoteForPost(id)));

            return setCacheHeaders(dto).build();
        }
        LOGGER.error("Post {}: Requested and not found", id);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/answers")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response answersOfPost(@PathParam("id") long id,
                                  @QueryParam("page") @DefaultValue(START_PAGE) int page) {

        Optional<Post> post = ps.findById(id);
        if (post.isPresent()) {
            LOGGER.info("Tech {}: Requested and found, retrieving data", id);
            final Optional<User> optionalUser = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());


            long amount = post.get().getAnswersAmount();
            int pages = (int) Math.ceil((double) amount / COMMENTS_PAGE_SIZE);
            List<PostCommentDTO> dto = commentService.getByPost(id, page).stream()
                    .map((PostComment comment) -> {
                        PostCommentDTO commentDTO = PostCommentDTO.fromComment(comment, uriInfo);
                        optionalUser.ifPresent(value -> commentDTO.setLoggedVote(comment.getUserAuthVote(value.getUsername())));
                        return commentDTO;
                    }).collect(Collectors.toList());
            return pagination(uriInfo, page, pages, new GenericEntity<List<PostCommentDTO>>(dto) {
            });
        }
        LOGGER.error("Tech {}: Requested and not found", id);
        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/tags")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getTags() {
        TagsDTO dto = new TagsDTO();
        for (String tag : fs.getAllCategories()) {
            dto.addCategory(PostTagDTO.fromString(tag, PostTagType.tech_category.name()));
        }
        for (String tag : fs.getAllTypes()) {
            dto.addType(PostTagDTO.fromString(tag, PostTagType.tech_type.name()));
        }
        for (String tag : fs.getFrameworkNames()) {
            dto.addName(PostTagDTO.fromString(tag, PostTagType.tech_name.name()));
        }
        return setCacheHeaders(dto).build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response addPost(@Valid final ValidatedPostDTO form) {
        Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (ps.isPostInvalid(form.getTitle(),form.getDescription(),form.getNames(), form.getCategories(), form.getTypes())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Post newPost = ps.insertPost(user.get().getId(), form.getTitle(), form.getDescription());
        if (form.getNames() != null) {
            for (String name : form.getNames()) {
                pts.insert(name, newPost.getPostId(), PostTagType.valueOf("tech_name"));
            }
        }
        if (form.getCategories() != null) {
            for (String c : form.getCategories()) {
                pts.insert(FrameworkCategories.valueOf(c).name(), newPost.getPostId(), PostTagType.valueOf("tech_category"));

            }
        }
        if (form.getTypes() != null) {
            for (String c : form.getTypes()) {
                pts.insert(FrameworkType.valueOf(c).name(), newPost.getPostId(), PostTagType.valueOf("tech_type"));
            }
        }
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newPost.getPostId())).build();
        return Response.created(uri).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response editPost(@Valid final ValidatedPostDTO form, @PathParam("id") long id) {
        final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        final Optional<Post> post = ps.findById(id);
        if (post.isPresent()) {
            if (ps.isPostInvalid(form.getTitle(),form.getDescription(),form.getNames(), form.getCategories(), form.getTypes())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if (post.get().getUser().getUsername().equals(user.get().getUsername()) || user.get().isAdmin()) {
                Optional<Post> updatedPost = ps.update(id, form.getTitle(), form.getDescription());

                if (updatedPost.isPresent()) {
                    List<String> names = form.getNames() == null ? Collections.emptyList() : form.getNames();
                    List<String> categories = form.getCategories() == null ? Collections.emptyList() : form.getCategories();
                    List<String> types = form.getTypes() == null ? Collections.emptyList() : form.getTypes();

                    pts.update(id, names, categories, types);

                    LOGGER.info("Post {}: Updated successfully with new information", id);
                    PostDTO dto = new PostDTO();
                    dto.setId(updatedPost.get().getPostId());
                    dto.setPostDescription(updatedPost.get().getDescription());
                    dto.setPostTitle(updatedPost.get().getDescription());
                    List<PostTagDTO> postTags = new LinkedList<>();
                    updatedPost.get().getPostTags().forEach((postTag) -> postTags.add(PostTagDTO.fromPostTag(postTag)));
                    dto.setPostTags(postTags);

                    return Response.ok(dto).build();
                }
                LOGGER.error("Posts: A problem occurred while creating the new Post");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            LOGGER.error("Post {}: Unauthorized user attempted to access page for updating", id);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        LOGGER.error("Post {}: Requested for getting update page and not found", id);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response deletePost(@PathParam("id") Long postId) {
        Optional<Post> post = ps.findById(postId);
        Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (post.isPresent()) {
            if (post.get().getUser().getUsername().equals(user.get().getUsername()) || user.get().isAdmin()) {
                ps.deletePost(postId);
                LOGGER.info("Posts: Post {} deleted successfully", postId);
                return Response.noContent().build();
            }

            LOGGER.error("Post {}: User without enough privileges attempted to delete the Post", postId);
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        LOGGER.error("Post {}: Requested for deleting Post and not found", postId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/up_vote")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response voteUpPost(@PathParam("id") long postId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

            ps.vote(postId, user.get().getId(), UP_VOTE_VALUE);
            LOGGER.info("Post {}: User {} voted up post", postId, user.get().getId());
            PostDTO postDTO = PostDTO.fromPost(post.get(), uriInfo);
            postDTO.setVotesUp(postDTO.getVotesUp() + 1);
            return Response.ok(postDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/down_vote")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response voteDownPost(@PathParam("id") long postId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            ps.vote(postId, user.get().getId(), DOWN_VOTE_VALUE);
            LOGGER.info("Post {}: User {} voted down post", postId, user.get().getId());
            PostDTO postDTO = PostDTO.fromPost(post.get(), uriInfo);
            postDTO.setVotesUp(postDTO.getVotesDown() + 1);
            return Response.ok(postDTO).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/answers/{commentId}/up_vote")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response voteUpComment(@PathParam("id") long postId, @PathParam("commentId") long commentId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            boolean appears = false;
            for (PostComment comment : post.get().getPostComments()) {
                if (comment.getPostCommentId() == commentId) {
                    appears = true;
                    break;
                }
            }
            if (user.isPresent() && appears) {
                commentService.vote(commentId, user.get().getId(), UP_VOTE_VALUE);
                return getVoteCommentResponse(commentId);
            }
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/answers/{commentId}/down_vote")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response voteDownComment(@PathParam("id") long postId, @PathParam("commentId") long commentId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            boolean appears = false;
            for (PostComment comment : post.get().getPostComments()) {
                if (comment.getPostCommentId() == commentId) {
                    appears = true;
                    break;
                }
            }
            if (user.isPresent() && appears) {
                commentService.vote(commentId, user.get().getId(), DOWN_VOTE_VALUE);
                return getVoteCommentResponse(commentId);
            }
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Response getVoteCommentResponse(long commentId) {
        Optional<PostComment> comment = commentService.getById(commentId);
        if (comment.isPresent()) {
            VoteDTO dto = new VoteDTO();
            dto.setCount(Double.valueOf(comment.get().getVotesUp()));
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    @POST
    @Path("/{id}/answers")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response commentPost(@Valid final ValidatedCommentDTO form, @PathParam("id") long postId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            if( user.isPresent()){
                commentService.insertPostComment(postId, user.get().getId(), form.getDescription(), null);
                return Response.ok(form).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}/answers/{commentId}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response deletePostComment(@PathParam("id") final long postId, @PathParam("commentId") final long commentId) {
        final Optional<Post> post = ps.findById(postId);
        if (post.isPresent()) {
            final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            commentService.deletePostComment(commentId);
            return Response.noContent().build();
        }
        LOGGER.error("Post {}: requested for deleting comment and not found", postId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
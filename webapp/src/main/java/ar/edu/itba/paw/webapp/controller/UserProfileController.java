package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedPasswordAndTokenDTO;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedPasswordDTO;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedUserUpdateDTO;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/users")
@Component
public class UserProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private FrameworkVoteService voteService;

    @Autowired
    private FrameworkService frameworkService;

    @Autowired
    private PostService postService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    final private long PAGE_SIZE = 5;
    final private long FRAMEWORK_PAGE_SIZE = 7;
    final private long VOTE_PAGE_SIZE = 10;
    final private String START_PAGE = "1";
    private final String PAGE = "page";

    private static Response.ResponseBuilder setConditionalCacheHeaders(Object resource, int hashcode, Request request) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);

        EntityTag etag = new EntityTag(Integer.toString(hashcode));
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

        if(builder == null){
            builder = Response.ok(resource);
            builder.tag(etag);
        }

        builder.cacheControl(cc);
        return builder;
    }

    private static Response.ResponseBuilder setCacheHeaders(Object resource) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        return Response.ok(resource).cacheControl(cc);
    }

    private Response.ResponseBuilder addPaginationLinks(Response.ResponseBuilder responseBuilder, String parameterName, long currentPage, long pages) {
        responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam(parameterName, 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam(parameterName, pages).build(), "last");
        if (currentPage < pages) {
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam(parameterName, currentPage + 1).build(), "next");
        }
        if (currentPage != 1) {
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam(parameterName, currentPage - 1).build(), "prev");
        }

        return responseBuilder;
    }

    @GET
    @Path("{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUser(@PathParam("id") Long userId, @Context Request request) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {

            LOGGER.info("User Profile: Requested user {} exists, retrieving data", userId);

            UserDTO dto = UserDTO.fromUser(user.get(), uriInfo);
            dto.setCommentAmount(commentService.getCommentsCountByUser(userId).orElse(0));
            dto.setContentAmount((int) (long) contentService.getContentCountByUser(userId).orElse(0L));
            dto.setTechsAmount(frameworkService.getByUserCount(userId).orElse(0));
            dto.setVotesAmount(voteService.getAllCountByUser(userId).orElse(0));
            dto.setPostsAmount(postService.getPostsCountByUser(userId).orElse(0));

            LOGGER.info("User Profile: User {} updated its profile successfully", user.get().getId());

            return setCacheHeaders(dto).build();
        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/comments")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUserComments(@PathParam("id") Long userId, @QueryParam("page") @DefaultValue(START_PAGE) Long commentsPage) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {

            final List<Comment> commentsList = commentService.getCommentsByUser(userId, commentsPage);
            final Optional<Integer> commentsAmount = commentService.getCommentsCountByUser(userId);

            if (commentsList.size() > 0) {
                List<CommentDTO> commentDTOList = commentsList.stream().map((Comment comment) -> CommentDTO.fromProfile(comment,uriInfo)).collect(Collectors.toList());
                Response.ResponseBuilder response = setCacheHeaders(new GenericEntity<List<CommentDTO>>(commentDTOList) {
                });

                return addPaginationLinks(response, PAGE, commentsPage, us.getPagesInt(commentsAmount,PAGE_SIZE)).build();
            }

            return Response.noContent().build();

        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/contents")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUserContents(@PathParam("id") Long userId, @QueryParam("page") @DefaultValue(START_PAGE) Long contentsPage) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            final List<Content> contentsList = contentService.getContentByUser(userId, contentsPage);
            final Optional<Long> contentsAmount = contentService.getContentCountByUser(userId);

            if (contentsList.size() > 0) {
                List<ContentDTO> contentDTOList = contentsList.stream().map((Content content) -> ContentDTO.fromProfile(content,uriInfo)).collect(Collectors.toList());
                Response.ResponseBuilder response = setCacheHeaders(new GenericEntity<List<ContentDTO>>(contentDTOList) {
                });
                return addPaginationLinks(response, PAGE, contentsPage, us.getPagesLong(contentsAmount,PAGE_SIZE)).build();
            }

            return Response.noContent().build();
        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/posts")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUserPosts(@PathParam("id") Long userId, @QueryParam("page") @DefaultValue(START_PAGE) Long postsPage) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            final List<Post> postsList = postService.getPostsByUser(userId, postsPage);
            final Optional<Integer> postsAmount = postService.getPostsCountByUser(userId);

            if (postsList.size() > 0) {
                List<PostDTO> postDTOList = postsList.stream().map((Post post) -> PostDTO.fromPost(post, uriInfo)).collect(Collectors.toList());
                Response.ResponseBuilder response = setCacheHeaders(new GenericEntity<List<PostDTO>>(postDTOList) {
                });
                return addPaginationLinks(response, PAGE, postsPage, us.getPagesInt(postsAmount,PAGE_SIZE)).build();
            }

            return Response.noContent().build();
        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/votes")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUserVotes(@PathParam("id") Long userId, @QueryParam("page") @DefaultValue(START_PAGE) Long votesPage) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            final List<FrameworkVote> votesList = voteService.getAllByUser(userId, votesPage);
            final Optional<Integer> votesAmount = voteService.getAllCountByUser(userId);

            if (votesList.size() > 0) {
                List<VoteDTO> voteDTOList = votesList.stream().map((FrameworkVote vote) -> VoteDTO.fromProfile(vote,uriInfo)).collect(Collectors.toList());
                Response.ResponseBuilder response = setCacheHeaders(new GenericEntity<List<VoteDTO>>(voteDTOList) {
                });
                return addPaginationLinks(response, PAGE, votesPage, us.getPagesInt(votesAmount,VOTE_PAGE_SIZE)).build();
            }

            return Response.noContent().build();
        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/techs")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getUserTechs(@PathParam("id") Long userId, @QueryParam("page") @DefaultValue(START_PAGE) Long techsPage) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            final List<Framework> techsList = frameworkService.getByUser(userId, techsPage);
            final Optional<Integer> techsAmount = frameworkService.getByUserCount(userId);

            if (techsList.size() > 0) {
                List<FrameworkDTO> frameworkDTOList = techsList.stream().map((Framework framework) -> FrameworkDTO.fromExtern(framework, uriInfo)).collect(Collectors.toList());
                Response.ResponseBuilder response = setCacheHeaders(new GenericEntity<List<FrameworkDTO>>(frameworkDTOList) {
                });
                return addPaginationLinks(response, PAGE, techsPage, us.getPagesInt(techsAmount,FRAMEWORK_PAGE_SIZE)).build();
            }

            return Response.noContent().build();
        }

        LOGGER.error("User Profile: Nonexistant user");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/image")
    @Produces({"image/jpg", "image/png", "image/gif"})
    public Response getImage(@PathParam("id") Long id, @Context Request request) {
        Optional<User> user = us.findById(id);

        if (user.isPresent()) {
            Optional<byte []> picture = pictureService.findPictureById(user.get().getPictureId());
            if (picture.isPresent()) {
                return setConditionalCacheHeaders(picture.get(), Arrays.hashCode(picture.get()), request).build();
            } else {
                return Response.noContent().build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/password")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response changePassword(@PathParam("id") Long userId, @Valid final ValidatedPasswordDTO form) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.get().getUsername())) {
                if (form.getPassword() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                us.updatePassword(userId, form.getPassword());
                LOGGER.info("Register: User {} updated its password successfully", userId);

                return Response.ok().build();
            }
            LOGGER.error("User Profile: User {} does not have enough privileges to update profile", userId);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        LOGGER.error("User Profile: Unauthorized user attempted to update another profile");
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("password")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response changePasswordWithToken(@Valid ValidatedPasswordAndTokenDTO passwordDTO) {

        Optional<User> user = us.findByToken(passwordDTO.getToken());

        if (user.isPresent()) {
            if (passwordDTO.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            us.updatePassword(user.get().getId(), passwordDTO.getPassword());
            LOGGER.info("Register: User {} updated its password successfully", user.get().getId());

            return Response.ok().build();
        }

        LOGGER.error("User Profile: User not found with token {}", passwordDTO.getToken());
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response updateProfile(@PathParam("id") Long userId,
                                  @Valid @FormDataParam("body") final ValidatedUserUpdateDTO dto,
                                  @Size(max = MAX_FILE_SIZE, message = "{ar.edu.itba.paw.validation.constraints.DefaultPicture.message}") @FormDataParam("picture") final byte[] picture) throws IOException {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.get().getUsername())) {

                if (picture == null && dto == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                us.updateInformation(userId, dto == null ? user.get().getDescription() : dto.getDescription(), picture);

                LOGGER.info("User Profile: User {} updated its profile successfully", user.get().getId());
                return Response.ok().build();
            }
            LOGGER.error("User Profile: User {} does not have enough privileges to update profile", userId);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        LOGGER.error("User Profile: Unauthorized user attempted to update another profile");
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @PUT
    @Path("/{id}/enable_modding/{value}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response enableMod(@PathParam("id") Long userId, @PathParam("value") Boolean value) {
        Optional<User> user = us.findById(userId);

        if (user.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.get().getUsername())) {
                us.updateModAllow(userId, value);
                LOGGER.info("User Profile: User {} updated its Mod Status successfully", userId);
                return Response.ok().build();
            }
            LOGGER.error("User Profile: User {} does not have enough privileges to update the Mod Status", userId);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        LOGGER.error("User Profile: User {} does not exist", userId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}

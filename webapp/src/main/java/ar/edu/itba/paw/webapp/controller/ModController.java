package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.service.CommentService;
import ar.edu.itba.paw.service.ContentService;
import ar.edu.itba.paw.service.FrameworkService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.ReportDTO;
import ar.edu.itba.paw.webapp.dto.VerifyUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("mod")
@Component
public class ModController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModController.class);
    private static final String DEFAULT_PROMOTE_TAB = "promote";
    private static final String DEFAULT_DEMOTE_TAB = "demote";
    private static final String DEFAULT_REPORT_TAB = "reports";

    @Autowired
    private UserService us;

    @Autowired
    private FrameworkService fs;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ContentService contentService;

    @Context
    private UriInfo uriInfo;

    private final long pageStart = 1;
    private final long PAGE_SIZE = 5;
    private final String START_PAGE = "1";
    private final String PAGE = "page";

    private static final String MOD_VIEW = "/mod?tabs=";

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
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        return responseBuilder.cacheControl(cc);
    }



    @GET
    @Path("/moderators")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response moderators(@QueryParam("page") @DefaultValue(START_PAGE) Long modsPage) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<Framework> ownedFrameworks = user.getOwnedFrameworks();
        int modsAmount = 0;
        List<VerifyUser> userList = Collections.emptyList();
        List<VerifyUserDTO> verifyUserDTOList;

        if (user.isAdmin()) {
            userList = us.getVerifyByPending(false, modsPage);
            modsAmount = us.getVerifyByPendingAmount(false).get();

        } else if (ownedFrameworks.size() > 0) {
            List<Long> frameworkIds = us.getOwnedFrameworks(user);

            userList = us.getVerifyByPendingAndFrameworks(false, frameworkIds, modsPage);
            modsAmount = us.getVerifyByPendingAndFrameworksAmount(false, frameworkIds);
        }

        if (userList.size() > 0) {
            verifyUserDTOList = userList.stream().map((VerifyUser verifyUser) -> VerifyUserDTO.fromVerifyUser(verifyUser,uriInfo)).collect(Collectors.toList());
            long pages = (long) Math.ceil(((double) modsAmount) / PAGE_SIZE);
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<VerifyUserDTO>>(verifyUserDTOList) {
            });
            return addPaginationLinks(response, PAGE, modsPage, pages).build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/applicants")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response applicants(@QueryParam("page") @DefaultValue(START_PAGE) Long applicantsPage) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<Framework> ownedFrameworks = user.getOwnedFrameworks();
        int modsAmount = 0;
        List<VerifyUser> userList = Collections.emptyList();
        List<VerifyUserDTO> verifyUserDTOList;

        if (user.isAdmin()) {
            userList = us.getApplicantsByPending(true, applicantsPage);
            modsAmount = us.getApplicantsByPendingAmount(true).get();

        } else if (ownedFrameworks.size() > 0 || user.isVerify()) {
            List<Long> frameworkIds = us.getVerifiedFrameworks(user);

            userList = us.getApplicantsByFrameworks(frameworkIds, applicantsPage);
            modsAmount = us.getApplicantsByFrameworkAmount(frameworkIds, true).get();
        }

        if (userList.size() > 0) {
            verifyUserDTOList = userList.stream().map((VerifyUser verifyUser) -> VerifyUserDTO.fromVerifyUser(verifyUser,uriInfo)).collect(Collectors.toList());
            long pages = (long) Math.ceil(((double) modsAmount) / PAGE_SIZE);
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<VerifyUserDTO>>(verifyUserDTOList) {
            });
            return addPaginationLinks(response, PAGE, applicantsPage, pages).build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/verified")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response verified(@QueryParam("page") @DefaultValue(START_PAGE) Long verifyPage) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<Framework> ownedFrameworks = user.getOwnedFrameworks();
        int modsAmount = 0;
        List<VerifyUser> userList = Collections.emptyList();
        List<VerifyUserDTO> verifyUserDTOList = null;

        if (user.isAdmin()) {
            userList = us.getVerifyByPending(true, verifyPage);
            modsAmount = us.getVerifyByPendingAmount(true).get();

        } else if (ownedFrameworks.size() > 0 || user.isVerify()) {
            List<Long> frameworkIds = us.getVerifiedFrameworks(user);

            userList = us.getVerifyByPendingAndFrameworks(true, frameworkIds, verifyPage);
            modsAmount = us.getVerifyByPendingAndFrameworksAmount(true, frameworkIds);
        }

        if (userList.size() > 0) {
            verifyUserDTOList = userList.stream().map((VerifyUser verifyUser) -> VerifyUserDTO.fromVerifyUser(verifyUser,uriInfo)).collect(Collectors.toList());
            long pages = (long) Math.ceil(((double) modsAmount) / PAGE_SIZE);
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<VerifyUserDTO>>(verifyUserDTOList) {
            });
            return addPaginationLinks(response, PAGE, verifyPage, pages).build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/reported_comments")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reportedComments(@QueryParam("page") @DefaultValue(START_PAGE) Long rComPage) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<Framework> ownedFrameworks = user.getOwnedFrameworks();
        int reportsAmount = 0;
        List<ReportComment> commentList = Collections.emptyList();
        List<ReportDTO> reportDTOList;

        if (user.isAdmin()) {
            commentList = commentService.getAllReport(rComPage);
            reportsAmount = commentService.getAllReportsAmount().get();

        } else if (ownedFrameworks.size() > 0) {
            List<Long> frameworkIdsForReportedComments = us.getOwnedFrameworks(user);

            commentList = commentService.getReportsByFrameworks(frameworkIdsForReportedComments, rComPage);
            reportsAmount = commentService.getReportsAmountByFrameworks(frameworkIdsForReportedComments);
        }

        if (commentList.size() > 0) {
            reportDTOList = commentList.stream().map((reportedComment) -> ReportDTO.fromReportComment(reportedComment, uriInfo)).collect(Collectors.toList());
            long pages = (long) Math.ceil(((double) reportsAmount) / PAGE_SIZE);
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<ReportDTO>>(reportDTOList) {
            });
            return addPaginationLinks(response, PAGE, rComPage, pages).build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/reported_contents")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reportedContents(@QueryParam("page") @DefaultValue(START_PAGE) Long rConPage) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<Framework> ownedFrameworks = user.getOwnedFrameworks();
        int reportsAmount = 0;
        List<ReportContent> contentList = Collections.emptyList();
        List<ReportDTO> reportDTOList;

        if (user.isAdmin()) {
            contentList = contentService.getAllReports(rConPage);
            reportsAmount = contentService.getAllReportsAmount().get();

        } else if (ownedFrameworks.size() > 0 || user.isVerify()) {
            List<Long> frameworkIds = us.getVerifiedFrameworks(user);

            contentList = contentService.getReportsByFrameworks(frameworkIds, rConPage);
            reportsAmount = contentService.getReportsAmount(frameworkIds).get();
        }

        if (contentList.size() > 0) {
            reportDTOList = contentList.stream().map((reportedContent) -> ReportDTO.fromReportContent(reportedContent, uriInfo)).collect(Collectors.toList());
            long pages = (long) Math.ceil(((double) reportsAmount) / PAGE_SIZE);
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<ReportDTO>>(reportDTOList) {
            });
            return addPaginationLinks(response, PAGE, rConPage, pages).build();
        }

        return Response.noContent().build();
    }


    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response demoteMod(@PathParam("id") Long verificationId) {
        Optional<User> user = us.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        Optional<VerifyUser> vu = us.getVerifyById(verificationId);

        if (user.get().isAdmin() || !user.get().getOwnedFrameworks().isEmpty()) {
            if (vu.isPresent()) {
                if (!vu.get().isPending()) {
                    us.deleteVerification(verificationId);
                    LOGGER.info("User: Demoted user according to Verification {}", verificationId);
                    return Response.ok().build();
                }
                return Response.notModified().build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/pending/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response acceptMod(@PathParam("id") Long verificationId) {
        Optional<User> user = us.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        Optional<VerifyUser> vu = us.getVerifyById(verificationId);
        if (user.isPresent() && vu.isPresent() && (user.get().isAdmin() || !user.get().getOwnedFrameworks().isEmpty() || user.get().isVerifyForFramework(vu.get().getFrameworkId()))) {
            if (vu.get().isPending()) {
                us.verify(verificationId);
                Optional<User> u = us.findById(vu.get().getUserId());
                u.ifPresent(value -> us.modMailing(value, vu.get().getFrameworkName()));
                return Response.ok().build();
            }
            return Response.notModified().build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @DELETE
    @Path("/pending/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response rejectMod(@PathParam("id") Long verificationId) {
        Optional<User> user = us.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        Optional<VerifyUser> vu = us.getVerifyById(verificationId);

        if (user.isPresent() && vu.isPresent() && (user.get().isAdmin() || !user.get().getOwnedFrameworks().isEmpty() || user.get().isVerifyForFramework(vu.get().getFrameworkId()))) {
            if (vu.get().isPending()) {
                if (vu.get().isPending()) {
                    us.deleteVerification(verificationId);
                    return Response.ok().build();
                }
                return Response.notModified().build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/tech/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response applyForTech(@PathParam("id") Long techId) {
        Optional<User> user = us.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        if (!user.get().hasAppliedToFramework(techId)) {
            Optional<Framework> framework = fs.findById(techId);
            if (framework.isPresent()) {
                us.createVerify(user.get(), framework.get());
                return Response.ok().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @DELETE
    @Path("/tech/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response quitModdingFromTech(@PathParam("id") Long techId) {
        final Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (us.quitModdingFromTech(user.get(), techId)) {
            LOGGER.info("Tech {}: User {} is no longer a Mod", techId, user.get().getId());
            return Response.ok().build();
        }
        LOGGER.error("Tech {}: User {} tried to quit from being a Mod without previously being one", techId, user.get().getId());
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/reports/comment/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response deleteCommentReport(@PathParam("id") Long commentId) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (user.isAdmin() || user.getOwnedFrameworks().size() > 0) {
            commentService.denyReport(commentId);
            LOGGER.info("User: Comment {} was removed from its report", commentId);
            return Response.ok().build();
        }
        LOGGER.error("User: User {} has not enough privileges to ignore a reported comment", user.getId());
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/reports/comment/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response acceptCommentReport(@PathParam("id") Long commentId) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (user.isAdmin() || user.getOwnedFrameworks().size() > 0) {
            Optional<Comment> commentOptional = commentService.getById(commentId);
            if (commentOptional.isPresent()) {
                commentService.acceptReport(commentId);
                LOGGER.info("User: Comment {} was successfully deleted", commentId);
                return Response.ok().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.error("User: User {} has not enough privileges to ignore a reported comment", user.getId());
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @DELETE
    @Path("/reports/content/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response deleteContentReport(@PathParam("id") Long contentId) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<Content> content = contentService.getById(contentId);
        if ( content.isPresent() && (user.isAdmin() || user.getOwnedFrameworks().size() > 0 || user.isVerifyForFramework(content.get().getFrameworkId()))) {
            contentService.denyReport(contentId);
            LOGGER.info("User: Content {} was removed from its report", contentId);
            return Response.ok().build();
        }
        LOGGER.error("User: User {} has not enough privileges to ignore a reported content", user.getId());
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/reports/content/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response acceptContentReport(@PathParam("id") Long contentId) {
        User user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<Content> content = contentService.getById(contentId);
        if ( content.isPresent() && (user.isAdmin() || user.getOwnedFrameworks().size() > 0 || user.isVerifyForFramework(content.get().getFrameworkId()))) {
            Optional<Content> contentOptional = contentService.getById(contentId);
            if (contentOptional.isPresent()) {
                contentService.acceptReport(contentId);
                LOGGER.info("User: Content {} was successfully deleted", contentId);
                return Response.ok().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.error("User: User {} has not enough privileges to ignore a reported content", user.getId());
        return Response.status(Response.Status.FORBIDDEN).build();
    }


}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.service.ExploreService;
import ar.edu.itba.paw.service.FrameworkService;
import ar.edu.itba.paw.service.PostService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.FrameworkDTO;
import ar.edu.itba.paw.webapp.dto.PostDTO;
import ar.edu.itba.paw.webapp.dto.SearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("explore")
@Component
public class ExploreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExploreController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FrameworkService fs;

    @Autowired
    private ExploreService es;

    @Context
    private UriInfo uriInfo;

    private final long TECHS_PAGE_SIZE = 24;
    private final long POSTS_PAGE_SIZE = 5;

    private UriBuilder addFilters( UriInfo uriInfo, int page, String toSearch,
                                   List<String> categories,
                                   List<String> types,
                                   Integer starsLeft,
                                   Integer starsRight,
                                   boolean nameFlag,
                                   Integer order,
                                   Integer commentAmount,
                                   Integer lastComment,
                                   Integer lastUpdate,
                                   boolean isPost){

        UriBuilder path = uriInfo.getAbsolutePathBuilder().queryParam("page",page).queryParam("to_search", toSearch);
        if( !isPost ){
            path = path.queryParam("name_flag", nameFlag);
        } else {
            path = path.queryParam("is_post", isPost);
        }
        if( !categories.isEmpty() && !categories.get(0).equals("")){
            for (String category : categories) {
                path = path.queryParam("categories", category);
            }
        }

        if( !types.isEmpty() && !categories.get(0).equals("")){
            for (String type : types) {
                path = path.queryParam("types", type);
            }
        }

        if( order != null ){
            path = path.queryParam("order", order);
        }
        if( starsLeft != null ){
            path = path.queryParam("stars_left", starsLeft);
        }
        if( starsRight != null ){
            path = path.queryParam("stars_right", starsRight);
        }
        if(commentAmount != null ){
            path = path.queryParam("comment_amount", commentAmount);
        }
        if(lastComment != null) {
            path = path.queryParam("last_comment", lastComment);
        }
        if(lastUpdate != null) {
            path = path.queryParam("last_update", lastUpdate);
        }
        return path;
    }
    private Response pagination(UriInfo uriInfo,int page,int pages,Object dto, String toSearch,
                                List<String> categories,
                                List<String> types,
                                Integer starsLeft,
                                Integer starsRight,
                                boolean nameFlag,
                                Integer order,
                                Integer commentAmount,
                                Integer lastComment,
                                Integer lastUpdate,
                                boolean isPost){
        Response.ResponseBuilder response = Response.ok(dto)
                .link(addFilters(uriInfo, 1, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, isPost).build(),"first")
                .link(addFilters(uriInfo, pages, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, isPost).build(),"last");
        if(page < pages){
            response = response.link(addFilters(uriInfo, page+1, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, isPost).build(),"next");
        }

        if(page != 1) {
            response = response.link(addFilters(uriInfo, page-1, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, isPost).build(),"prev");
        }
        return  response.build();
    }

    @GET
    @Path("techs")
    public Response advancedSearchTechs(@QueryParam("to_search") @DefaultValue("") String toSearch,
                                   @QueryParam("categories") final List<String> categories,
                                   @QueryParam("types") final List<String> types,
                                   @QueryParam("stars_left") final Integer starsLeft,
                                   @QueryParam("stars_right") final Integer starsRight,
                                   @QueryParam("name_flag") final boolean nameFlag,
                                   @QueryParam("order") final Integer order,
                                   @QueryParam("comment_amount") final Integer commentAmount,
                                   @QueryParam("last_comment") final Integer lastComment,
                                   @QueryParam("last_update") final Integer lastUpdate,
                                   @QueryParam("page") final int page){

        List<FrameworkCategories> categoriesList = new ArrayList<>();
        List<FrameworkType> typesList = new ArrayList<>();
        SearchDTO search = new SearchDTO();
        Integer searchResultsNumber;

        LOGGER.info("Explore: Searching results for: {}", toSearch);
        LOGGER.info("Explore: Searching categories among: {}", categories);
        LOGGER.info("Explore: Searching types among: {}", types);
        LOGGER.info("Explore: Searching stars between: {} and {}", starsLeft, starsRight);
        LOGGER.info("Explore: Using search only by name: {}", nameFlag);
        LOGGER.info("Explore: Searching by comment amount = {}", commentAmount);

        if (order != null && (order < -4 || order > 4)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(es.isExploringByMultiple(categories)) {
            try {
                categoriesList = es.getParsedCategories(categories);
            } catch (Exception e){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        if(es.isExploringByMultiple(types)) {
            try {
                typesList = es.getParsedTypes(types);
            } catch (Exception e){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        List<String> allCategories = fs.getAllCategories();
        List<String> allTypes = fs.getAllTypes();

        if(allCategories.contains(toSearch)){
            categoriesList.add(FrameworkCategories.valueOf(toSearch));
            toSearch="";
        } else if(allTypes.contains(toSearch)){
            toSearch="";
        }

        Date parsedLastComment = null;
        Date parsedLastUpdate = null;

        if (lastComment != null) {
            LOGGER.info("Explore: Searching 'last comment' according to criteria {}", lastComment);
            parsedLastComment = es.getParsedDateOption(lastComment);
        }

        if (lastUpdate != null) {
            LOGGER.info("Explore: Searching 'last update' according to criteria {}", lastUpdate);
            parsedLastUpdate = es.getParsedDateOption(lastUpdate);
        }

        setExploreParams(search, toSearch, categories, types, starsLeft, starsRight, nameFlag, lastComment, lastUpdate, order);

        /* --------------------- TECHS --------------------- */
        List<Framework> frameworks = es.searchFrameworks(!toSearch.equals("") ? toSearch : null, categoriesList.isEmpty() ? null : categoriesList, typesList.isEmpty() ? null : typesList, starsLeft == null ? 0 : starsLeft, starsRight == null ? 5 : starsRight, nameFlag, commentAmount == null ? 0 : commentAmount, parsedLastComment, parsedLastUpdate, order, page == 0 ? 1 : page);
        searchResultsNumber = es.getFrameworksResultNumber(!toSearch.equals("") ? toSearch : null, categoriesList.isEmpty() ? null : categoriesList, typesList.isEmpty() ? null : typesList, starsLeft == null ? 0 : starsLeft, starsRight == null ? 5 : starsRight, nameFlag, commentAmount == null ? 0 : commentAmount, parsedLastComment, parsedLastUpdate);

        LOGGER.info("Explore: Found {} matching techs", searchResultsNumber);

        int pages = (int) Math.ceil(((double)searchResultsNumber)/TECHS_PAGE_SIZE);

        search.setFrameworksAmount(searchResultsNumber);
        search.setFrameworks(frameworks.stream().map((Framework framework) -> FrameworkDTO.fromExtern(framework,uriInfo)).collect(Collectors.toList()));

        return pagination(uriInfo, page == 0 ? 1 : page, pages, search, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, false);
        /* -------------------------------------------------- */
    }

    @GET
    @Path("posts")
    public Response advancedSearchPosts(@QueryParam("to_search") @DefaultValue("") String toSearch,
                                   @QueryParam("categories") final List<String> categories,
                                   @QueryParam("types") final List<String> types,
                                   @QueryParam("stars_left") final Integer starsLeft,
                                   @QueryParam("stars_right") final Integer starsRight,
                                   @QueryParam("name_flag") final boolean nameFlag,
                                   @QueryParam("order") final Integer order,
                                   @QueryParam("comment_amount") final Integer commentAmount,
                                   @QueryParam("last_comment") final Integer lastComment,
                                   @QueryParam("last_update") final Integer lastUpdate,
                                   @QueryParam("page") final int page) {

        Date parsedLastComment = null;
        Date parsedLastUpdate = null;
        SearchDTO search = new SearchDTO();
        Integer searchResultsNumber;
        List<String> tags;

        LOGGER.info("Explore: Searching results for: {}", toSearch);
        LOGGER.info("Explore: Searching categories among: {}", categories);
        LOGGER.info("Explore: Searching types among: {}", types);
        LOGGER.info("Explore: Searching stars between: {} and {}", starsLeft, starsRight);
        LOGGER.info("Explore: Using search only by name: {}", nameFlag);
        LOGGER.info("Explore: Searching by comment amount = {}", commentAmount);

        if (order != null && (order < -4 || order > 4)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }



        setExploreParams(search, toSearch, categories, types, null, null, null, lastComment, lastUpdate, order);

        if (lastComment != null) {
            LOGGER.info("Explore: Searching 'last comment' according to criteria {}", lastComment);
            parsedLastComment = es.getParsedDateOption(lastComment);
        }

        if (lastUpdate != null) {
            LOGGER.info("Explore: Searching 'last update' according to criteria {}", lastUpdate);
            parsedLastUpdate = es.getParsedDateOption(lastUpdate);
        }

        tags = es.getTags(categories, types);

        List<Post> posts = es.searchPosts(!toSearch.equals("") ? toSearch : null, tags.isEmpty() ? null : tags, 0, 0, commentAmount == null ? 0 : commentAmount, parsedLastComment, parsedLastUpdate, order, page == 0 ? 1 : page, POSTS_PAGE_SIZE);
        searchResultsNumber = es.getPostsResultsNumber(!toSearch.equals("") ? toSearch : null, tags.isEmpty() ? null : tags, 0, 0, commentAmount == null ? 0 : commentAmount, parsedLastComment, parsedLastUpdate, order);
        LOGGER.info("Explore: Found {} matching posts", searchResultsNumber);

        int pages = (int) Math.ceil(((double)searchResultsNumber)/POSTS_PAGE_SIZE);

        search.setPosts(posts.stream().map((Post post) -> PostDTO.fromPost(post, uriInfo)).collect(Collectors.toList()));
        search.setPostsAmount(searchResultsNumber);

        return pagination(uriInfo, page == 0 ? 1 : page, pages, search, toSearch, categories, types, starsLeft, starsRight, nameFlag, order, commentAmount, lastComment, lastUpdate, true);

    }




   private void setExploreParams(SearchDTO searchDTO, String toSearch, List<String> categories, List<String> types, Integer starsLeft, Integer starsRight, Boolean nameFlag, Integer lastComment, Integer lastUpdate, Integer order){

        searchDTO.setToSearch(toSearch);
        searchDTO.setLastComment(lastComment);
        searchDTO.setLastUpdate(lastUpdate);

       List<String> parsedCategories = new ArrayList<>();
       List<String> parsedTypes = new ArrayList<>();

       if (categories.size() != 1 || !categories.get(0).equals("")) {
           parsedCategories.addAll(categories);
       }

       if (types.size() != 1 || !types.get(0).equals("")) {
           parsedTypes.addAll(types);
       }

       searchDTO.setCategories(parsedCategories);
       searchDTO.setTypes(parsedTypes);

       if (starsLeft != null) {
           searchDTO.setStarsLeft(starsLeft);
       }
       if (starsRight != null) {
           searchDTO.setStarsRight(starsRight);
       }
       if (nameFlag != null) {
           searchDTO.setNameFlag(nameFlag);
       }

       if (order != null) {
           searchDTO.setOrder((int) Math.signum(order));
           searchDTO.setSort(Math.abs(order));
       } else {
           searchDTO.setOrder(1);
           searchDTO.setSort(0);
       }
   }



   private boolean isOrderInvalid(Integer order){
        return  (order != null && (order < -4 || order > 4));
   }

}



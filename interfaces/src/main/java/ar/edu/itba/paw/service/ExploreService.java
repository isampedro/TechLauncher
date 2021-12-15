package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ExploreService {

    Date getParsedDateOption(Integer dateOption);
    List<FrameworkCategories> getParsedCategories(List<String> categories);
    List<FrameworkType> getParsedTypes(List<String> types);
    List<String> getTags(List<String> categories, List<String> types);

    boolean isExploringByMultiple(List<String> list);

    public List<Post> searchPosts(String toSearch, List<String> tags, Integer starsLeft, Integer starsRight, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page, long pageSize);
    public Integer getPostsResultsNumber(String toSearch, List<String> tags, Integer starsLeft, Integer starsRight, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order);

    public List<Framework> searchFrameworks(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page);
    public Integer getFrameworksResultNumber(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated);
    }

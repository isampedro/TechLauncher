package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.models.FrameworkType;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.persistence.FrameworkDao;
import ar.edu.itba.paw.persistence.PostDao;
import ar.edu.itba.paw.service.ExploreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExploreServiceImpl implements ExploreService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private FrameworkDao frameworkDao;

    final int DAYS = 1, WEEK = 2, MONTH = 3, MONTHS = 4, YEAR = 5;
    private final long PAGE_SIZE = 7;
    private final long PAGE_SIZE_SEARCH = 24;

    @Override
    public Date getParsedDateOption(Integer dateOption) {
        LocalDate localDate = null;
        switch (dateOption) {
            case DAYS:
                localDate = LocalDate.now().minusDays(3);
                break;
            case WEEK:
                localDate = LocalDate.now().minusWeeks(1);
                break;
            case MONTH:
                localDate = LocalDate.now().minusMonths(1);
                break;
            case MONTHS:
                localDate = LocalDate.now().minusMonths(3);
                break;
            case YEAR:
                localDate = LocalDate.now().minusYears(1);
                break;
        }

        if(localDate != null){
            return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        }
        return null;
    }

    @Override
    public List<FrameworkCategories> getParsedCategories(List<String> categories) {
        List<FrameworkCategories> parsedCategories = new ArrayList<>();
        for (String c : categories) {
            parsedCategories.add(FrameworkCategories.valueOf(c));
        }
        return parsedCategories;
    }

    @Override
    public List<FrameworkType> getParsedTypes(List<String> types) {
        List<FrameworkType> parsedTypes = new ArrayList<>();
        for (String t : types) {
            parsedTypes.add(FrameworkType.valueOf(t));
        }
        return parsedTypes;
    }

    @Override
    public List<String> getTags(List<String> categories, List<String> types) {
        List<String> tags = new ArrayList<>();
        tags.addAll(categories);
        tags.addAll(types);
        
        return tags;
    }

    @Override
    public boolean isExploringByMultiple(List<String> list) {
        return (list.size() != 1 || !list.get(0).equals(""));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> searchPosts(String toSearch, List<String> tags, Integer starsLeft, Integer starsRight, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page, long pageSize) {
        return postDao.search(toSearch,tags,starsLeft,starsRight,commentAmount,lastComment,lastUpdated,order,page,pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getPostsResultsNumber(String toSearch, List<String> tags, Integer starsLeft, Integer starsRight, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order){
        if(starsLeft<starsRight)
            return postDao.searchResultsNumber(toSearch,tags,starsLeft,starsRight,commentAmount,lastComment,lastUpdated, order);
        return postDao.searchResultsNumber(toSearch,tags,starsRight,starsLeft,commentAmount,lastComment,lastUpdated, order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> searchFrameworks(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page) {
        if(starsLeft<starsRight)
            return frameworkDao.search(toSearch,categories,types,starsLeft,starsRight,nameFlag,commentAmount,lastComment,lastUpdated,order, page, PAGE_SIZE_SEARCH);
        return frameworkDao.search(toSearch,categories,types,starsRight,starsLeft,nameFlag,commentAmount,lastComment,lastUpdated,order, page, PAGE_SIZE_SEARCH);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getFrameworksResultNumber(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated){
        if(starsLeft<starsRight)
            return frameworkDao.searchResultsNumber(toSearch,categories,types,starsLeft,starsRight,nameFlag,commentAmount,lastComment,lastUpdated);
        return frameworkDao.searchResultsNumber(toSearch,categories,types,starsRight,starsLeft,nameFlag,commentAmount,lastComment,lastUpdated);
    }
}

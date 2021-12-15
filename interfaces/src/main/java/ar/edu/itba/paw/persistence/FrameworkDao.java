package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.models.FrameworkType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FrameworkDao {
    Optional<Framework> findById(long id);
    List<String> getFrameworkNames();
    List<Framework> getByCategory(FrameworkCategories category, long page, long pageSize);
    List<Framework> getAll();
    List<Framework> getUserInterests(long userId);
    List<Framework> getByMinStars(int stars);
    List<Framework> getByUser(long userId, long page, long pageSize);
    Optional<Framework> getByName(String name);
    int getAmountByCategory(FrameworkCategories categories);

    Optional<Integer> getByUserCount(long userId);

    List<Framework> search(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page, long pageSize);
    Optional<Framework> create(String name, FrameworkCategories category, String description, String introduction, FrameworkType type, long userId, byte[] picture);
    Optional<Framework> update(long id,String name,FrameworkCategories category,String description,String introduction,FrameworkType type, byte[] picture);
    void delete(long id);
    Integer searchResultsNumber(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated);
}

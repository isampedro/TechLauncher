package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.models.FrameworkType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FrameworkService {
    Optional<Framework> findById(long frameworkId);
    List<String> getFrameworkNames();
    List<Framework> getByCategory(FrameworkCategories category, long page);
    List<Framework> getCompetitors(Framework framework);
    List<Framework> getBestRatedFrameworks();
    List<Framework> getUserInterests(long userId);
    Optional<Framework> getByName(String name);
    List<Framework> getByUser(long userId, long page);
     int getAmountByCategory(FrameworkCategories categories);

    Optional<Integer> getByUserCount(long userId);

    Optional<Framework> create(String name, FrameworkCategories category, String description, String introduction, FrameworkType type, long userId, byte[] picture);
    Optional<Framework> update(long id,String name,FrameworkCategories category,String description,String introduction,FrameworkType type,byte[] picture);
    void delete(long id);

    List<String> getAllCategories();

    List<String> getAllTypes();

    boolean nameAvailable(String tech, Long id);
}

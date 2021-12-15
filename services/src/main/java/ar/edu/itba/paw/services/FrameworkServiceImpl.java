package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.models.FrameworkType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.FrameworkDao;
import ar.edu.itba.paw.service.FrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FrameworkServiceImpl implements FrameworkService {
    @Autowired
    private FrameworkDao frameworkDao;

    private final long PAGE_SIZE = 7;
    private final long PAGE_SIZE_SEARCH = 24;

    @Transactional(readOnly = true)
    @Override
    public Optional<Framework> findById(long id) {
        return frameworkDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getFrameworkNames() {
        return frameworkDao.getFrameworkNames();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> getByCategory(FrameworkCategories category, long page) {
        return frameworkDao.getByCategory(category, page, PAGE_SIZE);
    }


    @Transactional(readOnly = true)
    @Override
    public int getAmountByCategory(FrameworkCategories categories) {
        return frameworkDao.getAmountByCategory(categories);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> getByUser(long userId, long page) {
        return frameworkDao.getByUser(userId, page, PAGE_SIZE);
    }

    @Transactional
    @Override
    public Optional<Integer> getByUserCount( long userId ){
        return frameworkDao.getByUserCount(userId);
    }

    @Transactional
    @Override
    public Optional<Framework> create(String name, FrameworkCategories category, String description, String introduction, FrameworkType type,long userId, byte[] picture) {
        return frameworkDao.create(name,category,description,introduction,type,userId, picture);
    }

    @Transactional
    @Override
    public Optional<Framework> update(long id, String name, FrameworkCategories category, String description, String introduction, FrameworkType type, byte[] picture) {
        return frameworkDao.update(id,name,category,description,introduction,type,picture);
    }

    @Transactional
    @Override
    public void delete(long id) {
        frameworkDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> getBestRatedFrameworks() {
        List<Framework> toReturn = frameworkDao.getByMinStars(4);
        return toReturn.size() > 5 ? toReturn.subList(0,4) : toReturn;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> getUserInterests(long userId) { return frameworkDao.getUserInterests(userId); }

    @Transactional(readOnly = true)
    @Override
    public Optional<Framework> getByName(String name) {
        return frameworkDao.getByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Framework> getCompetitors(Framework framework) {
        List<Framework> toReturn = getByCategory(framework.getCategory(), 1);
        toReturn.remove(framework);
        return toReturn.size() > 5 ? toReturn.subList(0,4) : toReturn;
    }

    @Override
    public List<String> getAllCategories() {
        List<String> frameworkCategories = new ArrayList<>();
        for (FrameworkCategories frameworkCategory : FrameworkCategories.values()) {
            frameworkCategories.add(frameworkCategory.name());
        }
        return frameworkCategories;
    }

    @Override
    public List<String> getAllTypes() {
        List<String> frameworkTypes = new ArrayList<>();
        for (FrameworkType frameworkType : FrameworkType.values()) {
            frameworkTypes.add(frameworkType.name());
        }
        return frameworkTypes;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean nameAvailable(final String tech, final Long id) {
        if (tech == null) {
            return false;
        }
        Optional<Framework> framework = getByName(tech);
        if (id == null) {
            return !framework.isPresent();
        }
        return !framework.filter(value -> value.getId() != id).isPresent();
    }

}

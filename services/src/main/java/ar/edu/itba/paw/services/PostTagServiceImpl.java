package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.PostTag;
import ar.edu.itba.paw.models.PostTagType;
import ar.edu.itba.paw.persistence.PostTagDao;
import ar.edu.itba.paw.service.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostTagServiceImpl implements PostTagService {

    @Autowired
    private PostTagDao postTagDao;

    @Transactional (readOnly = true)
    @Override
    public List<PostTag> getByPost(long postId) {
        return postTagDao.getByPost(postId);
    }

    @Transactional (readOnly = true)
    @Override
    public List<PostTag> getAll(){
        return postTagDao.getAll();
    }

    @Transactional
    @Override
    public Optional<PostTag> insert(String tagName, long postId, PostTagType type) {
        return postTagDao.insert(tagName, postId, type);
    }

    @Transactional
    @Override
    public void delete(long tagId) {
        postTagDao.delete(tagId);

    }

    @Transactional
    @Override
    public void update(long postId, List<String> names, List<String> categories, List<String> types) {
        List<String> newTags = new ArrayList<>();
        newTags.addAll(names);
        newTags.addAll(categories);
        newTags.addAll(types);

        List<PostTag> postTagList = getByPost(postId);
        List<String> postsTags = postTagList.stream().map(PostTag::getTagName).collect(Collectors.toList());

        /* Deleted removed tags */

        for (PostTag postTag : postTagList) {
            if(!newTags.contains(postTag.getTagName())) {
                postTagDao.delete(postTag.getTagId());
            }
        }

        /* Adding new Tags, if needed */

        for (String name : names) {
            if (!postsTags.contains(name)) {
                postTagDao.insert(name, postId, PostTagType.tech_name);
            }
        }

        for (String category : categories) {
            if (!postsTags.contains(category)) {
                postTagDao.insert(category, postId, PostTagType.tech_category);
            }
        }

        for (String type : types) {
            if (!postsTags.contains(type)) {
                postTagDao.insert(type, postId, PostTagType.tech_type);
            }
        }
    }
}

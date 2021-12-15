package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.PostTag;
import ar.edu.itba.paw.models.PostTagType;

import java.util.List;
import java.util.Optional;

public interface PostTagService {

    List<PostTag> getByPost(long postId);

    Optional<PostTag> insert(String tagName, long postId, PostTagType type);
    void delete(long tagId);

    List<PostTag> getAll();

    void update(long postId, List<String> names, List<String> categories, List<String> types);
}

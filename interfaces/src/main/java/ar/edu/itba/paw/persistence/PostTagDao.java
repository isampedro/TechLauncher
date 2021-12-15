package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.PostTag;
import ar.edu.itba.paw.models.PostTagType;

import java.util.List;
import java.util.Optional;

public interface PostTagDao {

    List<PostTag> getByPost(long postId);

    Optional<PostTag> insert(String tagName, long postId, PostTagType type);

    void delete(long tagId);

    List<PostTag> getAll();

}

package ar.edu.itba.paw;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.CommentDao;
import ar.edu.itba.paw.persistence.CommentVoteDao;
import ar.edu.itba.paw.services.CommentServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static junit.framework.Assert.*;
import static org.postgresql.shaded.com.ongres.scram.common.ScramAttributes.USERNAME;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    private final long VOTE_ID=1;
    private final long USER_ID=1;
    private final long COMMENT_ID=1;
    private final int VOTE_UP=1;
    private final int VOTE_DOWN=-1;
    private final long COMMENT_VOTE_MOCK_ID=1;

    @InjectMocks
    private final CommentServiceImpl commentServiceImplMock = new CommentServiceImpl();

    @Mock
    private CommentVoteDao commentVoteDao;

    // Used by commentVoteDao
    @Mock
    private CommentDao commentDao;

    @Test
    public void testVoteUp() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_UP);
        commentVote.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(commentVoteDao.insert(COMMENT_ID,USER_ID,VOTE_UP)).thenReturn(commentVote);
        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_UP);

        // Assert
        assertTrue(answer.isPresent());
        assertEquals(USER_ID,answer.get().getUserId());
        assertEquals(COMMENT_ID,answer.get().getCommentId());
        assertEquals(VOTE_UP,answer.get().getVote());
    }

    @Test
    public void testVoteDown() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_DOWN);
        commentVote.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(commentVoteDao.insert(COMMENT_ID,USER_ID,VOTE_DOWN)).thenReturn(commentVote);
        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_DOWN);

        // Assert
        assertTrue(answer.isPresent());
        assertEquals(USER_ID,answer.get().getUserId());
        assertEquals(COMMENT_ID,answer.get().getCommentId());
        assertEquals(VOTE_DOWN,answer.get().getVote());
    }

    @Test
    public void testVoteUpTwice() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_UP);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.of(commentVote));

        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_UP);

        // Assert
        assertFalse(answer.isPresent());
    }

    @Test
    public void testVoteDownTwice() {
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_DOWN);
        commentVote.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.of(commentVote));

        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_DOWN);

        // Assert
        assertFalse(answer.isPresent());
    }

    @Test
    public void testVoteUpOnVotedDown() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_DOWN);
        commentVote.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.of(commentVote));
        CommentVote commentVote2 = new CommentVote();
        commentVote2.setComment(comment);
        commentVote2.setUser(user);
        commentVote2.setVote(VOTE_UP);
        commentVote2.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.update(VOTE_ID,VOTE_UP)).thenReturn(Optional.of(commentVote2));
        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_UP);

        // Assert
        assertTrue(answer.isPresent());
        assertEquals(USER_ID,answer.get().getUserId());
        assertEquals(COMMENT_ID,answer.get().getCommentId());
        assertEquals(VOTE_UP,answer.get().getVote());
    }

    @Test
    public void testVoteDownOnVotedUp() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        Comment comment = new Comment();
        comment.setCommentId(COMMENT_ID);
        User user = new User();
        user.setId(USER_ID);
        commentVote.setComment(comment);
        commentVote.setUser(user);
        commentVote.setVote(VOTE_UP);
        commentVote.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID)).thenReturn(Optional.of(commentVote));
        CommentVote commentVote2 = new CommentVote();
        commentVote2.setComment(comment);
        commentVote2.setUser(user);
        commentVote2.setVote(VOTE_DOWN);
        commentVote2.setCommentVoteId(VOTE_ID);
        Mockito.when(commentVoteDao.update(VOTE_ID,VOTE_DOWN)).thenReturn(Optional.of(commentVote2));
        // Act
        Optional<CommentVote> answer = commentServiceImplMock.vote(COMMENT_ID, USER_ID,VOTE_DOWN);

        // Assert
        assertTrue(answer.isPresent());
        assertEquals(USER_ID,answer.get().getUserId());
        assertEquals(COMMENT_ID,answer.get().getCommentId());
        assertEquals(VOTE_DOWN,answer.get().getVote());
    }
}

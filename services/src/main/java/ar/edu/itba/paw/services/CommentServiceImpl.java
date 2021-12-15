package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.ReportComment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.*;
import ar.edu.itba.paw.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private static final int VOTES_FOR_VERIFY =10;

    @Autowired
    private CommentDao cmts;

    @Autowired
    private VerifyUserDao verifyUserDao;

    @Autowired
    private CommentVoteDao cmtVotes;

    @Autowired
    private ReportCommentDao rc;

    private final long PAGE_SIZE = 5;

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> getById(long contentId) {
        return cmts.getById(contentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getCommentsWithoutReferenceByFramework(long frameworkId, long page) {
        return cmts.getCommentsWithoutReferenceByFramework(frameworkId, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getCommentsByUser(long userId, long page) {
        return cmts.getCommentsByUser(userId, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getCommentsCountByUser(long userId){
        return cmts.getCommentsCountByUser(userId);
    }

    @Transactional
    @Override
    public Comment insertComment(long frameworkId, long userId, String description, Long reference) {
        return cmts.insertComment(frameworkId, userId, description, reference);
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        cmts.deleteComment(commentId);
    }

    @Transactional
    @Override
    public Optional<Comment> changeComment(long commentId, String description) {
        return cmts.changeComment(commentId, description);
    }

    @Transactional
    @Override
    public Optional<CommentVote> vote(long commentId,long userId,int voteSign) {
        Optional<CommentVote> vote = cmtVotes.getByCommentAndUser(commentId,userId);
        if(vote.isPresent()){
            if(vote.get().getVote()==voteSign) {
                cmtVotes.delete(vote.get().getCommentVoteId());
                vote = Optional.empty();
            }
            else
                vote = cmtVotes.update(vote.get().getCommentVoteId(),voteSign);
        }else {
            vote = Optional.ofNullable(cmtVotes.insert(commentId, userId, voteSign));
        }
        Optional<Comment> comment = getById(commentId);

        if(voteSign > 0 && (!vote.isPresent() || vote.get().getVote()!=voteSign) && comment.isPresent()) {
            if(comment.get().getVotesUp()==(VOTES_FOR_VERIFY - 1)){
                Comment c = comment.get();
                Optional<User> user = Optional.ofNullable(c.getUser());
                if(user.isPresent() && user.get().isAllowMod() && !user.get().isAdmin() && !user.get().hasAppliedToFramework(c.getFrameworkId())) {
                    verifyUserDao.create(c.getUser(), c.getFramework(), c);
                }
            }
        }
        return vote;
    }


    @Transactional(readOnly = true)
    @Override
    public List<ReportComment> getAllReport(long page) {
        return rc.getAll(page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getAllReportsAmount() {
        return rc.getAllReportsAmount();
    }

    @Transactional
    @Override
    public void addReport(long commentId, long userId, String description) {
        rc.insert(commentId,userId,description);
    }

    @Transactional
    @Override
    public void acceptReport(long commentId) {
        cmts.deleteComment(commentId);
    }

    @Transactional
    @Override
    public void denyReport(long commentId) {
        rc.deleteReportByComment(commentId);
    }

    @Transactional
    @Override
    public void deleteReport(long reportId) {
        rc.delete(reportId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReportComment> getReportsByFrameworks( List<Long> frameworksIds, long page){
        return rc.getByFrameworks( frameworksIds, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getReportsAmountByFrameworks(List<Long> frameworksIds){
        return  rc.getReportsAmountByFrameworks( frameworksIds );
    }

}

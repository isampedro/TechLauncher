package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.CommentVote;

public class CommentVoteDTO {
    private String voter;
    private Integer value;

    public static CommentVoteDTO fromCommentVote (CommentVote vote) {
        CommentVoteDTO dto = new CommentVoteDTO();
        dto.setValue(vote.getVote());
        dto.setVoter(vote.getUser().getUsername());
        return dto;
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}

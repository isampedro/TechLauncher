package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.ReportComment;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {
    private String description;
    private String date;
    private SimpleUserDTO user;
    private Long votesUp;
    private Long votesDown;
    private Long referenceId;
    private List<CommentDTO> replies;
    private Long frameworkId;
    private String location;
    private String reportLocation;
    private String techName;
    private List<ReportDTO> reports;
    private List<CommentVoteDTO> votes;
    private long techId;

    public static CommentDTO fromComment(Comment comment, UriInfo uriInfo) {
        final CommentDTO dto = new CommentDTO();
        dto.description = comment.getDescription();
        dto.date = comment.getTimestamp().toLocaleString();
        dto.referenceId = comment.getReference();
        dto.user = SimpleUserDTO.fromUser(comment.getUser(), comment.getFramework(),uriInfo);
        dto.votesUp = comment.getVotesUp();
        dto.votesDown = comment.getVotesDown();
        if(comment.getReplies() != null)
            dto.replies = comment.getReplies().stream().map((Comment comment1) -> fromComment(comment1,uriInfo)).collect(Collectors.toList());
        if(!comment.getCommentVotes().isEmpty()) {
            dto.votes = comment.getCommentVotes().stream().map(CommentVoteDTO::fromCommentVote).collect(Collectors.toList());
        }
        dto.location = uriInfo.getBaseUriBuilder().path("techs/"+comment.getFrameworkId()+"/comment/"+comment.getCommentId()).build().toString();
        dto.reportLocation = uriInfo.getBaseUriBuilder().path("techs/"+comment.getFrameworkId()+"/comment/"+comment.getCommentId()+ "/report").build().toString();
        if (comment.getReports() != null) {
            dto.reports = comment.getReports().stream().map((ReportComment report) -> ReportDTO.fromReportComment(report, uriInfo)).collect(Collectors.toList());
        }
        return dto;
    }
    public static CommentDTO fromProfile(Comment comment, UriInfo uriInfo){
        final CommentDTO dto = new CommentDTO();
        dto.description = comment.getDescription();
        dto.date = comment.getTimestamp().toLocaleString();
        dto.location =uriInfo.getBaseUriBuilder().path("techs/"+comment.getFrameworkId()).build().toString();
        dto.techName = comment.getFrameworkName();
        dto.techId = comment.getFrameworkId();
        return dto;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SimpleUserDTO getUser() {
        return user;
    }

    public void setUser(SimpleUserDTO user) {
        this.user = user;
    }

    public Long getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(Long votesUp) {
        this.votesUp = votesUp;
    }

    public Long getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(Long votesDown) {
        this.votesDown = votesDown;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }
    public Long getFrameworkId() {
        return frameworkId;
    }

    public void setFrameworkId(Long frameworkId) {
        this.frameworkId = frameworkId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public List<ReportDTO> getReports() {
        return reports;
    }

    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public List<CommentVoteDTO> getVotes() {
        return votes;
    }

    public void setVotes(List<CommentVoteDTO> votes) {
        this.votes = votes;
    }

    public long getTechId() {
        return techId;
    }

    public void setTechId(long techId) {
        this.techId = techId;
    }
}

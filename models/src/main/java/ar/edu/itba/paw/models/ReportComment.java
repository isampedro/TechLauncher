package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comment_report", uniqueConstraints = @UniqueConstraint( columnNames = {"comment_id", "user_id"}))
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_report_report_id_seq")
    @SequenceGenerator(sequenceName = "comment_report_report_id_seq", name = "comment_report_report_id_seq", allocationSize = 1)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name="description", nullable = false)
    private String reportDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ReportComment() {

    }

    public long getCommentId() {
        return comment.getCommentId();
    }

    public long getFrameworkId() {
        return comment.getFrameworkId();
    }

    public long getUserId() {
        return user.getId();
    }

    public String getUserReporterName(){ return user.getUsername();}

    public String getCommentDescription() {
        return comment.getDescription();
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public Date getTimestamp() {
        return comment.getTimestamp();
    }

    public Long getReference() {
        return comment.getReference();
    }

    public String getFrameworkName() {
        return comment.getFrameworkName();
    }

    public String getUserNameOwner() {
        return comment.getUserName();
    }

    public FrameworkCategories getCategory() {
        return comment.getFramework().getCategory();
    }
    public String getCategoryAsString(){return comment.getFramework().getCategory().name();}


    public List<String> getReportsUserName(){
        List<String> list = new ArrayList<>();
        for (ReportComment rc:comment.getReports()) {
            list.add(rc.getUserReporterName());
        }
        return list;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
}

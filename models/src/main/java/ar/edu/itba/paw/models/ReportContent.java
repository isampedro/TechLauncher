package ar.edu.itba.paw.models;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content_report", uniqueConstraints = @UniqueConstraint( columnNames = {"content_id", "user_id"}))
public class ReportContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_report_report_id_seq")
    @SequenceGenerator(sequenceName = "content_report_report_id_seq", name = "content_report_report_id_seq", allocationSize = 1)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false)
    private String description;

    public ReportContent(){

    }

    public ReportContent( Content content, User user, String reportDescription){
        this.content = content;
        this.user = user;
        this.description = reportDescription;
    }


    public long getContentId() {
        return content.getContentId();
    }

    public long getFrameworkId() {
        return content.getFrameworkId();
    }

    public long getUserId() {
        return user.getId();
    }

    public String getTitle() {
        return content.getTitle();
    }

    public String getReportDescription() {
        return description;
    }

    public Date getTimestamp() {
        return content.getTimestamp();
    }

    public List<String> getReportsUserName() {
        List<String> list = new ArrayList<>();
        for (ReportContent rc:content.getReports()) {
            list.add(rc.getUserReporterName());
        }
        return list;
    }

    public String getFrameworkName() {
        return content.getFrameworkName();
    }

    public String getUserNameOwner() {
        return content.getUserName();
    }


    public FrameworkCategories getCategory() {
        return content.getCategory();
    }

    public String getCategoryAsString(){return content.getCategory().name();}

    public String getLink() {
        return content.getLink();
    }

    public ContentTypes getType() {
        return content.getType();
    }
    public String getTypeAsString(){return content.getType().name();}

    public Long getReportId() {
        return reportId;
    }

    public Content getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserReporterName() { return user.getUsername(); }
}

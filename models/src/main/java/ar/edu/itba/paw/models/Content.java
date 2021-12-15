package ar.edu.itba.paw.models;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_content_id_seq")
    @SequenceGenerator(sequenceName = "content_content_id_seq", name = "content_content_id_seq", allocationSize = 1)
    @Column(name = "content_id",nullable = false)
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_id",nullable = false)
    private Framework framework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String title;

    @Column(name = "tstamp",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(nullable = false)
    private String link;

    @Column(name = "type",nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentTypes type;

    /*this refers to the other relation mapped in ReportComment*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content",cascade = CascadeType.REMOVE)
    private List<ReportContent> reports;

    public Content(){

    }


    public Content(Framework framework, User user, String title, String link, ContentTypes type){
        this.framework = framework;
        this.user = user;
        this.title = title;
        this.link = link;
        this.type = type;
    }


    public long getContentId() {
        return contentId;
    }

    public long getFrameworkId() {
        return framework.getId();
    }

    public long getUserId() {
        return user.getId();
    }

    public String getTitle() {
        return title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getLink() {
        return link;
    }

    public ContentTypes getType() {
        return type;
    }

    public String getFrameworkName() {
        return framework.getName();
    }

    public FrameworkCategories getCategory() {
        return framework.getCategory();
    }


    public boolean isVerify() {
        return user.isVerify();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public boolean isReporter(String name){
        for (ReportContent rc: reports) {
            if(rc.getUserReporterName().equals(name))
                return true;
        }
        return false;
    }

    public String getUserName() {
        return user.getUsername();
    }

    public List<String> getReportersNames() {
        List<String> list = new ArrayList<>();
        for (ReportContent rc:reports) {
            list.add(rc.getUserReporterName());
        }
        return list;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setType(ContentTypes type) {
        this.type = type;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Framework getFramework() {
        return framework;
    }

    public User getUser() {
        return user;
    }

    public List<ReportContent> getReports() {
        return reports;
    }

    public List<Long> getReportsIds() {
        List<Long> list = new ArrayList<>();
        for (ReportContent rc:reports) {
            list.add(rc.getReportId());
        }
        return list;
    }

    public void setReports(List<ReportContent> reports) {
        this.reports = reports;
    }

}

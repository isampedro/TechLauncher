package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Content;
import ar.edu.itba.paw.models.ReportContent;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class ContentDTO {
    private String location;
    private String reportLocation;
    private SimpleUserDTO user;
    private String date;
    private String title;
    private String link;
    private String type;
    private String techName;
    private List<ReportDTO> reports;
    private long techId;

    public static ContentDTO fromContent(Content content, UriInfo uriInfo) {
        final ContentDTO dto = new ContentDTO();
        dto.date = content.getTimestamp().toLocaleString();
        dto.link = content.getLink();
        dto.user = SimpleUserDTO.fromUser(content.getUser(), content.getFramework(),uriInfo);
        dto.title = content.getTitle();
        dto.type = content.getType().name();
        dto.location = uriInfo.getBaseUriBuilder().path("techs/"+content.getFrameworkId()+"/content/"+content.getContentId()).build().toString();
        dto.reportLocation = uriInfo.getBaseUriBuilder().path("techs/"+content.getFrameworkId()+"/content/"+content.getContentId()+"/report").build().toString();
        if (content.getReports() != null) {
            dto.reports = content.getReports().stream().map((ReportContent report) -> ReportDTO.fromReportContent(report, uriInfo)).collect(Collectors.toList());
        }
        return dto;
    }
    public static ContentDTO fromProfile(Content content,UriInfo uriInfo) {
        final ContentDTO dto = new ContentDTO();
        dto.date = content.getTimestamp().toLocaleString();
        dto.link = content.getLink();
        dto.title = content.getTitle();
        dto.type = content.getType().name();
        dto.location = uriInfo.getBaseUriBuilder().path("techs/"+content.getFrameworkId()).build().toString();
        dto.techName = content.getFrameworkName();
        dto.techId = content.getFrameworkId();
        return dto;
    }

    public SimpleUserDTO getUser() {
        return user;
    }

    public void setUser(SimpleUserDTO user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public long getTechId() {
        return techId;
    }

    public void setTechId(long techId) {
        this.techId = techId;
    }
}


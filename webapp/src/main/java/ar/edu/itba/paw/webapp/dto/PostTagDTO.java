package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.PostTag;

public class PostTagDTO {
    private String tagName;
    private String type;
    public static PostTagDTO fromPostTag( PostTag tag ){
        PostTagDTO dto = new PostTagDTO();
        dto.tagName = tag.getTagName();
        dto.type = tag.getType().name();
        return dto;
    }

    public static PostTagDTO fromString( String tag, String type ){
        PostTagDTO dto = new PostTagDTO();
        dto.tagName = tag;
        dto.type = type;
        return dto;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

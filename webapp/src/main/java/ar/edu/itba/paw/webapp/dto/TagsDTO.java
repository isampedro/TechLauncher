package ar.edu.itba.paw.webapp.dto;

import java.util.LinkedList;
import java.util.List;

public class TagsDTO {
    List<PostTagDTO> names, categories, types;

    public TagsDTO (){
        names = new LinkedList<>();
        categories = new LinkedList<>();
        types = new LinkedList<>();
    }

    public void addName(PostTagDTO name){
        names.add(name);
    }

    public void addCategory( PostTagDTO category ){
        categories.add(category);
    }

    public void addType( PostTagDTO type ){
        types.add(type);
    }

    public List<PostTagDTO> getNames() {
        return names;
    }

    public void setNames(List<PostTagDTO> names) {
        this.names = names;
    }

    public List<PostTagDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<PostTagDTO> categories) {
        this.categories = categories;
    }

    public List<PostTagDTO> getTypes() {
        return types;
    }

    public void setTypes(List<PostTagDTO> types) {
        this.types = types;
    }
}

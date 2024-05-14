package com.jessm.ntusocietybrowser;

public class SearchResult {
    private String _title;
    private String _description;
    private String _image;
    private String _id;

    public SearchResult() { }

    public SearchResult(String title, String description, String image, String id) {
        this._title = title;
        this._description = description;
        this._image = image;
        this._id = id;
    }

    public String getTitle() { return _title; }

    public void setTitle(String title) { this._title = title; }

    public String getDescription() { return _description; }

    public void setDescription(String description) { this._description = description; }
    public String getImage() { return _image; }
    public void setImage(String image) { this._image = image; }

    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }
}

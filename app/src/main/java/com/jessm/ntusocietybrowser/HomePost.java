package com.jessm.ntusocietybrowser;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/*
 * Class used for creating new home posts within HomeFragment
 */
public class HomePost
{
    private String _title;
    private String _subtitle;
    private String _content;
    private String _image;
    private HomePostType _type;

    private Date _date;
    @ServerTimestamp
    private Date _timestamp;

    public HomePost(String title, String subtitle, String content, String image, String type, Date date) {
        _title = title;
        _subtitle = subtitle;
        _content = content;
        _image = image;
        _type = HomePostType.valueOf(type);
        _date = date;
    }


    public String getTitle() { return _title; }

    public void setTitle(String _title) { this._title = _title; }

    public String getSubtitle() { return _subtitle; }

    public void setSubtitle(String subtitle) { this._subtitle = subtitle; }

    public String getContent() { return _content; }

    public void setContent(String _content) { this._content = _content; }

    public String getImage() { return _image; }

    public void setImage(String _image) { this._image = _image; }

    public HomePostType getType() { return _type; }

    public void setType(HomePostType _type) { this._type = _type; }

    public Date getDate() { return _date; }

    public void setDate(Date _date) { this._date = _date; }

    public Date getTimestamp() { return _timestamp; }

    public void setTimestamp(Date timestamp) { this._timestamp = timestamp; }
}

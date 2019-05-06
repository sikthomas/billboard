package com.glac.academics;

import com.glac.ecommerce.PostId;

import java.util.Date;

/**
 * Created by mwarachael on 2/6/2019.
 */

public class MyClassPostList extends com.glac.ecommerce.PostId{
    public String title,regno,imageUrl,post,user_id;
    public Date timeStamp;

    public MyClassPostList() {
    }

    public MyClassPostList(String title, String regno, String imageUrl, String post, String user_id, Date timeStamp) {
        this.title = title;
        this.regno = regno;
        this.imageUrl = imageUrl;
        this.post = post;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

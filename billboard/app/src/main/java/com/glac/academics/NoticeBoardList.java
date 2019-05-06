package com.glac.academics;

import com.glac.ecommerce.PostId;

import java.util.Date;

/**
 * Created by mwarachael on 2/10/2019.
 */

public class NoticeBoardList extends com.glac.ecommerce.PostId {
    private String post,user_id,fileUri;
    private Date timeStamp;

    public NoticeBoardList() {
    }

    public NoticeBoardList(String post, String user_id, String fileUri, Date timeStamp) {
        this.post = post;
        this.user_id = user_id;
        this.fileUri = fileUri;
        this.timeStamp = timeStamp;
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

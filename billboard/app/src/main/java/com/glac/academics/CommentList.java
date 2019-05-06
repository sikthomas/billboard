package com.glac.academics;

import com.glac.ecommerce.PostId;

import java.util.Date;

/**
 * Created by mwarachael on 2/9/2019.
 */

public class CommentList extends com.glac.ecommerce.PostId {
    public String comment,regno;
    public Date timeStamp;

    public CommentList() {


    }

    public CommentList(String comment, String regno, Date timeStamp) {
        this.comment = comment;
        this.regno = regno;
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

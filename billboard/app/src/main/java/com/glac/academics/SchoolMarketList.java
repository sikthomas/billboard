package com.glac.academics;

import com.glac.ecommerce.PostId;

import java.util.Date;

/**
 * Created by mwarachael on 2/6/2019.
 */

public class SchoolMarketList extends com.glac.ecommerce.PostId{
    public String fullname,regno,title,desc,imageUrl,phone,user_id;
    private Date timeStamp;

    public SchoolMarketList() {
    }

    public SchoolMarketList(String fullname, String regno, String title, String desc, String imageUrl, String phone, String user_id, Date timeStamp) {
        this.fullname = fullname;
        this.regno = regno;
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

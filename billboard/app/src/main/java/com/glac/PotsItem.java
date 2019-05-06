package com.glac;

import java.util.Date;

/**
 * Created by mwarachael on 1/28/2019.
 */

public class PotsItem extends PostId {
    String price,phone,location,email,title,county,user_id,desc_val,image_url;
    Date timeStamp;

    public PotsItem(){

    }

    public PotsItem(String price, String phone, String location, String email, String title, String county, String user_id, String desc_val, String image_url, Date timeStamp) {
        this.price = price;
        this.phone = phone;
        this.location = location;
        this.email = email;
        this.title = title;
        this.county = county;
        this.user_id = user_id;
        this.desc_val = desc_val;
        this.image_url = image_url;
        this.timeStamp = timeStamp;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDesc_val() {
        return desc_val;
    }

    public void setDesc_val(String desc_val) {
        this.desc_val = desc_val;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

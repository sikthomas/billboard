package com.glac.transport;

import java.util.Date;

/**
 * Created by mwarachael on 2/25/2019.
 */

public class ClientList extends com.glac.ecommerce.PostId {

    private String fullname,county,location,plate,category,phone,user_id,availability;
    private Date timeStamp;

    public ClientList() {
    }

    public ClientList(String fullname, String county, String location, String plate, String category, String phone, String user_id, String availability, Date timeStamp) {
        this.fullname = fullname;
        this.county = county;
        this.location = location;
        this.plate = plate;
        this.category = category;
        this.phone = phone;
        this.user_id = user_id;
        this.availability = availability;
        this.timeStamp = timeStamp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

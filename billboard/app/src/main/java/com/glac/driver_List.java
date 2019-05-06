package com.glac;

public class driver_List extends postID {
    public String fname,lname,id,phone,county,larea,plate;

    public driver_List() {
    }

    public driver_List(String fname, String lname, String id, String phone, String county, String larea, String plate) {
        this.fname = fname;
        this.lname = lname;
        this.id = id;
        this.phone = phone;
        this.county = county;
        this.larea = larea;
        this.plate = plate;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLarea() {
        return larea;
    }

    public void setLarea(String larea) {
        this.larea = larea;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}

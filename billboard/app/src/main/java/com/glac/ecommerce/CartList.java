package com.glac.ecommerce;

import java.util.Date;

/**
 * Created by mwarachael on 1/29/2019.
 */

public class CartList extends PostId{
    private  String title,price;
    private Date timeStamp;

    public CartList(){

    }

    public CartList(String title, String price, Date timeStamp) {
        this.title = title;
        this.price = price;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

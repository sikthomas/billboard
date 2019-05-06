package com.glac.ecommerce.Carts;

import com.glac.ecommerce.PostId;

import java.util.Date;

/**
 * Created by mwarachael on 1/29/2019.
 */

public class CartList extends com.glac.ecommerce.PostId {
    private String title,price;
    private int id;

    public CartList() {

    }

    public CartList(String title, String price, int id) {
        this.title = title;
        this.price = price;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

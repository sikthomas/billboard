package com.glac.ecommerce.Carts;

/**
 * Created by mwarachael on 2/24/2019.
 */

public class Constants {
    //columns

    static final String ROW_ID = "id";
    static final String TITLE = "title";
    static final String PRICE = "price";

    //DB PROPERTIES
    static final String DB_NAME = "db";
    static final String DB_TB = "carttable";
    static final int DB_VERSION =1;

    //CREATING THE TABLE
    static final String CREATE_DB = "CREATE TABLE carttable (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, price TEXT NOT NULL);";

    static final String DROP_TB = "DROP TABLE IF EXISTS "+DB_TB;
}

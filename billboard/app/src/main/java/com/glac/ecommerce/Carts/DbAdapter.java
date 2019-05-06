package com.glac.ecommerce.Carts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mwarachael on 2/24/2019.
 */

public class DbAdapter {

    private Context context;
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;

        dbHelper = new DbHelper(context);
    }

    public void openDb(){
        try
        {
            db = dbHelper.getWritableDatabase();

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void closeDb(){
        try
        {

            dbHelper.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean add(String title, String price){
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.TITLE,title);
            contentValues.put(Constants.PRICE,price);

            long result = db.insert(Constants.DB_TB,Constants.ROW_ID,contentValues);

            if (result>0){
                return true;
            }


        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public Cursor retrieve(){
        String [] columns ={Constants.ROW_ID,Constants.PRICE,Constants.TITLE};

        Cursor c = db.query(Constants.DB_TB,columns,null,null,null,null,null);
        return c;
    }

    public boolean remove(int id){
        try {
            int result = db.delete(Constants.DB_TB,Constants.ROW_ID+" =?",new String[]{String.valueOf(id)});
            if (result>0){
                return true;
            }

        }catch (SQLException e)
        {

            e.printStackTrace();
        }
        return false;
    }
}

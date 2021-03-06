package com.glac.ecommerce.Carts;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mwarachael on 2/24/2019.
 */

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try
        {

            db.execSQL(Constants.CREATE_DB);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Constants.DROP_TB);
        onCreate(db);

    }
}

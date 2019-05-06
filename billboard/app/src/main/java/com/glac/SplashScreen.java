package com.glac;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener{


    private ProgressBar bar;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);bar = (ProgressBar)findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        final TextView textView = (TextView)findViewById(R.id.textLoading);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnection();
            }
        },2000);
    }

    @Override
    public void onClick(View v) {

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");


        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isOnline() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");


        } catch (Exception e) {
            return false;
        }
    }
    public void checkConnection(){
        if(isOnline()){
            new AlertDialog.Builder(SplashScreen.this)
                    .setTitle("Network Connection")
                    .setMessage("Opps! You have no internet connection!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.dismiss();
                        }
                    }).show();

        }else {

            startActivity(new Intent(SplashScreen.this,Login.class));
            finish();
        }
    }

}

package com.glac.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserProfile extends AppCompatActivity {
    private TextView mEmail,mPhone,mCounty,mLocation,mId;
    private ImageView mImageProf;
    private FloatingActionButton mFloat;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,fullname,email,phone,county,location,id;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        toolbar = (Toolbar) findViewById(R.id.main_toolbar1);
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();
        mImageProf=(ImageView)findViewById(R.id.UserProfileImage);
        mEmail=(TextView)findViewById(R.id.tvEmailprof);
        mPhone=(TextView)findViewById(R.id.tvPhoneProf);
        mCounty=(TextView)findViewById(R.id.tvCountyProf);
        mLocation=(TextView)findViewById(R.id.tvLocationProf);
        mId=(TextView)findViewById(R.id.IdProf);
        mFloat=(FloatingActionButton)findViewById(R.id.floatProf);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar1);
        toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.main_collapsing1);
        setSupportActionBar(toolbar);

        final  String image_url = getIntent().getExtras().getString("image_url");

        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this,MyAccountASettings.class));
            }
        });

        mImageProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(UserProfile.this);
                dialog.setContentView(R.layout.profile_dialog_2);
                dialog.setCancelable(true);
                //dialog.setTitle(username);

                final ImageView userImage_dialog = (ImageView) dialog.findViewById(R.id.imageUserProfile);
                ImageView save = (ImageView) dialog.findViewById(R.id.image_save);
                ImageView edit = (ImageView) dialog.findViewById(R.id.image_edit_pic);
                final TextView userNameProfile = (TextView) dialog.findViewById(R.id.userNameProfile);

                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String username = task.getResult().getString("fname").toLowerCase();
                                String susername = task.getResult().getString("lname").toLowerCase();

                                if (image_url != null){


                                    try{
                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions.centerCrop();
                                        requestOptions.placeholder(R.color.lightGrray);

                                        Glide.with(UserProfile.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(userImage_dialog);

                                    }catch (Exception e){
                                        Toast.makeText(UserProfile.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //Picasso.with(UserProfile.this).load(image_url).fit().into(userImage_dialog);
                                userNameProfile.setText("@"+username+susername);
                            }
                        }

                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BitmapDrawable draw = (BitmapDrawable) userImage_dialog.getDrawable();
                        Bitmap bitmap = draw.getBitmap();
                        if (userImage_dialog!=null) {

                            FileOutputStream outStream = null;
                            File sdCard = Environment.getExternalStorageDirectory();
                            File dir = new File(sdCard.getAbsolutePath() + "/GlacMarketApp");
                            dir.mkdirs();
                            String fileName = String.format("%d.jpg", System.currentTimeMillis());
                            File outFile = new File(dir, fileName);

                            try {
                                outStream = new FileOutputStream(outFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                outStream.flush();
                                outStream.close();
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(Uri.fromFile(dir));
                                sendBroadcast(intent);
                                Toast.makeText(getApplicationContext(), "Saved to gallery", Toast.LENGTH_SHORT).show();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"oops!:\nCannot download null image...!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //startActivity(new Intent(UserProfile.this,ProfileChange.class));
                        Intent intent = new Intent(view.getContext(), ProfileChange.class);
                        intent.putExtra("image_url",image_url);
                        view.getContext().startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        //getting user details
        gettingUserDetails(toolbarLayout);
    }

    private void gettingUserDetails(final CollapsingToolbarLayout collapsingToolbarLayout) {

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String fname=task.getResult().getString("fname");
                        String lname=task.getResult().getString("lname");
                        fullname=fname.toUpperCase()+ " "+lname.toUpperCase();

                        email=task.getResult().getString("email");
                        phone=task.getResult().getString("phone");
                        id=task.getResult().getString("id_number");
                        county=task.getResult().getString("county");
                        location=task.getResult().getString("local_area");
                        String image_Url=task.getResult().getString("imageUrl");


                        mId.setText("ID Number: "+id);
                        mEmail.setText("Email: "+email);
                        mEmail.setTextColor(R.color.blue);
                        mPhone.setText("Phone Number: "+phone);
                        mPhone.setTextColor(R.color.lightgreen);
                        mCounty.setText("County: "+county +" County");
                        mLocation.setText("Residential Area: "+location);
                        collapsingToolbarLayout.setTitle(fullname);

                        //setting image

                        try{
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop();
                            requestOptions.placeholder(R.color.lightGrray);

                            Glide.with(UserProfile.this).applyDefaultRequestOptions(requestOptions).load(image_Url).into(mImageProf);

                        }catch (Exception e){
                            Toast.makeText(UserProfile.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }

            }
        });

    }
}

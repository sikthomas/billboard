package com.glac.academics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.R;
import com.glac.account.ProfileChange;
import com.glac.account.UserProfile;
import com.glac.ecommerce.PostItemDesc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SchoolItemDesc extends AppCompatActivity {
    private FloatingActionButton mCalling,mWhatsApping;
    private String title,username,regno,user_profile_url,image_posted_url,description,user_id,phone,owner_user_id;
    private TextView mUsername,mRegno,mDesc,mTitlePost,mPhone;
    private CircleImageView mUserProfile;
    private ImageView mImagePosted;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();

        setContentView(R.layout.activity_school_item_desc);
        mCalling = (FloatingActionButton)findViewById(R.id.floating_user_call);
        mWhatsApping = (FloatingActionButton)findViewById(R.id.flaoting_user_whatsapp);
        mUsername =(TextView)findViewById(R.id.tv_user_fullname);
        mRegno = (TextView)findViewById(R.id.tv_user_regno);
        mDesc = (TextView)findViewById(R.id.tv_user_description);
        mTitlePost = (TextView)findViewById(R.id.tv_user_title_schooldesc);
        mPhone = (TextView)findViewById(R.id.tv_user_phone);
        mImagePosted = (ImageView)findViewById(R.id.image_posted_user);
        mUserProfile =(CircleImageView)findViewById(R.id.image_user_profile);

        //getting the values from the clicked position.
        title =getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("desc");
        phone = getIntent().getExtras().getString("phone");
        image_posted_url= getIntent().getExtras().getString("image_url");
        regno = getIntent().getExtras().getString("regno");
        owner_user_id = getIntent().getExtras().getString("user_id");
        //geting user detsils

        firebaseFirestore.collection("Users").document(owner_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String fname = task.getResult().getString("fname");
                        String lname = task.getResult().getString("lname");
                        user_profile_url = task.getResult().getString("imageUrl");
                        username = fname+" "+lname;
                        mUsername.setText(username);

                        //setting vslues now
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.centerCrop();
                        requestOptions.placeholder(R.color.lightgray);
                        Glide.with(SchoolItemDesc.this).load(user_profile_url).into(mUserProfile);
                    }
                }

            }
        });

        //setting values to other items
        setTitle(title);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.placeholder(R.color.lightgray);
        Glide.with(SchoolItemDesc.this).load(image_posted_url).into(mImagePosted);
        mPhone.setText(phone);
        mTitlePost.setText(title);
        mDesc.setText(description);
        mRegno.setText(regno);

        //setting buttons for calling and whatsApping

        mCalling.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent sIntent = new Intent(Intent.ACTION_CALL, Uri


                        .parse("tel:"+phone));


                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



                startActivity(sIntent);
            }
        });
        mWhatsApping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(v,phone);
            }
        });

        mUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users").document(owner_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String image_url = task.getResult().getString("imageUrl");
                                // custom dialog
                                final Dialog dialog = new Dialog(SchoolItemDesc.this);
                                dialog.setContentView(R.layout.owner_profile_info);
                                dialog.setCancelable(true);
                                //dialog.setTitle(username);

                                final ImageView userImage_dialog = (ImageView) dialog.findViewById(R.id.igm_profile_info);
                                Button save = (Button) dialog.findViewById(R.id.btn_save_to);

                                RequestOptions requestOptions1 = new RequestOptions();
                                requestOptions1.fitCenter();
                                requestOptions1.placeholder(R.color.lightgray);
                                Glide.with(SchoolItemDesc.this).load(image_url).into(userImage_dialog);


                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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



                                dialog.show();
                            }
                        }
                    }
                });

            }
        });


    }


    public void openWhatsApp(View view,String toNumber){
        PackageManager pm=getPackageManager();
        try {

            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(SchoolItemDesc.this,"Pease install whatsApp Messenger to use this option...",Toast.LENGTH_LONG).show();

        }
    }
}

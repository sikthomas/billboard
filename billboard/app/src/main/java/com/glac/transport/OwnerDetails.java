package com.glac.transport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnerDetails extends AppCompatActivity {
    private String county,phone,name,location,plate,category,postID,visibility,owner_user_id,availability,switchSeen;
    private TextView mDetails,mSeenTv;
    private FloatingActionButton mCall;
    private Button mDecline,mAccept;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth auth;
    private String user_id;
    private Switch mAvailability;
    private CircleImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_details);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        mDetails = (TextView)findViewById(R.id.tv_owner_transport);
        mSeenTv = (TextView)findViewById(R.id.tv_availability_owner);
        mDecline = (Button)findViewById(R.id.btn_decline);
        mAccept = (Button)findViewById(R.id.btn_approve);
        mCall = (FloatingActionButton)findViewById(R.id.floating_call_transport);
        mAvailability = (Switch)findViewById(R.id.switch_availability);
        mImageView = (CircleImageView)findViewById(R.id.profileImage_transport_owner);

        visibility = getIntent().getExtras().getString("visibility");
        if (visibility.equals("yes"))
        {
            mDecline.setVisibility(View.GONE);
            mAccept.setVisibility(View.GONE);
        }

        //getting the passed values
        county = getIntent().getExtras().getString("county");
        phone = getIntent().getExtras().getString("phone");
        name = getIntent().getExtras().getString("fullname");
        location = getIntent().getExtras().getString("location");
        plate = getIntent().getExtras().getString("plate");
        category = getIntent().getExtras().getString("category");
        postID = getIntent().getExtras().getString("postID");
        owner_user_id = getIntent().getExtras().getString("owner_user_id");
        availability = getIntent().getExtras().getString("availability");
        switchSeen = getIntent().getExtras().getString("switchSeen");

        if (availability.equals("Available")){
            mSeenTv.setText("Available");
        }else {
            mSeenTv.setText("Not Available");
        }

        //setting profile image
        try {

            firebaseFirestore.collection("Users").document(owner_user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                String imageUrl = task.getResult().getString("imageUrl");
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                requestOptions.placeholder(R.color.lightgray);
                                Glide.with(OwnerDetails.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mImageView);
                            }
                        }
                    });

        }catch (Exception e){

        }

        //SETTING THE AVAILABILITY BUTTON
        if (availability.equals("Available")){
            mAvailability.setChecked(true);
        }else {
            mAvailability.setChecked(false);
        }

        //SHOWING THE SWITCH BUTTON
        if (switchSeen.equals("Client")){
        if (owner_user_id.equals(user_id)){
            mAvailability.setVisibility(View.VISIBLE);
        }else {
            mAvailability.setVisibility(View.GONE);

        }
        }else {
            mAvailability.setVisibility(View.GONE);
        }

        mAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingAvailabilityOn(postID);
                } else {
                    settingAvailabilityOff(postID);
                }
            }
        });

        //SETTING USER DETAILS VALUES TO THE TEXTVIEW
        mDetails.setText("Full Name: "+name+"\nPhone Number: "+phone+"\nCounty: "+county+"\nLocation: "+location+"\nNumber Plate: "+plate+"\nVehicle Category: "+category);


        setTitle(name);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }

        //calling
        mCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sIntent);
            }
        });

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(OwnerDetails.this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to proceed for the approval?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            submiting(plate,name,county,location,category,phone,postID,user_id);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            }
        });

        mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(OwnerDetails.this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to decline?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            droping(postID);
                            sendingDeclinedSms(phone,name,plate,category);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            }
        });


    }
    private void submiting(String NPlate, final String fullnsme, String County, String Location, String Cat, final String phone, final String postID,String userID){
        Map<String , Object > stringMap = new HashMap<>();
        stringMap.put("fullname",fullnsme);
        stringMap.put("plate",NPlate);
        stringMap.put("county",County);
        stringMap.put("location",Location);
        stringMap.put("category",Cat);
        stringMap.put("phone",phone);
        stringMap.put("user_id",userID);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());
        stringMap.put("availability","Unavailable");


    }firebaseFirestore.collection("Vehicles").document("AllVehicles").collection("Approved").add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {

            if (task.isSuccessful()){
                sendingSms(phone,fullnsme,plate,category);
                droping(postID);
                new android.support.v7.app.AlertDialog.Builder(OwnerDetails.this)
                        .setTitle("Success")
                        .setMessage("Successfully approved. Approval message has been sent to client")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }else {
                Toast.makeText(OwnerDetails.this, "Failed..\nSomething went wrong "+task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    });

    private void sendingSms(String phone,String fullname,String plate,String category){

        String sms = "Hi am "+fullname+", \nYour request has been accepted for the vehicle of the following details:\n\nNumber plate: "+plate+"\nCategory: "+category+"\n\nDefault Message from GLAC App";
        String phoneNum = phone;
        if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
            if(checkPermission()) {

//Get the default SmsManager//

                SmsManager smsManager = SmsManager.getDefault();

//Send the SMS//

                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
            }else {
                Toast.makeText(OwnerDetails.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendingDeclinedSms(String phone,String fullname,String plate,String category){

        String sms = "DECLINED:\n\nHi am "+fullname+", \nYour request for the vehicle of the following details:\n\nNumber plate: "+plate+"\nCategory: "+category+"\n\nDefault Message from GLAC International has been declined";
        String phoneNum = phone;
        if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
            if(checkPermission()) {

//Get the default SmsManager//

                SmsManager smsManager = SmsManager.getDefault();

//Send the SMS//

                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
            }else {
                Toast.makeText(OwnerDetails.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(OwnerDetails.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(OwnerDetails.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(OwnerDetails.this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void droping(String postID){
        firebaseFirestore.collection("Vehicles").document("AllVehicles").collection("Requests").document(postID).delete();

    }

    private void settingAvailabilityOn(String postID) {
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("availability", "Available");
        firebaseFirestore.collection("Vehicles").document("AllVehicles").collection("Approved").document(postID).update(stringMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OwnerDetails.this, "Availability: ON", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OwnerDetails.this, "Something went wrong:\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void settingAvailabilityOff(String postID){
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("availability","Unavailable");
        firebaseFirestore.collection("Vehicles").document("AllVehicles").collection("Approved").document(postID).update(stringMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(OwnerDetails.this, "Availability: OFF", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(OwnerDetails.this, "Something went wrong:\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });





    }
}

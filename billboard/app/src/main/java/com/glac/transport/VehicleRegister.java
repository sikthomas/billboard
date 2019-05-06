package com.glac.transport;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.glac.R;
import com.glac.ecommerce.PostItemDesc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VehicleRegister extends AppCompatActivity {
    private TextInputEditText mPlate;
    private Button mSubmit;
    private TextView mDetails;
    private Spinner mCat;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_register);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);

        mPlate = (TextInputEditText)findViewById(R.id.edt_vehicle_number);
        mSubmit = (Button)findViewById(R.id.btn_vehicle);
        mDetails = (TextView)findViewById(R.id.tv_vehicle_person);
        mCat = (Spinner)findViewById(R.id.sp_vehicles);

        mCat.setSelection(0);
        settingDetails(user_id,mDetails);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String plate = mPlate.getText().toString().trim();
                final String category = mCat.getSelectedItem().toString();

                if (TextUtils.isEmpty(plate)){
                    Toast.makeText(VehicleRegister.this, "Enter number plate...", Toast.LENGTH_SHORT).show();
                }
                else if (mCat.getSelectedItemPosition()== 0){
                    Toast.makeText(VehicleRegister.this, "Pick vehicle category...", Toast.LENGTH_SHORT).show();
                }else {
                    mPlate.setText("");
                    progressDialog.setMessage("Submitting details...");
                    progressDialog.show();
                    firebaseFirestore.collection("Users").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            String fname = task.getResult().getString("fname");
                                            String lname = task.getResult().getString("lname");
                                            String county = task.getResult().getString("county");
                                            String location = task.getResult().getString("local_area");
                                            String phone = task.getResult().getString("phone");

                                            submiting(plate,fname+" "+lname,county,location,category,phone,user_id);
                                            sendingSms(fname+" "+lname,plate,category);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void settingDetails(String UserId, final TextView textView){
        firebaseFirestore.collection("Users").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String fname = task.getResult().getString("fname");
                                String lname = task.getResult().getString("lname");
                                String county = task.getResult().getString("county");
                                String location = task.getResult().getString("larea");
                                String phone = task.getResult().getString("phone");
                                textView.setText("Personal Details:\n\nFull Name: "+fname+" "+lname+"\nCounty: "+county+"\nLocal Area: "+location+"\nPhone Number: "+phone);

                            }
                        }
                    }
                });
    }

    private void submiting(String NPlate,String fullnsme, String County, String Location,String Cat,String phone,String user_ID){
        Map<String , Object > stringMap = new HashMap<>();
        stringMap.put("fullname",fullnsme);
        stringMap.put("plate",NPlate);
        stringMap.put("county",County);
        stringMap.put("location",Location);
        stringMap.put("category",Cat);
        stringMap.put("phone",phone);
        stringMap.put("user_id",user_ID);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());
        stringMap.put("availability","Unavailable");

        firebaseFirestore.collection("Vehicles").document("AllVehicles").collection("Requests").add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()){
                    new android.support.v7.app.AlertDialog.Builder(VehicleRegister.this)
                            .setTitle("Success")
                            .setMessage("Successfully registered. Please wait for approval")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else {
                    Toast.makeText(VehicleRegister.this, "Failed..\nSomething went wrong "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendingSms(String fullname,String plate,String category){

        String sms = "Hi am "+fullname+", \nI was kindly requesting for vehicle approval with the following details:\n\nNumber plate: "+plate+"\nCategory: "+category+"\n\nDefault Message from GLAC App";
        String phoneNum = "0716698513";
        if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
            if(checkPermission()) {

//Get the default SmsManager//

                SmsManager smsManager = SmsManager.getDefault();

//Send the SMS//

                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
            }else {
                Toast.makeText(VehicleRegister.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(VehicleRegister.this, Manifest.permission.SEND_SMS);
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

                    Toast.makeText(VehicleRegister.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(VehicleRegister.this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}

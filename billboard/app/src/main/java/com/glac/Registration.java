package com.glac;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private EditText mFname,mLname,mId,mPhone,mLocalArea,mPlate;
    private Spinner mCounty;
    private Button mSubmit;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    String user_id,fname,lname,id,phone,county,larea,plate;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        mFname = (EditText)findViewById(R.id.edt_fname_user_transport);
        mLname = (EditText)findViewById(R.id.edt_lname_user_transport);
        mId = (EditText)findViewById(R.id.edt_id_user_transport);
        mPhone = (EditText)findViewById(R.id.edt_phone_user_transport);
        mCounty = (Spinner) findViewById(R.id.sp_registration);
        mLocalArea = (EditText)findViewById(R.id.edt_localarea_user_transport);
        mPlate = (EditText)findViewById(R.id.edt_plate_transport);
        mSubmit = (Button)findViewById(R.id.btn_register_trans);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = mFname.getText().toString().trim();
                lname = mLname.getText().toString().trim();
                id = mId.getText().toString().trim();
                phone = mPhone.getText().toString().trim();
                county = mCounty.getSelectedItem().toString();
                larea = mLocalArea.getText().toString().trim();
                plate =mPlate.getText().toString().trim();

                if (TextUtils.isEmpty(fname)){
                    mFname.setError("Enter your first name...");
                }else if (TextUtils.isEmpty(lname)){

                    mLname.setError("Enter your last name...");
                }else if (TextUtils.isEmpty(id)){

                    mId.setError("Enter your ID...");
                }else if (TextUtils.isEmpty(phone)){

                    mPhone.setError("Enter your phone number...");
                }else if (TextUtils.isEmpty(larea)){

                    mLocalArea.setError("Enter your local area...");
                }else if (TextUtils.isEmpty(plate)){

                    mPlate.setError("Enter your vehicle number plate...");
                }else if (mCounty.getSelectedItemPosition() == 0){

                    Toast.makeText(Registration.this, "Please select your county...", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Submiting your user details...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    detailsUpload(fname,lname,id,phone,county,larea,plate,v);

                }
            }
        });


    }

    private void detailsUpload( String fname, String lname, String id, String phone, String county, String larea, String plate,final View view) {

        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("fname",fname);
        detailsMap.put("lname",lname);
        detailsMap.put("id",id);
        detailsMap.put("phone",phone);
        detailsMap.put("county",county);
        detailsMap.put("larea",larea);
        detailsMap.put("plate",plate);

        firebaseFirestore.collection("Drivers").document(user_id).set(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(Registration.this, "Successfully registered...", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }else {
                    String error = task.getException().toString();
                    Snackbar.make(view, error, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                progressDialog.dismiss();

            }
        });


    }
}

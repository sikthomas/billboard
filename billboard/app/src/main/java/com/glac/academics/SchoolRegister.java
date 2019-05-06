package com.glac.academics;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SchoolRegister extends AppCompatActivity {
    private Spinner mUniverisities,mCourses,mYos;
    private String user_id,fname,lname,phone,regno,university,yos,course,fullname;
    private Button mSubmit;
    private EditText mRegno;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_register);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        mUniverisities = (Spinner)findViewById(R.id.sp_University);
        mCourses = (Spinner)findViewById(R.id.sp_Course);
        mYos = (Spinner)findViewById(R.id.sp_Yos);
        mSubmit = (Button)findViewById(R.id.btn_schoolRegistration);
        mRegno = (EditText)findViewById(R.id.edt_regno);

        //initializing values to all spinners
        mUniverisities.setSelection(0);
        mYos.setSelection(0);
        mCourses.setSelection(0);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialising them strings
                regno = mRegno.getText().toString().trim();
                university =mUniverisities.getSelectedItem().toString();
                yos = mYos.getSelectedItem().toString();
                course = mCourses.getSelectedItem().toString();

                //checkin'
                if (TextUtils.isEmpty(regno)){
                    mRegno.setError("Enter a valid registration number!");
                }else if (mYos.getSelectedItemPosition() == 0){
                    Toast.makeText(SchoolRegister.this, "Please select Year of Study!", Toast.LENGTH_SHORT).show();
                }else if (mUniverisities.getSelectedItemPosition() == 0){
                    Toast.makeText(SchoolRegister.this, "Please select University!", Toast.LENGTH_SHORT).show();
                }else if (mCourses.getSelectedItemPosition() == 0){
                    Toast.makeText(SchoolRegister.this, "Please select your course!", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Uploading your details...");
                    progressDialog.show();

                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                if (task.getResult().exists()){
                                    fname = task.getResult().getString("fname");
                                    lname = task.getResult().getString("lname");
                                    phone = task.getResult().getString("phone");

                                    //setting fullname
                                    fullname = "@"+fname+lname;
                                    detailsSubmiting(fullname.toLowerCase(),phone,university,course,yos,regno.toUpperCase());

                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void detailsSubmiting(String fullname,String phone,String university, String course, String yos,String regno) {
        //uploading
        Map<String , String> stringMap = new HashMap<>();
        stringMap.put("regno",regno);
        stringMap.put("fullname",fullname);
        stringMap.put("yos",yos);
        stringMap.put("phone",phone);
        stringMap.put("university",university);
        stringMap.put("course",course);

        //actual uploading
        firebaseFirestore.collection("UniversityRegistration").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(SchoolRegister.this, "Details successfully submitted", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(SchoolRegister.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }


}

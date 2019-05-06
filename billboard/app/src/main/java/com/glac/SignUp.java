package com.glac;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText mEmail,mPassword,mCPassword;
    private Button mCreate;
    private String email, password, cpassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        mCPassword = (EditText)findViewById(R.id.edt_cpassword);
        mCreate = (Button)findViewById(R.id.btn_create_account);
        mEmail = (EditText)findViewById(R.id.edt_email);
        mPassword = (EditText)findViewById(R.id.edt_password);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                cpassword = mCPassword.getText().toString().trim();

                //check if they are empty
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Enter valid email...");
                }else

                if (TextUtils.isEmpty(password)){
                    mCPassword.setError("Enter Password...");
                }else

                if (TextUtils.isEmpty(cpassword)){
                    mCPassword.setError("Enter Confirm Password...");
                }else

                    if (!password.equals(cpassword)){
                        Toast.makeText(SignUp.this, "Passwords don't match..!", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.setMessage("Creating your account..");
                        progressDialog.show();
                        createAccount(email,password);
                    }
            }
        });
    }

    private void createAccount(String email,String password) {

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(SignUp.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(SignUp.this, "Error occured:\n"+error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }
        });

    }
}

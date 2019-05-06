package com.glac.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.Login;
import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailAccount extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputCPassword;
    private ActionProcessButton btnSignUp;
    private Button Already;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainma);



        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnSignUp = (ActionProcessButton) findViewById(R.id.btnSignUp);
        Already = (Button)findViewById(R.id.btnAlreadyAccount);
        inputEmail = (EditText) findViewById(R.id.edtEmailSignUp);
        inputPassword = (EditText) findViewById(R.id.edtPasswordSignUp);
        inputCPassword = (EditText)findViewById(R.id.edtCPasswordSignUp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        Already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailAccount.this,Login.class));
                finish();
            }
        });



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String cpassword = inputCPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    //Toast.makeText(getApplicationContext(), "Enter email address...!", Toast.LENGTH_SHORT).show();
                    inputEmail.setError("Enter email address...!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    //Toast.makeText(getApplicationContext(), "Enter password...!", Toast.LENGTH_SHORT).show();
                    inputPassword.setError("Enter password...!");
                    return;
                }

                if (password.length() < 6) {
                    //Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters...!", Toast.LENGTH_SHORT).show();
                    inputPassword.setError("Password too short, enter minimum 6 characters...!");
                    return;
                }
                if(!password.equals(cpassword)){
                    Toast.makeText(getApplicationContext(), "Passwords don't match...!", Toast.LENGTH_SHORT).show();
                    inputPassword.setError("Passwords don't match...!");
                    inputCPassword.setError("Passwords don't match...!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                progressDialog.setMessage("Creating Account...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(EmailAccount.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(EmailAccount.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EmailAccount.this,Login.class));
                                }else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(view.GONE);
                                progressDialog.dismiss();

                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

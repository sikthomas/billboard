package com.glac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.account.ForgotPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mrgames13.jimdo.splashscreen.App.SplashScreenBuilder;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements  ProgressGenerator.OnCompleteListener {

    private ActionProcessButton btnSignIn;
    private ProgressGenerator progressGenerator;
    private TextView AccountCreation;
    private static final String TAG = "FAXELOG";
    private EditText inputEmail, inputPassword;
    private Button mForgot;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);


        progressGenerator = new ProgressGenerator(this);

        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        AccountCreation = (TextView)findViewById(R.id.tvCreateAccount);
        mForgot = (Button)findViewById(R.id.btnForgotPassword);
        inputEmail = (EditText)findViewById(R.id.edtEmailLogin);
        inputPassword = (EditText) findViewById(R.id.edtPasswordLogin);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });

        AccountCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this, com.glac.account.AccountCreation.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressGenerator.start(btnSignIn);
                btnSignIn.setProgress(100);
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Authenticating...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();


                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(Login.this, error, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    String device_token = FirebaseInstanceId.getInstance().getToken();
                                    user_id = auth.getCurrentUser().getUid();

                                    Map<String, Object> stringMap = new HashMap<>();
                                    stringMap.put("devicetoken",device_token);
                                    firebaseFirestore.collection("Users").document(user_id).update(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Intent intent = new Intent(Login.this, MainPanelActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onComplete() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser !=null)

        {
            updateUI();
        }
    }

    private void updateUI() {

        Intent intent = new Intent(Login.this, MainPanelActivity.class);
        startActivity(intent);
        finish();

    }
}

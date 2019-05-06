package com.glac.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.ProgressGenerator;
import com.glac.R;

public class AccountCreation extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {

    private ActionProcessButton btnEmail,btnPhone;
    private ProgressGenerator progressGenerator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);


        progressGenerator = new ProgressGenerator(this);

        btnEmail = (ActionProcessButton) findViewById(R.id.btnEmailPassword);
        btnPhone = (ActionProcessButton) findViewById(R.id.btnPhoneNumber);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressGenerator.start(btnEmail);
                btnEmail.setMode(ActionProcessButton.Mode.PROGRESS);
                startActivity(new Intent(AccountCreation.this,EmailAccount.class));
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressGenerator.start(btnPhone);
                btnEmail.setMode(ActionProcessButton.Mode.PROGRESS);
                startActivity(new Intent(AccountCreation.this,PhoneAccount.class));
            }
        });

    }

    @Override
    public void onComplete() {

    }
}

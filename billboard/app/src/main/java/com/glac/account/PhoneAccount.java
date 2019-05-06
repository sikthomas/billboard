package com.glac.account;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.R;

public class PhoneAccount extends AppCompatActivity {
    private ActionProcessButton btnProceed, btnGet;
    private TextView tvAutoPhone;
    private EditText mCountryCode, mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_account);

        //initializing them items
        btnGet = (ActionProcessButton) findViewById(R.id.btnGetVerification);
        btnProceed = (ActionProcessButton) findViewById(R.id.btnProceed);
        tvAutoPhone = (TextView) findViewById(R.id.tvPhoeAuto);
        mCountryCode = (EditText) findViewById(R.id.edtCountryCode);
        mPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumberVerification);

        //setting the original phone number
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            String n = tm.getLine1Number();
            tvAutoPhone.setText(n);
            return;
        }

        //setting country code
        String countryCodeValue = tm.getNetworkCountryIso();
        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        mCountryCode.setText(countryCodeValue);
        mCountryCode.setEnabled(false);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String mobile = tvAutoPhone.getText().toString().trim();
                Intent intent = new Intent(PhoneAccount.this, VerificaionCode.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String mobile = mPhoneNumber.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 10 ||mobile.length()>10){
                    mPhoneNumber.setError("Enter a valid mobile number");
                    mPhoneNumber.requestFocus();
                    return;
                }else {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PhoneAccount.this);
                    builder.setTitle("Account Creation...");
                    builder.setMessage("Is this the number you want to create account with\n+254" + mobile + "?");
// Add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(PhoneAccount.this, VerificaionCode.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}

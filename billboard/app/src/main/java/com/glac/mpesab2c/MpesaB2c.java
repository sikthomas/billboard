package com.glac.mpesab2c;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glac.R;
import com.glac.mpesab2c.API.Models.AccessToken;
import com.glac.mpesab2c.API.Models.B2CPaymentRequest;
import com.glac.mpesab2c.API.Models.B2CPaymentResponse;
import com.glac.mpesab2c.API.Models.C2BPaymentRequest;
import com.glac.mpesab2c.API.Models.C2BPaymentResponse;

import static com.glac.mpesab2c.Utils.Enumerations.SANDBOX;

public class MpesaB2c extends AppCompatActivity {

    private EditText edPhone,edAmount;
    private Button bSend,sTkPush;

    private ProgressDialog dialog;
    private Mpesa mpesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_b2c);
        edPhone = (EditText)findViewById(R.id.edPhone);
        edAmount =(EditText)findViewById(R.id.edAmount);

        bSend = (Button)findViewById(R.id.b2csend);
        sTkPush = (Button)findViewById(R.id.stkPush);


       mpesa=Mpesa.with("oTyqtS9FNz2pSGRagaam5Kw2PkInboUF", "Bkf6Aok2zYx6H92i",SANDBOX, new MpesaLib<AccessToken>() {

            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Toast.makeText(MpesaB2c.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
                Log.wtf(MpesaB2c.this.getClass().getSimpleName(), accessToken.getAccess_token());

            }

            @Override
            public void onError(String error) {
                Toast.makeText(MpesaB2c.this, error,Toast.LENGTH_SHORT).show();
                Log.wtf("AUTHENTICATION ERROR: ",error);

            }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeB2CPayment();
            }
        });

        sTkPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSTKPayment();
            }
        });

    }

    public void makeSTKPayment(){

        String phoneNumber = edPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            edPhone.setError("Please Provide a Phone Number");
            return;
        }else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.show();
            C2BPaymentRequest request = new C2BPaymentRequest(
                    "174379",
                    "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                    "10",
                    "254716698513",
                    "174379",
                    phoneNumber,
                    "http://mycallbackurl.com/checkout.php",
                    "001ABC",
                    "Goods Payment"
            );

            mpesa.C2BStkPushPayment(request, new MpesaLib<C2BPaymentResponse>() {
                        @Override
                        public void onResult(@NonNull C2BPaymentResponse c2BPaymentResponse) {
                            dialog.dismiss();
                            edPhone.setText("");
                            Toast.makeText(MpesaB2c.this, "Success",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                            Log.wtf("Button","Fail: "+error);
                            Toast.makeText(MpesaB2c.this, "Fail",Toast.LENGTH_SHORT).show();
                        }
                    }

            );

        }


    }
    public void makeB2CPayment() {
        String amount = edAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            edAmount.setError("Please Provide the amount");

            return;
        } else {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.show();
            B2CPaymentRequest request = new B2CPaymentRequest(
                    amount,
                    "BusinessPayment",
                    "testapi251",
                    "Good",
                    "600251",
                    "254716698513",
                    "https://mobimech.azurewebsites.net/callback.php",
                    "Good",
                    "https://mobimech.azurewebsites.net/callback.php",
                    "Safaricom251!"
            );

            mpesa.B2CMpesaPayment(request, new MpesaLib<B2CPaymentResponse>() {
                @Override
                public void onResult(@NonNull B2CPaymentResponse b2CPaymentResponse) {
                    dialog.dismiss();
                    edAmount.setText("");
                    Log.wtf("Button", "success");
                    Toast.makeText(MpesaB2c.this, "Success",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    dialog.dismiss();
                    Log.wtf("Button","Fail: "+error);
                    Toast.makeText(MpesaB2c.this, "Fail",Toast.LENGTH_SHORT).show();

                }

            });

        }

    }
}

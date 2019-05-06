package com.glac;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostItemDesc extends AppCompatActivity implements com.glac.interfaces.PaymentListener{
    private TextView mTitle,mDesc,mLocation,mCounty,mPhone,mEmail;
    private Button mAdd,mBuy,mCall;
    private ImageView imagePostItem;
    String price,location,email,county,phonenumber,desc,title,phoneWhoToPay,user_id;

    Chowder chowder;
    String PAYBILL_NUMBER = "898998";
    String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";

    String productId = Utils.generateProductId();
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fbAddCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item_description);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mTitle = (TextView)findViewById(R.id.tvTitleDesc);
        fbAddCart = (FloatingActionButton)findViewById(R.id.floatAddCart);
        mCall = (Button) findViewById(R.id.btnPhoneDesc);
        mDesc = (TextView)findViewById(R.id.tvDescDesc);
        mLocation = (TextView)findViewById(R.id.tvLocationDesc);
        mCounty = (TextView)findViewById(R.id.tvCountyDesc);
        mEmail = (TextView)findViewById(R.id.tvEmailDesc);
        mBuy = (Button)findViewById(R.id.btnBuyDesc);
        imagePostItem = (ImageView)findViewById(R.id.imageView3);

        //getting passed values
        price =getIntent().getExtras().getString("price");
        mBuy.setText("Buy KSH "+price);//setting value to button
        location = getIntent().getExtras().getString("location");
        county = getIntent().getExtras().getString("county");
        desc = getIntent().getExtras().getString("desc");
        phonenumber =getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        title = getIntent().getExtras().getString("title").toUpperCase();
        String image_url = getIntent().getExtras().getString("image_url");

        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(PostItemDesc.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePostItem);

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        setTitle(title);

        //setting the values to the coresponding items

        mLocation.setText(location);
        mCounty.setText(county+" (County)");
        mDesc.setText(desc);
        mCall.setText("Call: +254"+phonenumber);
        mEmail.setText(email);
        mTitle.setText(title);

        mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if (task.isSuccessful()){
                         if (task.getResult().exists()){
                             phoneWhoToPay = task.getResult().getString("phone");

                             setUp(phoneWhoToPay);

                         }
                     }
                    }
                });
            }
        });

        mCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent sIntent = new Intent(Intent.ACTION_CALL, Uri


                        .parse("tel:"+phonenumber));


                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



                startActivity(sIntent);


            }

        });

        fbAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCart(title,price);

            }
        });



    }

    private void addCart(String Title,String Price){

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("title",Title);
        stringMap.put("price",Price);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());
        stringMap.put("user_id",user_id);

        firebaseFirestore.collection("Carts"+user_id).add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()){
                    Toast.makeText(PostItemDesc.this, "Added", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(PostItemDesc.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUp(String phoneNumberTopay) {

        chowder =new Chowder(PostItemDesc.this,PAYBILL_NUMBER,PASSKEY,this);


        //  makePayment(productId, etAmount, PhoneNumber);
        String amount = getIntent().getExtras().getString("price");
        String phoneNumber = phoneNumberTopay;
        //Your product's ID must have 13 characters
        chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);


    }

    @Override
    public void onPaymentReady(String returnCode, String processDescription, String merchantTransactionId, String transactionId) {

        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sp.edit().putString("chowderTransactionId", transactionId).apply();

        new android.support.v7.app.AlertDialog.Builder(PostItemDesc.this)
                .setTitle("Payment in progress")
                .setMessage("Please wait for a pop up from Safaricom and enter your Bonga PIN.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {



                        confirmLastPayment();

                        //Well you can skip the dialog if you want, but it will make the user feel safer, they'll know what's going on instead of sitting there
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onPaymentSuccess(String merchantId, String phoneNumber, String amount, String mpesaTransactionDate, String mpesaTransactionId, String transactionStatus, String returnCode, String processDescription, String merchantTransactionId, String encParams, String transactionId) {

        chowder.subscribeForProduct(productId, com.toe.chowder.Chowder.SUBSCRIBE_DAILY);

        //After a month, if you check the product's subscription it will be invalid, but before it will be valid

        //The payment was successful.
        new android.support.v7.app.AlertDialog.Builder(PostItemDesc.this)
                .setTitle("Payment confirmed")
                .setMessage("Payment Status: "+transactionStatus + ". Your amount of Ksh." + amount + " has been successfully paid from " + phoneNumber + " to PayBill number " + merchantId + " with the M-Pesa transaction code " + mpesaTransactionId + " on " + mpesaTransactionDate + ".\n\nThank you for your business.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //something
                    }
                }).show();
    }

    @Override
    public void onPaymentFailure(String merchantId, String phoneNumber, String amount, String transactionStatus, String processDescription) {

        //The payment has failed.

        if (transactionStatus.equals("Success")){
            //The payment was successful.
            new android.support.v7.app.AlertDialog.Builder(PostItemDesc.this)
                    .setTitle("Payment confirmed")
                    .setMessage("Payment Status: "+transactionStatus + ". Your amount of Ksh." + amount + " has been successfully paid from " + phoneNumber + " to PayBill number " + merchantId + ".\n\nThank you for your business.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Well you can skip the dialog if you want, but it might make the user feel safer
                            //The user has successfully paid so give them their goodies
                        }
                    }).show();

        }else {
            new android.support.v7.app.AlertDialog.Builder(PostItemDesc.this)
                    .setTitle("Payment failed")
                    .setMessage("Payment Status: "+transactionStatus + ". Your amount of Ksh." + amount + " was not paid from " + phoneNumber + " to PayBill number " + merchantId + ". Please try again.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String amount = getIntent().getExtras().getString("price");
                            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            phoneWhoToPay = task.getResult().getString("phone");


                                            chowder.processPayment(amount, phoneWhoToPay.replaceAll("\\+", ""), productId);
                                        }
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Well you can skip the dialog if you want, but it might make the user feel safer
                    //The user has successfully paid so give them their goodies
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private void confirmLastPayment() {
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        String transactionId = sp.getString("chowderTransactionId", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        if (transactionId != null) {
            chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionId);
        } else {
            Toast.makeText(getApplicationContext(), "No previous transaction available", Toast.LENGTH_SHORT).show();
        }
    }
}

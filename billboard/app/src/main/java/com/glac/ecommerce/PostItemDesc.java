package com.glac.ecommerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.Chowder;
import com.glac.R;
import com.glac.ecommerce.Mpesa1.Mpesa;
import com.glac.ecommerce.Mpesa1.interfaces.AuthListener;
import com.glac.ecommerce.Mpesa1.interfaces.MpesaListener;
import com.glac.ecommerce.Mpesa1.models.STKPush;
import com.glac.ecommerce.Mpesa1.utils.Pair;
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

public class PostItemDesc extends AppCompatActivity implements AuthListener, MpesaListener {
    private TextView mTitle,mDesc,mLocation,mCounty,mPriceB4Discount;
    private Button mAdd,mBuy,mCall;
    private ImageView imagePostItem;
    private FloatingActionButton mCalling,mEmailing,mWhatsApping;
    String price,location,email,county,phonenumber,desc,title,phoneWhoToPay,user_id,pricediscount;
    private ProgressDialog progressDialog;


    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String CONSUMER_KEY = "4acfxEoS9bPf5RVsAGEhcim4ef30yA61";
    public static final String CONSUMER_SECRET = "v2aI3tXJnLPxMYvI";
    public static final String CALLBACK_URL = "YOUR_CALLBACK_URL";


    public static final String  NOTIFICATION = "PushNotification";
    public static final String SHARED_PREFERENCES = "com.glac.ecommerce";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String productId = Utils.generateProductId();
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fbAddCart;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item_description);
        setupWindowAnimationsEnter();

        Mpesa.with(this, CONSUMER_KEY, CONSUMER_SECRET);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.main_collapsing);
        setSupportActionBar(toolbar);
        progressDialog  = new ProgressDialog(this);

        mTitle = (TextView)findViewById(R.id.tvTitleDesc);
        fbAddCart = (FloatingActionButton)findViewById(R.id.floatAddCart);
        mDesc = (TextView)findViewById(R.id.tvDescDesc);
        mLocation = (TextView)findViewById(R.id.tvLocationDesc);
        mCounty = (TextView)findViewById(R.id.tvCountyDesc);
        mBuy = (Button)findViewById(R.id.btnBuyDesc);
        imagePostItem = (ImageView)findViewById(R.id.imageView3);
        mCalling = (FloatingActionButton)findViewById(R.id.floating_call);
        mWhatsApping = (FloatingActionButton)findViewById(R.id.flaoting_whatsapp);
        mEmailing = (FloatingActionButton)findViewById(R.id.floating_email);
        mPriceB4Discount = (TextView)findViewById(R.id.tvPriceB4Discount);

        //getting passed values
        price =getIntent().getExtras().getString("price");
        mBuy.setText("Buy KSH "+price);//setting value to button
        location = getIntent().getExtras().getString("location");
        county = getIntent().getExtras().getString("county");
        desc = getIntent().getExtras().getString("desc");
        phonenumber =getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        title = getIntent().getExtras().getString("title").toUpperCase();
        pricediscount = getIntent().getExtras().getString("pricediscount");
        String image_url = getIntent().getExtras().getString("image_url");


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFICATION)) {
                    String title = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");
                    int code = intent.getIntExtra("code", 0);
                    showDialog(title, message, code);
                    if (code==0){
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(context, "Not", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(PostItemDesc.this, message, Toast.LENGTH_SHORT).show();

                }
            }
        };

        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(PostItemDesc.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePostItem);

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //setting the values to the coresponding items
        toolbarLayout.setTitle(title);

        mLocation.setText(location);
        mCounty.setText(county+" (County)");
        mDesc.setText(desc);
        mTitle.setVisibility(View.GONE);
        mPriceB4Discount.setText("Previous price: KSH "+pricediscount);
        mPriceB4Discount.setPaintFlags(mPriceB4Discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if (task.isSuccessful()){
                         if (task.getResult().exists()){
                             phoneWhoToPay = task.getResult().getString("phone");
                             int amount = Integer.parseInt(price);
                             pay(phoneWhoToPay,amount);

                         }
                     }
                    }
                });
            }
        });

        //Making a call
        mCalling.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String mYphone = task.getResult().getString("phone");

                                if (phonenumber.equals(mYphone)){
                                    Toast.makeText(PostItemDesc.this, "You cannot call yourself..", Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent sIntent = new Intent(Intent.ACTION_CALL, Uri


                                            .parse("tel:"+phonenumber));


                                    sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



                                    startActivity(sIntent);
                                }
                            }
                        }
                    }
                });


            }
        });
        //sending and email
        mEmailing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                String fname = task.getResult().getString("fname");
                                String lname = task.getResult().getString("lname");
                                String myEmail = task.getResult().getString("email");
                                String fullname = fname + " " + lname;

                                if (email.equals(myEmail)) {
                                    Toast.makeText(PostItemDesc.this, "You cannot email yourself..", Toast.LENGTH_SHORT).show();
                                } else {

                                    //sedning the email here

                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", email, null));
                                    intent.putExtra(Intent.EXTRA_SUBJECT, title.toUpperCase());
                                    intent.putExtra(Intent.EXTRA_TEXT, "Hope this email finds you well\nI am " + fullname.toUpperCase() + " saw your item on GLAC...");
                                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                                }
                            }
                        }

                    }
                });
            }
        });

        //whatsApping
        mWhatsApping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String mYphone = task.getResult().getString("phone");

                                if (phonenumber.equals(mYphone)){
                                    Toast.makeText(PostItemDesc.this, "You cannot WhatsApp yourself..", Toast.LENGTH_SHORT).show();
                                }else {

                                    openWhatsApp(view,phonenumber);
                                }
                            }
                        }
                    }
                });

            }
        });

        fbAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCart(title,price);

            }
        });



    }

    private void showDialog(String title, String message, int code) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message+"\n"+code)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setupWindowAnimationsExit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupWindowAnimationsEnter();
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimationsEnter() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
        }
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimationsExit() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(slide);
        }
    }

    public void openWhatsApp(View view,String toNumber){
        PackageManager pm=getPackageManager();
        try {

            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(PostItemDesc.this,"Pease install whatsApp Messenger to use this option...",Toast.LENGTH_LONG).show();

        }
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


    private void pay(String phone, int amount){
        progressDialog.setMessage("Connecting to Safaricom...");
        progressDialog.show();
        STKPush.Builder builder = new STKPush.Builder(BUSINESS_SHORT_CODE, PASSKEY, amount,BUSINESS_SHORT_CODE, phone);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String token = sharedPreferences.getString("InstanceID", "");

        builder.setFirebaseRegID(token);
        STKPush push = builder.build();



        Mpesa.getInstance().pay(this,push);

    }

    @Override
    public void onMpesaError(Pair<Integer, String> result) {
        progressDialog.dismiss();
        Toast.makeText(this, "Erorr occured \n"+result.message, Toast.LENGTH_SHORT).show();new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setMessage("Transaction Failed. Try again")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onMpesaSuccess(String MerchantRequestID, String CheckoutRequestID, String CustomerMessage) {
        progressDialog.dismiss();
        Toast.makeText(this, CustomerMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthError(Pair<Integer, String> result) {
        Toast.makeText(this, "Error\n"+result.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthSuccess() {
        Toast.makeText(this, "Authentication success", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NOTIFICATION));

    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}

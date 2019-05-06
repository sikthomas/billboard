package com.glac.academics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.glac.Chowder;
import com.glac.R;
import com.glac.ecommerce.PostItemDesc;
import com.glac.ecommerce.Posting;
import com.glac.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SchoolPosting extends AppCompatActivity implements com.glac.interfaces.PaymentListener{
    private EditText mTitle,mDesc;
    private Button mPost;
    private String user_id,title,descc,university,phone,fullname,regno,phoneWhoToPay;
    private ImageView mImagePost;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Uri imageUri = null;


    Chowder chowder;
    String PAYBILL_NUMBER = "898998";
    String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";

    String productId = Utils.generateProductId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_posting);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //initlising items
        mTitle = (EditText)findViewById(R.id.edt_title_school);
        mDesc = (EditText)findViewById(R.id.edt_desc_school);
        mPost = (Button)findViewById(R.id.btn_school_post);
        mImagePost = (ImageView)findViewById(R.id.img_school);



        //picking image
        mImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SchoolPosting.this);
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = mTitle.getText().toString().trim().toUpperCase();
                descc = mDesc.getText().toString().trim();

                if (TextUtils.isEmpty(title)){
                    mTitle.setError("Enter your post title...");
                }else if (TextUtils.isEmpty(descc)){
                    mDesc.setError("Enter your post description...");
                }else if (imageUri == null){
                    Toast.makeText(SchoolPosting.this, "Please pick image...", Toast.LENGTH_SHORT).show();
                }else {
                    new android.support.v7.app.AlertDialog.Builder(SchoolPosting.this)
                            .setTitle("Payment Request")
                            .setMessage("Payment Status: \nTo post an item you'll be charged KSH 20 from your MPESA Account.\nProceed? ")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                if (task.getResult().exists()){
                                                   final String phone = task.getResult().getString("phone");
                                                    //setUp(phone);progressDialog.setMessage("Uploading your item...");
                                                    progressDialog.setMessage("Uploading...");
                                                    progressDialog.show();
                                                    firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                            if (task.isSuccessful()){
                                                                if (task.getResult().exists()){
                                                                    university = task.getResult().getString("university");
                                                                    final String phone1 = task.getResult().getString("phone");
                                                                    fullname = task.getResult().getString("fullname");
                                                                    regno = task.getResult().getString("regno");


                                                                    String randoM = UUID.randomUUID().toString();
                                                                    final StorageReference reference = storageReference.child(user_id+"SMarket").child(randoM+".jpg");
                                                                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                @Override
                                                                                public void onSuccess(Uri uri) {

                                                                                    Uri downloadUri = uri;
                                                                                    postingDone(title,descc,downloadUri.toString(),fullname,regno,phone1,university);
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                }
                            }).show();


                }

            }
        });
    }
//setting the image data to the imageView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mImagePost.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void postingDone(String title,String desc,String image_url,String fullname,String regno,String phone,String university){

        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("fullname",fullname);
        objectMap.put("title",title);
        objectMap.put("desc",desc);
        objectMap.put("imageUrl",image_url);
        objectMap.put("user_id",auth.getCurrentUser().getUid());
        objectMap.put("regno",regno);
        objectMap.put("phone",phone);
        objectMap.put("timeStamp",FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university).collection("SchoolMarket").add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()){
                    Toast.makeText(SchoolPosting.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(SchoolPosting.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }



    private void setUp(String phoneNumberTopay) {

        chowder =new Chowder(SchoolPosting.this,PAYBILL_NUMBER,PASSKEY,this);


        //  makePayment(productId, etAmount, PhoneNumber);
        String amount = "20";
        String phoneNumber = phoneNumberTopay;
        //Your product's ID must have 13 characters
        chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);


    }

    @Override
    public void onPaymentReady(String returnCode, String processDescription, String merchantTransactionId, String transactionId) {
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sp.edit().putString("chowderTransactionId", transactionId).apply();

        new android.support.v7.app.AlertDialog.Builder(SchoolPosting.this)
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
        new android.support.v7.app.AlertDialog.Builder(SchoolPosting.this)
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
            new android.support.v7.app.AlertDialog.Builder(SchoolPosting.this)
                    .setTitle("Payment confirmed")
                    .setMessage("Payment Status: "+transactionStatus + ". Your amount of Ksh." + amount + " has been successfully paid from " + phoneNumber + " to PayBill number " + merchantId + ".\n\nThank you for your business.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("Uploading your item...");
                            progressDialog.show();
                            firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            university = task.getResult().getString("university");
                                            phone = task.getResult().getString("phone");
                                            fullname = task.getResult().getString("fullname");
                                            regno = task.getResult().getString("regno");


                                            String randoM = UUID.randomUUID().toString();
                                            final StorageReference reference = storageReference.child(user_id+"SMarket").child(randoM+".jpg");
                                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Uri downloadUri = uri;
                                                            postingDone(title,descc,downloadUri.toString(),fullname,regno,phone,university);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }

                                }
                            });
                        }
                    }).show();

        }else {
            new android.support.v7.app.AlertDialog.Builder(SchoolPosting.this)
                    .setTitle("Payment failed")
                    .setMessage("Payment Status: "+transactionStatus + ". Your amount of Ksh." + amount + " was not paid from " + phoneNumber + " to PayBill number " + merchantId + ". Please try again.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String amount = "20";
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

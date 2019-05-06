package com.glac.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.MainPanelActivity;
import com.glac.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class Posting extends AppCompatActivity {
    private static int MAX_LENGTH =1000;
    private View view;
    private ImageView buttonLoadImage,buttonLoadImage1;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private Spinner countySpinner,categories;
    private EditText mTtitle,mDesc,mPrice,mLocation,mPrice_Initial;
    private FirebaseStorage firebaseStorage;
    private ActionProcessButton btnPost;
    private String phone_val,email_val,uriDownload,downloadThumUri;
    private UploadTask uploadTask;
    private String price,location,email,phonenumber,desc,title,phoneWhoToPay,user_id,pricediscount,county;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);



        mStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //initializing bttons and spinner
        btnPost = (ActionProcessButton)findViewById(R.id.btnPostItem);
        mTtitle = (EditText) findViewById(R.id.edtPostTitle);
        mDesc = (EditText) findViewById(R.id.edtPostDesc);
        mPrice = (EditText) findViewById(R.id.edtPostPriceAfterDiscount);
        mPrice_Initial = (EditText) findViewById(R.id.edtPostPriceInitial);
        mLocation = (EditText) findViewById(R.id.edtLoacation);
        countySpinner = (Spinner)findViewById(R.id.spinnerPostCounty);
        categories = (Spinner)findViewById(R.id.spinnerCategory);
        buttonLoadImage = (ImageView) findViewById(R.id.imageToPost);

        //picking our image item
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Posting.this);
            }
        });
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Posting.this);
            }
        });

        //setting inital value to spinner
        countySpinner.setSelection(0);
        categories.setSelection(0);

        //OnPost button getin' clicked
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //posting shit done here
                startposting();


            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                buttonLoadImage.setImageURI(imageUri);
                buttonLoadImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
    private void postingTest(){
        storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
        @Override
        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageReference.getDownloadUrl();
        }
    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                postingValues(downloadUri.toString());

            } else {
                Toast.makeText(Posting.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });
    }
    private void startposting() {

        final String title_val = mTtitle.getText().toString().trim();
        final String desc_val = mDesc.getText().toString().trim();
        final String price_val = mPrice.getText().toString().trim();
        final String location_val = mLocation.getText().toString().trim();
        final String county_val = countySpinner.getSelectedItem().toString();
        final String pricediscount = mPrice_Initial.getText().toString();
        final String category = categories.getSelectedItem().toString();
        if (TextUtils.isEmpty(title_val)){
            Toast.makeText(this, "Enter Post Title...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(desc_val)){
            Toast.makeText(this, "Enter item Description...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(price_val)){
            Toast.makeText(this, "Enter item Price...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(location_val)){
            Toast.makeText(this, "Enter item location...", Toast.LENGTH_SHORT).show();
        }else if (countySpinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select item county...", Toast.LENGTH_SHORT).show();
        }else if (categories.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select item category...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(pricediscount)){
            mPrice_Initial.setError("Enter price without the discount");
        }else {
            btnPost.setEnabled(false);
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if (task.getResult().exists()){
                            phone_val = task.getResult().getString("phone");
                            email_val = task.getResult().getString("email");

                            progressDialog.setMessage("Uploading your post...");
                            progressDialog.show();

                            final String randomName = UUID.randomUUID().toString();
                            final StorageReference reference = storageReference.child(category+" post_images").child(randomName+".jpg");
                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            Uri dowlaodUrl = uri;

                                            Map<String, Object> objectMap = new HashMap<>();
                                            objectMap.put("title",title_val);
                                            objectMap.put("phone",phone_val);
                                            objectMap.put("price",price_val);
                                            objectMap.put("location",location_val);
                                            objectMap.put("email",email_val);
                                            objectMap.put("county",county_val);
                                            objectMap.put("category",category);
                                            objectMap.put("user_id",user_id);
                                            objectMap.put("desc_val",desc_val);
                                            objectMap.put("pricediscount",pricediscount);
                                            objectMap.put("image_url",dowlaodUrl.toString());
                                            objectMap.put("timeStamp",FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("HotDeals").document("Categories").collection(category).add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {


                                                    if(task.isSuccessful()){

                                                        Toast.makeText(getApplicationContext(),"Uploaded successfully...",Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Posting.this,MainPanelActivity.class));
                                                        finish();


                                                    }else {
                                                        String erro = task.getException().getMessage();
                                                        Toast.makeText(getApplicationContext(),erro,Toast.LENGTH_SHORT).show();

                                                    }
                                                    progressDialog.dismiss();

                                                }
                                            });


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
    void postingValues(String url){
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image_uri", url);
        postMap.put("timeStamp", FieldValue.serverTimestamp());


        firebaseFirestore.collection("HotDeals").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Uploaded successfully...",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Posting.this,MainPanelActivity.class));
                    finish();


                }else {
                    String erro = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(),erro,Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();

            }
        });

    }
}

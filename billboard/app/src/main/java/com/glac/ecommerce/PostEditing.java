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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.MainPanelActivity;
import com.glac.R;
import com.glac.account.MyAccountASettings;
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

public class PostEditing extends AppCompatActivity {
    private static int MAX_LENGTH =1000;
    private View view;
    private ImageView buttonLoadImage;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private Spinner countySpinner;
    private EditText mTtitle,mDesc,mPrice,mLocation,mPrice_Initial;
    private FirebaseStorage firebaseStorage;
    private ActionProcessButton btnPost;
    private String phone_val,email_val,uriDownload,downloadThumUri;
    private UploadTask uploadTask;
    private String price,location,email,phonenumber,desc,title,phoneWhoToPay,user_id,pricediscount,county,postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editing);


        mStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //initializing bttons and spinner
        btnPost = (ActionProcessButton)findViewById(R.id.btnPostItem_edit);
        mTtitle = (EditText) findViewById(R.id.edtPostTitle_edit);
        mDesc = (EditText) findViewById(R.id.edtPostDesc_edit);
        mPrice = (EditText) findViewById(R.id.edtPostPriceAfterDiscount_edit);
        mPrice_Initial = (EditText) findViewById(R.id.edtPostPriceInitial_edit);
        mLocation = (EditText) findViewById(R.id.edtLoacation_edit);
        countySpinner = (Spinner)findViewById(R.id.spinnerPostCounty_edit);
        buttonLoadImage = (ImageView) findViewById(R.id.imageToPost_edit);



        //picking our image item
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostEditing.this);
            }
        });

        //setting inital value to spinner
        countySpinner.setSelection(0);



        //getting passed values
        price =getIntent().getExtras().getString("price");
        location = getIntent().getExtras().getString("location");
        county = getIntent().getExtras().getString("county");
        desc = getIntent().getExtras().getString("desc");
        phonenumber =getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        title = getIntent().getExtras().getString("title").toUpperCase();
        pricediscount = getIntent().getExtras().getString("pricediscount");
        String image_url = getIntent().getExtras().getString("image_url");
        postId = getIntent().getExtras().getString("postID");

        mTtitle.setText(title);
        mDesc.setText(desc);
        mLocation.setText(location);
        mPrice.setText(price);
        mPrice_Initial.setText(pricediscount);


        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(PostEditing.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(buttonLoadImage);

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        //setting user county to spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PostEditing.this, R.array.spinneritems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(adapter);
        if (county != null) {
            int spinnerPosition = adapter.getPosition(county);
            countySpinner.setSelection(spinnerPosition);
        }


        //OnPost button getin' clicked
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //posting shit done here
                startposting(postId);


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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void startposting(final String postId) {

        final String title_val = mTtitle.getText().toString().trim();
        final String desc_val = mDesc.getText().toString().trim();
        final String price_val = mPrice.getText().toString().trim();
        final String location_val = mLocation.getText().toString().trim();
        final String county_val = countySpinner.getSelectedItem().toString();
        final String pricediscount = mPrice_Initial.getText().toString();
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
        }else if (TextUtils.isEmpty(pricediscount)){
            mPrice_Initial.setError("Enter price without the discount");
        }else {
            if (imageUri == null){
                Toast.makeText(this, "Please reload your image..!", Toast.LENGTH_SHORT).show();
            }else
            { btnPost.setEnabled(false);
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
                                final StorageReference reference = storageReference.child(user_id+" post_images").child(randomName+".jpg");
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
                                                objectMap.put("user_id",user_id);
                                                objectMap.put("desc_val",desc_val);
                                                objectMap.put("pricediscount",pricediscount);
                                                objectMap.put("image_url",dowlaodUrl.toString());
                                                objectMap.put("timeStamp", FieldValue.serverTimestamp());

                                                firebaseFirestore.collection("HotDeals").document(postId).update(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){
                                                            Toast.makeText(PostEditing.this, "Successfully edited...", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            String error = task.getException().toString();
                                                            Toast.makeText(PostEditing.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                                                        }

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

    }
}

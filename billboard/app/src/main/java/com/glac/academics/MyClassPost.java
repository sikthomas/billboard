package com.glac.academics;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.glac.R;
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

public class MyClassPost extends AppCompatActivity {
    private ImageView mPostedImage;
    private Button mPost;
    private EditText mTitle,mDesc;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String user_id,university,yos,course,title,desc,regno;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class_post);

        //for firebase initialization
        auth = FirebaseAuth.getInstance();
        user_id  = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //for layout items initialization
        mDesc=(EditText)findViewById(R.id.edt_desc_school_class);
        mTitle = (EditText)findViewById(R.id.edt_title_school_class);
        mPost = (Button)findViewById(R.id.btn_school_post_class);
        mPostedImage = (ImageView)findViewById(R.id.img_school_class);
        progressDialog = new ProgressDialog(this);

        mPostedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MyClassPost.this);
            }
        });

        //on click
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                title = mTitle.getText().toString().trim().toUpperCase();
                desc = mDesc.getText().toString().trim();
                if (TextUtils.isEmpty(title)){
                    mTitle.setError("Please enter post title");
                }else if (TextUtils.isEmpty(desc)){
                    mDesc.setError("Please enter post description");
                }else {
                    //getting other values from firebase
                    firebaseFirestore.collection("UniversityRegistration").document(user_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    university = task.getResult().getString("university");
                                    course = task.getResult().getString("course");
                                    yos = task.getResult().getString("yos");
                                    regno = task.getResult().getString("regno");

                                    if (imageUri == null) {
                                        progressDialog.setMessage("Uploading your post\n" + title);
                                        progressDialog.show();
                                        String imageUrl = "NoImage";
                                        postToClassWithNoImageOption(university, regno, yos, course, desc, title, imageUrl);
                                        //Toast.makeText(MyClassPost.this, "Pick image", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            progressDialog.setMessage("Uploading your post\n" + title);
                                            progressDialog.show();

                                            //posting image
                                            String randomName = UUID.randomUUID().toString();
                                            final StorageReference reference = storageReference.child(user_id + "ClassPostsImages").child(randomName + ".jpg");
                                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Uri newImageUri = uri;
                                                            postToClass(university, regno, yos, course, desc, title, newImageUri.toString());


                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }
                                }
                            });
                }
            }
        });


    }

    private void postToClass(String university, String regno, String yos,String course,String post,String title,String imageUrl) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("post",post);
        objectMap.put("title",title.toUpperCase());
        objectMap.put("regno",regno.toUpperCase());
        objectMap.put("imageUrl",imageUrl);
        objectMap.put("user_id",auth.getCurrentUser().getUid());
        objectMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university)
                .collection("ClassPosts").document(course).collection(yos).add(objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(MyClassPost.this, "Successfully posted", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(MyClassPost.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }



    private void postToClassWithNoImageOption(String university, String regno, String yos,String course,String post,String title,String imageUrl) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("post",post);
        objectMap.put("title",title.toUpperCase());
        objectMap.put("regno",regno.toUpperCase());
        objectMap.put("imageUrl",imageUrl);
        objectMap.put("user_id",auth.getCurrentUser().getUid());
        objectMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university)
                .collection("ClassPosts").document(course).collection(yos).add(objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(MyClassPost.this, "Successfully posted", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(MyClassPost.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
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
                mPostedImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "An error occured"+error, Toast.LENGTH_SHORT).show();
            }else if (requestCode == RESULT_CANCELED){
                imageUri = Uri.parse("no image".toString());
                imageUri =null;
            }
        }


    }


}

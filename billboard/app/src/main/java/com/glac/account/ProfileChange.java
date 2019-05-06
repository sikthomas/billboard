package com.glac.account;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileChange extends AppCompatActivity {
    private ImageView mImageToChange;
    private Button mSubmit;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String user_id, iamge_url;
    private Uri imageUri = null;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth =FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        //initializing the items
        mImageToChange = (ImageView)findViewById(R.id.imageToChange);
        mSubmit = (Button)findViewById(R.id.btnSubmitToChange);

        //pickiing the image from gallery
        mImageToChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ProfileChange.this);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri!=null){

                final String randomName = UUID.randomUUID().toString();
                final StorageReference reference = storageReference.child(user_id+" user_images").child(randomName+".jpg");
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downld = uri;
                                editing(downld.toString());
                            }
                        });
                    }
                });
            }else {
                    Toast.makeText(ProfileChange.this, "Please pick image first...!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void editing(String download_uri) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("imageUrl",download_uri);

        firebaseFirestore.collection("Users").document(user_id).update(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(ProfileChange.this, "Image profile successfully changed...", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(ProfileChange.this, "Something went wrong: "+error, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mImageToChange.setImageURI(imageUri);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

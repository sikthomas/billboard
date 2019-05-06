package com.glac.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dd.processbutton.iml.ActionProcessButton;
import com.glac.Login;
import com.glac.MainPanelActivity;
import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountSetting extends AppCompatActivity { private ActionProcessButton btnSubmit;
    private TextInputEditText mFName,mLName,mID,mPhone,mEmail,mArea;
    private CircleImageView mProfileImage;

    private FirebaseFirestore firebaseFirestore;
    private Uri imageUri = null;
    private TextView usernameSet;
    private Bitmap compressedImageFile;
    private final String randomName = UUID.randomUUID().toString();

    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String user_id,admin=null;
    private ProgressDialog progressDialog;
    private View view;
    private Context context;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_setting);

        progressDialog = new ProgressDialog(MyAccountSetting.this);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();

        btnSubmit = (ActionProcessButton)findViewById(R.id.btnAccountProfile);
        mFName = (TextInputEditText)findViewById(R.id.edtFirstName);
        mLName = (TextInputEditText)findViewById(R.id.edtLastName);
        mID = (TextInputEditText)findViewById(R.id.edtID);
        mPhone = (TextInputEditText)findViewById(R.id.edtPhoneNumberAccountSetup);
        mEmail = (TextInputEditText)findViewById(R.id.edtEmailAccount);
        mProfileImage =(CircleImageView)findViewById(R.id.image_profile);
        spinner = (Spinner)findViewById(R.id.spinnerLoaction);
        mArea = (TextInputEditText)findViewById(R.id.edtLocalArea);

        //SETTING DEFAUL VALUE TO SPINNER
        spinner.setSelection(0);

        gettingUserDetails();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MyAccountSetting.this);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final  String fname = mFName.getText().toString().trim();
                final  String lname = mLName.getText().toString().trim();
                final  String id_number = mID.getText().toString().trim();
                final  String phone_number = mPhone.getText().toString().trim();
                final  String email = mEmail.getText().toString().trim();
                final  String county = spinner.getSelectedItem().toString();
                final  String larea = mArea.getText().toString().trim();

                if (TextUtils.isEmpty(fname)){
                    mFName.setError("Enter Your First Name..!");
                }else if (TextUtils.isEmpty(lname)){
                    mLName.setError("Enter Your Last Name..!");
                }else if (TextUtils.isEmpty(id_number)){
                    mID.setError("Enter Your ID Number..!");
                }else if (TextUtils.isEmpty(phone_number)){
                    mPhone.setError("Enter Your Phone Number..!");
                }else if (TextUtils.isEmpty(email)){
                    mEmail.setError("Enter Your Valid Email..!");
                }else if (spinner.getSelectedItemPosition()== 0){
                    Toast.makeText(MyAccountSetting.this, "Select Your County..!", Toast.LENGTH_SHORT).show();
                }else if (imageUri == null){
                    Toast.makeText(MyAccountSetting.this, "Select Profile Image..!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(larea)){
                    mArea.setError("Enter Your Local Area..!");
                }else {

                    if (!TextUtils.isEmpty(fname)) {
                        progressDialog.setMessage("Uploading your personal details...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();

                        //imageUpload();
                        fireStoreUpload(fname,lname,id_number,phone_number,county,larea,email);
                    }
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout_myaccoount){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(MyAccountSetting.this,Login.class));
                    auth.signOut();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        return true;
    }

    private void imageUpload(){
        final StorageReference reference = storageReference.child(user_id+" user_images").child(randomName+".jpg");
        reference.putFile(imageUri);
    }

    private void fireStoreUpload( final String fname, final String lname, final String id_number, final String phone, final String county, final String local_area, final String email) {

        final StorageReference reference = storageReference.child(user_id+" user_images").child(randomName+".jpg");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String devicetoken = FirebaseInstanceId.getInstance().getToken();
                            admin = "not_admin";

                            Map<String, String> stringMap = new HashMap<>();
                            stringMap.put("fname", fname);
                            stringMap.put("lname",lname);
                            stringMap.put("id_number",id_number);
                            stringMap.put("phone",phone);
                            stringMap.put("email",email);
                            stringMap.put("county",county);
                            stringMap.put("local_area",local_area);
                            stringMap.put("imageUrl",uri.toString());
                            stringMap.put("admin",admin);
                            stringMap.put("devicetoken",devicetoken);




                            firebaseFirestore.collection("Users").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"User settings are uploaded",Toast.LENGTH_LONG);
                                        onBackPressed();


                                    }else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(),"Server error!..\n"+errorMessage,Toast.LENGTH_LONG);
                                    }

                                    progressDialog.dismiss();

                                }
                            });
                        }
                    });
                }
            }
        });





    }

    private void firebaseEditing(@NonNull Task<UploadTask.TaskSnapshot> task, final String fname, final String lname, final String id_number, final String phone, final String county, final String local_area, final String email){


        final String randomName = UUID.randomUUID().toString();
        final StorageReference reference = storageReference.child(user_id+" user_images").child(randomName+".jpg");

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("fname", fname);
                stringMap.put("lname",lname);
                stringMap.put("id_number",id_number);
                stringMap.put("phone",phone);
                stringMap.put("email",email);
                stringMap.put("county",county);
                stringMap.put("local_area",local_area);
                stringMap.put("imageUrl",uri.toString());




                firebaseFirestore.collection("Users").document(user_id).update(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successfully edited...",Toast.LENGTH_LONG);
                            onBackPressed();


                        }else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),"Server error!..\n"+errorMessage,Toast.LENGTH_LONG);
                        }

                        progressDialog.dismiss();

                    }
                });
            }
        });



    }

    private void gettingUserDetails(){
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String fname = task.getResult().getString("fname");
                        String lname = task.getResult().getString("lname");
                        String id = task.getResult().getString("id_number");
                        String image_url = task.getResult().getString("imageUrl");
                        String phone = task.getResult().getString("phone");
                        String email = task.getResult().getString("email");
                        String county = task.getResult().getString("county");
                        String local = task.getResult().getString("local_area");

                        mFName.setText(fname);
                        mLName.setText(lname);
                        mID.setText(id);
                        mPhone.setText(phone);
                        mEmail.setText(email);
                        mArea.setText(local);

                        //setting image url
                        try{
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop();
                            requestOptions.placeholder(R.color.lightGrray);

                            Glide.with(MyAccountSetting.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(mProfileImage);

                        }catch (Exception e){
                        }

                        //setting user county to spinner
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MyAccountSetting.this, R.array.spinneritems, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        if (county != null) {
                            int spinnerPosition = adapter.getPosition(county);
                            spinner.setSelection(spinnerPosition);
                        }
                    }
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
                mProfileImage.setImageURI(imageUri);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

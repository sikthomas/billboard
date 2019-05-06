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
import android.widget.TextView;
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoticeBoard extends AppCompatActivity {
    private Button mPost,mChoose;
    private static final int PICKFILE_RESULT_CODE = 0;
    private EditText mMind;
    private FirebaseAuth auth;
    private TextView mFilePath;
    private Uri uri;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,university;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();


        mPost = (Button)findViewById(R.id.btn_post_noticeboard);
        mMind = (EditText)findViewById(R.id.edt_mind_post);
        mChoose = (Button)findViewById(R.id.btn_choose_file);

        //picking the file
        mChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(NoticeBoard.this, "Please install a file manager..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //on click
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post = mMind.getText().toString().trim();
                if (TextUtils.isEmpty(post)){
                    mMind.setError("Enter post..!");
                }else {
                    gettingUni(post);
                    mMind.setText("");
                }
            }
        });


    }

    private void gettingUni(final String post){
        firebaseFirestore.collection("UniversityRegistration").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                progressDialog.setMessage("Uploading...");
                                progressDialog.show();
                               final String university = task.getResult().getString("university");

                               if (uri !=null) {
                                   String randomVal = UUID.randomUUID().toString();
                                   final StorageReference reference = storageReference.child(user_id + "NoticeBoardFiles").child(mFilePath.getText().toString());
                                   reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                               @Override
                                               public void onSuccess(Uri uri) {
                                                   String newUri = uri.toString();
                                                   sendingNotifications(university);
                                                   sendingPost(university, post, newUri);
                                               }
                                           });
                                       }
                                   });

                               }else {
                                   String newUri = "NoFile";
                                   sendingPostNullFile(university,post,newUri);
                               }


                            }
                        }
                    }
                });
    }

    private void sendingNotifications(String university){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("from",user_id);
        stringMap.put("type","notice");

        firebaseFirestore.collection("Universities").document(university).collection("NoticeBoard").document("Notice").collection("Notifications").add(stringMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(NoticeBoard.this, "Sent..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendingPost(String university,String post,String fileUri){


        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("post",post);
        stringMap.put("user_id",user_id);
        stringMap.put("fileUri",fileUri);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university).collection("NoticeBoard").add(stringMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(NoticeBoard.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            Toast.makeText(NoticeBoard.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                });
    }


    private void sendingPostNullFile(String university,String post,String fileUri){


        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("post",post);
        stringMap.put("user_id",user_id);
        stringMap.put("fileUri","NoFile");
        stringMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Universities").document(university).collection("NoticeBoard").add(stringMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(NoticeBoard.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            Toast.makeText(NoticeBoard.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK && requestCode==PICKFILE_RESULT_CODE && data!=null && data.getData() !=null){
                    uri = data.getData();
                    try {
                        String path = FileUtils.getPath(this, uri);
                        mFilePath = (TextView)findViewById(R.id.tv_file_path);
                        mFilePath.setText(path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }

        super.onActivityResult(requestCode,resultCode,data);
    }
}

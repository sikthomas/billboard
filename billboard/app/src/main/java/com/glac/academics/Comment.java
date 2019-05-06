package com.glac.academics;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Comment extends AppCompatActivity {
    private Button mSend;
    private RecyclerView mRecycleview;
    private EditText mComment;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,university,yos,course,postId,regno;

    private List<CommentList> commentLists;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentLists = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentLists);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();

        mComment= (EditText)findViewById(R.id.edittext_chatbox);
        mSend = (Button)findViewById(R.id.button_chatbox_send);
        mRecycleview = (RecyclerView)findViewById(R.id.reyclerview_message_list);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mRecycleview.setAdapter(commentAdapter);


        gettingAllComments();

        //sending the comment
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mComment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){
                    mComment.setError("Enter comment...");
                }else {
                    mComment.setText("");
                    sending(comment);

                }
            }
        });

        postId = getIntent().getExtras().getString("postId");
        String title = getIntent().getExtras().getString("title");
        setTitle(title);
    }

    private void sending(final String comment) {

        //getting user details
        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        university = task.getResult().getString("university");
                        course = task.getResult().getString("course");
                        yos = task.getResult().getString("yos");
                        regno = task.getResult().getString("regno");

                        Map<String, Object> stringMap = new HashMap<>();
                        stringMap.put("regno",regno);
                        stringMap.put("comment",comment);
                        stringMap.put("timeStamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Universities").document(university).collection("ClassPosts").document(course).collection(yos).document(postId).collection("comments")
                                .add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(Comment.this,"Sent...",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(Comment.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }
                }
            }
        });

    }

    private void gettingAllComments(){

        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                      String  university = task.getResult().getString("university");
                       String course = task.getResult().getString("course");
                       String yos = task.getResult().getString("yos");
                        String regno = task.getResult().getString("regno");

                        Query query = firebaseFirestore.collection("Universities").document(university)
                                .collection("ClassPosts").document(course).collection(yos).document(postId)
                                .collection("comments").orderBy("timeStamp", Query.Direction.ASCENDING);
                        query.addSnapshotListener(Comment.this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                                    if (doc.getType() == DocumentChange.Type.ADDED){

                                        String postId = doc.getDocument().getId();
                                        CommentList commentList  = doc.getDocument().toObject(CommentList.class).withId(postId);

                                        commentLists.add(commentList);
                                        commentAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });


    }
}

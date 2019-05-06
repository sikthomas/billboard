package com.glac.academics;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MyClassAvailablePosst extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,university,course,yos;
    private RecyclerView recyclerView;
    private List<MyClassPostList> myClassPostLists;
    private MyClassPostAdapter myClassPostAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class_available_posst);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        myClassPostLists = new ArrayList<>();
        myClassPostAdapter = new MyClassPostAdapter(myClassPostLists);

        recyclerView = (RecyclerView)findViewById(R.id.post_list_myclass);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_myclass);
        mPost = (FloatingActionButton)findViewById(R.id.floating_school_myclass);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myClassPostAdapter);

        firebaseFirestore.collection("UniversityRegistration").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                university = task.getResult().getString("university");
                                course = task.getResult().getString("course");
                                yos = task.getResult().getString("yos");
                                gettingPosts(university,course,yos);

                                setTitle(course+" "+"("+yos+")");
                            }
                        }
                    }
                });


        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyClassAvailablePosst.this,MyClassPost.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(myClassPostAdapter);

                    }
                },300);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.noticeboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_noticeboard){
            startActivity(new Intent(MyClassAvailablePosst.this,NoticeboadCarrie.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void gettingPosts(String university, String course, String yos){
        Query query = firebaseFirestore.collection("Universities").document(university).collection("ClassPosts")
                .document(course).collection(yos).orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(MyClassAvailablePosst.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String postID = doc.getDocument().getId();
                                MyClassPostList myClassPostList = doc.getDocument().toObject(MyClassPostList.class).withId(postID);

                                myClassPostLists.add(myClassPostList);

                                myClassPostAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getApplicationContext(), "No post for ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}

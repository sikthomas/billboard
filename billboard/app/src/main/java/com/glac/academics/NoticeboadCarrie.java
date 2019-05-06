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
import android.view.View;
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

public class NoticeboadCarrie extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<NoticeBoardList> noticeBoardLists;
    private NoticeboadAdapter noticeboadAdapter;
    private String university,user_id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboad_carrie);
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        noticeBoardLists = new ArrayList<>();
        noticeboadAdapter = new NoticeboadAdapter(noticeBoardLists);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_list_noticeboard);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swie_refresh_notice);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floating_noticeboard);


        recyclerView.setLayoutManager(new LinearLayoutManager(NoticeboadCarrie.this));
        recyclerView.setAdapter(noticeboadAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticeboadCarrie.this,NoticeBoard.class));
            }
        });



        gettingUni();
        setTitle("Notice Board");



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(noticeboadAdapter);

                    }
                },300);
            }
        });
    }

    private void gettingUni(){
        firebaseFirestore.collection("UniversityRegistration").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        university = task.getResult().getString("university");
                        gettingAllThePosts(university);
                    }
                }
            }
        });
    }

    private void gettingAllThePosts(String University){

        Query query = firebaseFirestore.collection("Universities").document(University).collection("NoticeBoard")
                .orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(NoticeboadCarrie.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        NoticeBoardList noticeBoardList = doc.getDocument().toObject(NoticeBoardList.class).withId(postID);

                        noticeBoardLists.add(noticeBoardList);

                        noticeboadAdapter.notifyDataSetChanged();

                    }
                }
            }
        });


    }
}

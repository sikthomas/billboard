package com.glac.academics;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.glac.R;
import com.glac.ecommerce.PostItemDesc;
import com.glac.ecommerce.PotsItem;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Accademics extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,university;
    private View view;
    private TextView mTitle;
    private RecyclerView recyclerView;
    private List<SchoolMarketList> schoolMarketLists;
    private SchoolAdapter schoolAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_accademics, container, false);

        firebaseFirestore =FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        schoolMarketLists = new ArrayList<>();
        schoolAdapter =  new SchoolAdapter(schoolMarketLists);

        recyclerView = (RecyclerView)view.findViewById(R.id.post_list_school);
        mTitle = (TextView)view.findViewById(R.id.tvUniversity);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_school);
        mPost = (FloatingActionButton)view.findViewById(R.id.floating_school);

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),MyClassAvailablePosst.class));
            }
        });

        //setting dapter to thw recyclerview

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(schoolAdapter);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SchoolPosting.class);
                v.getContext().startActivity(intent);
            }
        });

        checkingRegistration(user_id,view);

        //getting the university
        firebaseFirestore.collection("UniversityRegistration").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        university = task.getResult().getString("university");
                        gettingThePosts(university);
                    }
                }
            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(schoolAdapter);

                    }
                },300);
            }
        });


        return view;
    }

   private void checkingRegistration(String userId, final View v) {
        try
        {
            firebaseFirestore.collection("UniversityRegistration").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (!task.getResult().exists()){
                        Intent intent = new Intent(v.getContext(), SchoolRegister.class);
                        v.getContext().startActivity(intent);
                    }else {
                        String uni =task.getResult().getString("university");
                        mTitle.setText(uni.toUpperCase());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void gettingThePosts(String university){
        Query query = firebaseFirestore.collection("Universities").document(university).collection("SchoolMarket").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        SchoolMarketList schoolMarketList = doc.getDocument().toObject(SchoolMarketList.class).withId(postID);

                        schoolMarketLists.add(schoolMarketList);

                        schoolAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), "No post for ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}

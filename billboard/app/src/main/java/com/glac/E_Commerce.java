package com.glac;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class E_Commerce extends Fragment {
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private View view;
    private List<PotsItem> potsItems;
    private PostAdapter postAdapter;

    private FirebaseFirestore firebaseFirestore;
    private Boolean isRecentPost = true;
    private DocumentSnapshot lastVisible;
    private String user_country;
    private String current_user_id;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_e__commerce, container, false);
        potsItems = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.post_list);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);

        postAdapter = new PostAdapter(potsItems);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);
        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("HotDeals").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        PotsItem postItem = doc.getDocument().toObject(PotsItem.class).withId(postID);

                        potsItems.add(postItem);

                        postAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), "No post for ", Toast.LENGTH_SHORT).show();
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
                        recyclerView.setAdapter(postAdapter);

                    }
                },300);
            }
        });


        return  view;
    }
}

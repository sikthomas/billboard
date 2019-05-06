package com.glac;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
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
public class Lists extends Fragment {

    private View view;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private String user_id;
    private driver_Adapter driver_adapter;
    private List<driver_List> driver_lists;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_lists, container, false);
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_list);
        driver_lists=new ArrayList<>();
        driver_adapter=new driver_Adapter(driver_lists);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(driver_adapter);


        Query query=firebaseFirestore.collection("Drivers").orderBy("fname",Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String postID = doc.getDocument().getId();
                        driver_List driver_list = doc.getDocument().toObject(driver_List.class).withId(postID);
                        driver_lists.add(driver_list);
                        driver_adapter.notifyDataSetChanged();
                    }
                }
            }
        });






        return view;
    }

}

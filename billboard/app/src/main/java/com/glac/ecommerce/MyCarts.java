package com.glac.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.glac.R;
import com.glac.ecommerce.Carts.CartAdapter;
import com.glac.ecommerce.Carts.CartList;
import com.glac.ecommerce.Carts.DbAdapter;
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

public class MyCarts extends Fragment {
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private View view;
    private List<CartList> cartLists;
    private CartAdapter cartAdapter;

    private FirebaseFirestore firebaseFirestore;
    private Boolean isRecentPost = true;
    private DocumentSnapshot lastVisible;
    private String user_country;
    private String current_user_id;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_carts, container, false);



        cartLists = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.post_list1);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe1);
        cartAdapter = new CartAdapter(cartLists);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAllList(getContext());



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(cartAdapter);

                    }
                },300);
            }
        });

        return view;
    }

    public void getAllList(Context context){
        DbAdapter dbAdapter = new DbAdapter(getContext());
        dbAdapter.openDb();
        Cursor cursor = dbAdapter.retrieve();
        CartList cartList = null;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String price = cursor.getString(2);

            cartList = new CartList();
            cartList.setId(id);
            cartList.setPrice(price);
            cartList.setTitle(title);

            cartLists.add(cartList);
        }

        dbAdapter.closeDb();
        recyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

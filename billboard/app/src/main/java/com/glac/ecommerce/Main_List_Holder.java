package com.glac.ecommerce;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.glac.Login;
import com.glac.MainPanelActivity;
import com.glac.R;
import com.glac.academics.MyClassAvailablePosst;
import com.glac.academics.SchoolRegister;
import com.glac.account.MyAccountSetting;
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

public class Main_List_Holder extends AppCompatActivity {
    String category;
    private FirebaseAuth auth;
    private RecyclerView recyclerView1,recyclerView2;
    private List<PotsItem> potsItems;
    private PostAdapter postAdapter;

    private FirebaseFirestore firebaseFirestore;
    private Boolean isRecentPost = true;
    private DocumentSnapshot lastVisible;
    private SearchView searchView;
    private String user_country;
    private String current_user_id;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__list__holder);

        //getting the value
        category = getIntent().getExtras().getString("value");
        setTitle(category);

        potsItems = new ArrayList<>();
        recyclerView2 = (RecyclerView)findViewById(R.id.post_list_two);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe11);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        postAdapter = new PostAdapter(potsItems);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Main_List_Holder.this,2);
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(postAdapter);


        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("HotDeals").document("Categories").collection(category).orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(Main_List_Holder.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        PotsItem postItem = doc.getDocument().toObject(PotsItem.class).withId(postID);

                        potsItems.add(postItem);

                        postAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(Main_List_Holder.this, "No post for ", Toast.LENGTH_SHORT).show();
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
                        recyclerView2.setAdapter(postAdapter);

                    }
                },300);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.items_holder_menu, menu);// Associate searchable configuration with the SearchView

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                postAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                postAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}

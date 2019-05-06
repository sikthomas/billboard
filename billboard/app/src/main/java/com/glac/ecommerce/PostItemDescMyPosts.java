package com.glac.ecommerce;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.Chowder;
import com.glac.R;
import com.glac.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostItemDescMyPosts extends AppCompatActivity {
    private TextView mTitle,mDesc,mLocation,mCounty,mPrice;
    private ImageView imagePostItem;
    String price,location,email,county,phonenumber,desc,title,phoneWhoToPay,user_id;

    Chowder chowder;
    String PAYBILL_NUMBER = "898998";
    String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";

    String productId = Utils.generateProductId();
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fbAddCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item_description_my_posts);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mTitle = (TextView)findViewById(R.id.tvTitleDesc1);
        fbAddCart = (FloatingActionButton)findViewById(R.id.floatAddCart1);
        mDesc = (TextView)findViewById(R.id.tvDescDesc1);
        mLocation = (TextView)findViewById(R.id.tvLocationDesc1);
        mCounty = (TextView)findViewById(R.id.tvCountyDesc1);
        imagePostItem = (ImageView)findViewById(R.id.imageView31);
        mPrice = (TextView)findViewById(R.id.tvPriceDsc1);

        //getting passed values
        price =getIntent().getExtras().getString("price");
        location = getIntent().getExtras().getString("location");
        county = getIntent().getExtras().getString("county");
        desc = getIntent().getExtras().getString("desc");
        phonenumber =getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        title = getIntent().getExtras().getString("title").toUpperCase();
        String image_url = getIntent().getExtras().getString("image_url");

        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(PostItemDescMyPosts.this).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePostItem);

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        setTitle(title);

        //setting the values to the coresponding items

        mLocation.setText(location);
        mCounty.setText(county+" (County)");
        mDesc.setText(desc);
        mTitle.setText(title);
        mPrice.setText("KSH "+price);

        fbAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }
}

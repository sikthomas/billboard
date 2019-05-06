package com.glac.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.Chowder;
import com.glac.R;
import com.glac.ecommerce.Carts.DbAdapter;
import com.glac.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwarachael on 1/28/2019.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {

    private Chowder chowder;
    private String PAYBILL_NUMBER = "898998";
    private String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";
    private  FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    String productId = Utils.generateProductId();

    private List<PotsItem> potsItems;
    private Context context;
    private List<PotsItem> postFiltered;

    public PostAdapter(List<PotsItem> potsItems) {
        this.potsItems = potsItems;
        this.postFiltered = potsItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_display, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        final String user_id = auth.getCurrentUser().getUid();

        final String image_url = postFiltered.get(position).getImage_url();
        holder.setImagePosted(image_url);

        final String Title = postFiltered.get(position).getTitle();
        holder.setTitle(Title);

        final String Price = postFiltered.get(position).getPrice();
        holder.setPrice("KSH "+Price);

        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               holder.addintToCart(Title,Price);

            }
        });

        final String Location = postFiltered.get(position).getLocation();
        final String Description = postFiltered.get(position).getDesc_val();
        final String Phone = postFiltered.get(position).getPhone();
        final String County = postFiltered.get(position).getCounty();
        final String Email = postFiltered.get(position).getEmail();
        final String PriceDiscount= postFiltered.get(position).getPricediscount();
        final String postId = postFiltered.get(position).PostId;

        holder.setPriceB4("KSH "+PriceDiscount);
        holder.PriceB4.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        holder.imagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostItemDesc.class);
                intent.putExtra("title",Title);
                intent.putExtra("price",Price);
                intent.putExtra("location",Location);
                intent.putExtra("desc",Description);
                intent.putExtra("phone",Phone);
                intent.putExtra("county",County);
                intent.putExtra("email",Email);
                intent.putExtra("image_url",image_url);
                intent.putExtra("pricediscount",PriceDiscount);
                view.getContext().startActivity(intent);

            }
        });



        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String admin = task.getResult().getString("admin");
                        if (admin.equals("admin")){
                            holder.mEdit.setVisibility(View.VISIBLE);
                            holder.mEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), PostEditing.class);
                                    intent.putExtra("title",Title);
                                    intent.putExtra("price",Price);
                                    intent.putExtra("location",Location);
                                    intent.putExtra("desc",Description);
                                    intent.putExtra("phone",Phone);
                                    intent.putExtra("county",County);
                                    intent.putExtra("email",Email);
                                    intent.putExtra("image_url",image_url);
                                    intent.putExtra("pricediscount",PriceDiscount);
                                    intent.putExtra("postID",postId);
                                    v.getContext().startActivity(intent);

                                }
                            });
                        }
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return postFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    postFiltered = potsItems;
                } else {
                    List<PotsItem> filteredList = new ArrayList<>();
                    for (PotsItem row : potsItems) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getCounty().contains(charSequence)) {
                           filteredList.add(row);
                        }
                    }

                    postFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = postFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                postFiltered = (ArrayList<PotsItem>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private TextView Price,Title,mEdit,PriceB4;
        private Button addCart,buy;
        private ImageView imagePosted;

        public ViewHolder(View itemView) {


            super(itemView);
            view = itemView;
            addCart = (Button)view.findViewById(R.id.btnAddCart);
            mEdit = (TextView)view.findViewById(R.id.tvPpostEdit);
        }
        public void setPrice(String price){

            Price = (TextView)view.findViewById(R.id.tvPriceItem);
            Price.setText(price);
        }
        public void setPriceB4(String price){

            PriceB4 = (TextView)view.findViewById(R.id.tvPriceItemBefore);
            PriceB4.setText(price);
        }
        public void setTitle(String title){


            Title = (TextView)view.findViewById(R.id.tvItemTitle);
            Title.setText(title);
        }

        public void setImagePosted(String image_url){
            imagePosted = (ImageView)view.findViewById(R.id.imageView2);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePosted);
        }

        public void addintToCart(String title, String price){

            DbAdapter dbAdapter = new DbAdapter(context);
            dbAdapter.openDb();
            boolean save = dbAdapter.add(title,price);

            if (save){
                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
            }
            dbAdapter.closeDb();
        }


    }
}

package com.glac;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwarachael on 1/28/2019.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements PostAdapter1 {

    private Chowder chowder;
    private String PAYBILL_NUMBER = "898998";
    private String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";
    private  FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    String productId = Utils.generateProductId();

    public List<PotsItem> potsItems;
    public Context context;
    private List<PotsItem> postFiltered;

    public PostAdapter(List<PotsItem> potsItems) {
        this.potsItems = potsItems;
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
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        final String user_id = auth.getCurrentUser().getUid();

        final String image_url = potsItems.get(position).getImage_url();
        holder.setImagePosted(image_url);

        final String Title = potsItems.get(position).getTitle();
        holder.setTitle(Title);

        final String Price = potsItems.get(position).getPrice();
        holder.setPrice("KSH "+Price);

        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("title",Title);
                stringMap.put("price",Price);
                stringMap.put("timeStamp", FieldValue.serverTimestamp());
                stringMap.put("user_id",user_id);

                firebaseFirestore.collection("Carts"+user_id).add(stringMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(context, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        final String Location = potsItems.get(position).getLocation();
        final String Description = potsItems.get(position).getDesc_val();
        final String Phone = potsItems.get(position).getPhone();
        final String County = potsItems.get(position).getCounty();
        final String Email = potsItems.get(position).getEmail();

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
                view.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return potsItems.size();
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
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getPrice().contains(charSequence)) {
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
        private TextView Price,Title;
        private Button addCart,buy;
        private ImageView imagePosted;
        private CardView cardViewPost;

        public ViewHolder(View itemView) {


            super(itemView);
            view = itemView;
            addCart = (Button)view.findViewById(R.id.btnAddCart);
        }
        public void setPrice(String price){

            Price = (TextView)view.findViewById(R.id.tvPriceItem);
            Price.setText(price);
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


    }
}

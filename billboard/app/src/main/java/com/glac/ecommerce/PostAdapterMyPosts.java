package com.glac.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.Chowder;
import com.glac.R;
import com.glac.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwarachael on 1/28/2019.
 */

public class PostAdapterMyPosts extends RecyclerView.Adapter<PostAdapterMyPosts.ViewHolder>  {

    private Chowder chowder;
    private String PAYBILL_NUMBER = "898998";
    private String PASSKEY = "ada798a925b5ec20cc331c1b0048c88186735405ab8d59f968ed4dab89da5515";
    private  FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    String productId = Utils.generateProductId();

    public List<PotsItemMyPosts> potsItems;
    public Context context;
    private List<PotsItemMyPosts> postFiltered;

    public PostAdapterMyPosts(List<PotsItemMyPosts> potsItems) {
        this.potsItems = potsItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_displaymypost, parent, false);
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

        final String Location = potsItems.get(position).getLocation();
        final String Description = potsItems.get(position).getDesc_val();
        final String Phone = potsItems.get(position).getPhone();
        final String County = potsItems.get(position).getCounty();
        final String Email = potsItems.get(position).getEmail();

        holder.imagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostItemDescMyPosts.class);
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

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    postFiltered = potsItems;
                } else {
                    List<PotsItemMyPosts> filteredList = new ArrayList<>();
                    for (PotsItemMyPosts row : potsItems) {

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
                postFiltered = (ArrayList<PotsItemMyPosts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private TextView Price,Title;
        private ImageView imagePosted;

        public ViewHolder(View itemView) {


            super(itemView);
            view = itemView;
        }
        public void setPrice(String price){

            Price = (TextView)view.findViewById(R.id.tvPriceItem1);
            Price.setText(price);
        }
        public void setTitle(String title){


            Title = (TextView)view.findViewById(R.id.tvItemTitle1);
            Title.setText(title);
        }

        public void setImagePosted(String image_url){
            imagePosted = (ImageView)view.findViewById(R.id.imageView21);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightGrray);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image_url).into(imagePosted);
        }


    }
}

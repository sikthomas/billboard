package com.glac;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by mwarachael on 1/29/2019.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    public List<CartList> cartLists;
    public Context context;

    public CartAdapter(List<CartList> cartLists) {
        this.cartLists=cartLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_items, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        final String postID = cartLists.get(position).PostId;
        final String user_id = auth.getCurrentUser().getUid();

        String Price = cartLists.get(position).getPrice();
        holder.setPrice(Price);

        String Title = cartLists.get(position).getTitle();
        holder.setTitle(Title);

        //removing
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firebaseFirestore.collection("Carts"+user_id).document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {

                                firebaseFirestore.collection("Carts"+user_id ).document(postID).delete();
                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, price;
        private Button btnRemove;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            btnRemove = (Button)view.findViewById(R.id.btnDeleteCartItem);
        }

        public void setTitle(String Title){
            title = (TextView)view.findViewById(R.id.tvTitleCartItem);
            title.setText(Title);
        }
        public void setPrice(String Price){
            price = (TextView)view.findViewById(R.id.tvPriceCartItem);
            price.setText(Price);
        }
    }
}

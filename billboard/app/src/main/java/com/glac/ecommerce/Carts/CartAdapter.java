package com.glac.ecommerce.Carts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.glac.R;
import com.glac.ecommerce.MyCarts;
import com.google.firebase.auth.FirebaseAuth;
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
    private MyCarts myCarts;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final int id = cartLists.get(position).getId();

        String Price = cartLists.get(position).getPrice();
        holder.setPrice(Price);

        String Title = cartLists.get(position).getTitle();
        holder.setTitle(Title);

        //removing
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                holder.removeCart(id);

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
            title = (TextView)view.findViewById(R.id.tvPriceCartItem);
            title.setText("Ksh "+Title);
        }
        public void setPrice(String Price){
            price = (TextView)view.findViewById(R.id.tvTitleCartItem);
            price.setText(Price);
        }

        public void removeCart(int id){
            DbAdapter dbAdapter = new DbAdapter(context);
            dbAdapter.openDb();
            boolean deleted = dbAdapter.remove(id);
            dbAdapter.closeDb();

            if (deleted){
                myCarts = new MyCarts();
                myCarts.onDetach();
                Toast.makeText(context, "Removed Reload Cart List", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

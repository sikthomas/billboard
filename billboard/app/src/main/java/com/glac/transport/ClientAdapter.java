package com.glac.transport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glac.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwarachael on 2/25/2019.
 */

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> implements Filterable {

    private Context  context;
    private List<ClientList> requestsLists;
    private List<ClientList> filterredRequest;

    public ClientAdapter(List<ClientList> requestsLists) {
        this.requestsLists = requestsLists;
        this.filterredRequest = requestsLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_item_display, parent, false);
        context = parent.getContext();
        return new ClientAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String fullname = filterredRequest.get(position).getFullname();
        final String plate = filterredRequest.get(position).getPlate();
        final String county = filterredRequest.get(position).getCounty();
        final String location = filterredRequest.get(position).getLocation();
        final String phone = filterredRequest.get(position).getPhone();
        final String category = filterredRequest.get(position).getCategory();
        final String postId = filterredRequest.get(position).PostId;
        final String owner_user_id = filterredRequest.get(position).getUser_id();
        final String availability = filterredRequest.get(position).getAvailability();

        //seting values
        holder.setAll(fullname,plate);

        if (availability.equals("Available")){
            holder.Availability.setText("Available");
        }else {
            holder.Availability.setText("Unavailable");
            holder.Availability.setTextColor(R.color.red);
        }

        //onclick item
        holder.mLinearRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visibility = "Available";
                String switchSeen = "Client";

                Intent i = new Intent(context,OwnerDetails.class);
                i.putExtra("county",county);
                i.putExtra("location",location);
                i.putExtra("fullname",fullname);
                i.putExtra("phone",phone);
                i.putExtra("category",category);
                i.putExtra("plate",plate);
                i.putExtra("postID",postId);
                i.putExtra("visibility",visibility);
                i.putExtra("owner_user_id",owner_user_id);
                i.putExtra("availability",availability);
                i.putExtra("switchSeen",switchSeen);
                v.getContext().startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return filterredRequest.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterredRequest = requestsLists;
                } else {
                    List<ClientList> filteredList = new ArrayList<>();
                    for (ClientList row : requestsLists) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getFullname().contains(charSequence) || row.getCounty().contains(charSequence) || row.getPlate().contains(charSequence) || row.getAvailability().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filterredRequest = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterredRequest;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterredRequest = (ArrayList<ClientList>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView Fullname, Plate,Availability;
        private LinearLayout mLinearRequest;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            mLinearRequest = (LinearLayout)view.findViewById(R.id.linearRequest);
            Availability =(TextView)view.findViewById(R.id.tv_client_availability);
        }

        public void setAll(String fullname,String plate) {
            Fullname = (TextView)view.findViewById(R.id.tv_client_fullname);
            Plate =(TextView)view.findViewById(R.id.tv_client_plate);

            Fullname.setText(fullname);
            Plate.setText(plate);
        }
    }
}

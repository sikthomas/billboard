package com.glac;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class driver_Adapter extends RecyclerView.Adapter<driver_Adapter.ViewHolder> {

    private Context context;
    private List<driver_List> driver_lists;

    public driver_Adapter(List<driver_List> driver_lists) {
        this.driver_lists = driver_lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.available_people,viewGroup,false);
        context=viewGroup.getContext();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String fname=driver_lists.get(i).getFname();
        String lname=driver_lists.get(i).getLname();
        String fullname=fname+" "+lname;
        viewHolder.setName(fullname);

        String localArea=driver_lists.get(i).getLarea();
        viewHolder.setLocalArea(localArea);


    }

    @Override
    public int getItemCount() {
        return driver_lists.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,localArea,availability;
        private View view;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            view=itemView;

        }
        public void setName(String Name){
            name= (TextView) view.findViewById(R.id.tvname_transport);
            name.setText(Name);
        }
        public void setLocalArea(String Local){
            localArea=(TextView) view.findViewById(R.id.tvlocalarea_transport);
            localArea.setText(Local);

        }
        public void setAvailability(String Availability){
            availability=(TextView)view.findViewById(R.id.tvavailability_transport);
            availability.setText(Availability);
        }
    }
}

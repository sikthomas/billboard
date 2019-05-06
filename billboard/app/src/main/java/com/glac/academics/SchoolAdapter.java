package com.glac.academics;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.R;
import com.glac.ecommerce.PostAdapter;
import com.glac.ecommerce.PostItemDesc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

/**
 * Created by mwarachael on 2/6/2019.
 */

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.ViewHolder> {
    private Context context;
    private List<SchoolMarketList> schoolMarketLists;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    public SchoolAdapter(List<SchoolMarketList> schoolMarketLists) {
        this.schoolMarketLists = schoolMarketLists;
    }

    @Override
    public SchoolAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_school_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new SchoolAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SchoolAdapter.ViewHolder holder, int position) {

        final String fullname = schoolMarketLists.get(position).getFullname();
        final String regno = schoolMarketLists.get(position).getRegno();
        final String title  = schoolMarketLists.get(position).getTitle();
        final String imageUrl = schoolMarketLists.get(position).getImageUrl();
        final String desc = schoolMarketLists.get(position).getDesc();
        Date date = schoolMarketLists.get(position).getTimeStamp();
        final String phone = schoolMarketLists.get(position).getPhone();
        final String user_id = schoolMarketLists.get(position).getUser_id();

        //setting them values to the item dapater
        holder.setmFullname(fullname);
        holder.setmRegno(regno);
        holder.setmTtitle(title);
        holder.setmImagePosted(imageUrl);

        //seting onClickListener to the imagePosition
        holder.mImagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), SchoolItemDesc.class);
                intent.putExtra("title",title);
                intent.putExtra("desc",desc);
                intent.putExtra("phone",phone);
                intent.putExtra("image_url",imageUrl);
                intent.putExtra("regno",regno);
                intent.putExtra("fullname",fullname);
                intent.putExtra("user_id",user_id);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return schoolMarketLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImagePosted;
        private TextView mTtitle,mTime,mDesc,mRegno,mFullname;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        public void setmTtitle(String Title){
            mTtitle = (TextView)view.findViewById(R.id.tv_post_postItem);
            mTtitle.setText(Title);
        }
        public void setmTime(String Time){
            mTime = (TextView)view.findViewById(R.id.tv_time_postItem);
            mTime.setText(Time);
        }
        public void setmRegno(String Regno){
            mRegno = (TextView)view.findViewById(R.id.tv_regno_postItem);
            mRegno.setText(Regno);
        }
        public void setmFullname(String Title){
            mFullname = (TextView)view.findViewById(R.id.tv_fullname_postItem);
            mFullname.setText(Title);
        }
        public void setmImagePosted(String Image_Url){
            mImagePosted = (ImageView) view.findViewById(R.id.img_posted_item);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightgray);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(Image_Url).into(mImagePosted);
        }
    }
}

package com.glac.academics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mwarachael on 2/7/2019.
 */

public class MyClassPostAdapter extends RecyclerView.Adapter<MyClassPostAdapter.ViewHolder> {
    private List<MyClassPostList> myClassPostLists;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public MyClassPostAdapter(List<MyClassPostList> myClassPostLists) {
        this.myClassPostLists = myClassPostLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_class_post_item_display, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new MyClassPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String posted_image_url =myClassPostLists.get(position).getImageUrl();
        final String title = myClassPostLists.get(position).getTitle();
        String desc = myClassPostLists.get(position).getPost();
        String owner_user_id = myClassPostLists.get(position).getUser_id();
        final String postId = myClassPostLists.get(position).PostId;
        String user_id = auth.getCurrentUser().getUid();

        //setting the values
        holder.setmDesc(desc);

        if (posted_image_url.equals("NoImage")){
            holder.mImagePosted.setVisibility(View.GONE);
        }else {
            holder.setmImagePosted(posted_image_url);
        }
        holder.setmTitle(title);

        //getting post owner user profile
        firebaseFirestore.collection("Users").document(owner_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String imageUrl = task.getResult().getString("imageUrl");
                        holder.setmUserProfileImage(imageUrl);
                    }
                }
            }
        });

        //getting user regno
        firebaseFirestore.collection("UniversityRegistration").document(owner_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String regno = task.getResult().getString("regno");
                                holder.setmUserReg(regno.toUpperCase());
                            }
                        }
                    }
                });

        holder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  i = new Intent(v.getContext(), Comment.class);
                i.putExtra("postId",postId);
                i.putExtra("title",title);
                v.getContext().startActivity(i);
            }

        });

        holder.mImagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  i = new Intent(v.getContext(), Comment.class);
                i.putExtra("postId",postId);
                i.putExtra("title",title);
                v.getContext().startActivity(i);

            }
        });
        holder.mDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent  i = new Intent(v.getContext(), Comment.class);
                i.putExtra("postId",postId);
                i.putExtra("title",title);
                v.getContext().startActivity(i);
            }
        });

        //getting comments count

        firebaseFirestore.collection("UniversityRegistration").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){

                                String  university = task.getResult().getString("university");
                                String course = task.getResult().getString("course");
                                String yos = task.getResult().getString("yos");

                                firebaseFirestore.collection("Universities").document(university)
                                        .collection("ClassPosts").document(course).collection(yos).document(postId)
                                        .collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                        if (!documentSnapshots.isEmpty()) {

                                            int count = documentSnapshots.size();
                                            holder.setmComments(count+" Comment(s)");

                                        } else {

                                            holder.setmComments(0+" Comment(s)");

                                        }

                                    }
                                });
                            }
                        }
                    }
                });

        holder.mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  i = new Intent(v.getContext(), Comment.class);
                i.putExtra("postId",postId);
                i.putExtra("title",title);
                v.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return myClassPostLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView mUserReg,mTitle,mDesc,mComments;
        private ImageView mImagePosted,mComment;
        private CircleImageView mUserProfileImage;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mImagePosted = (ImageView)view.findViewById(R.id.image_myclass_posted);
            mComment = (ImageView)view.findViewById(R.id.my_class_comment_item);
            mDesc = (TextView)view.findViewById(R.id.tv_myclass_desc);
            mComments = (TextView)view.findViewById(R.id.tv_my_class_comments);

        }

        public void setmTitle(String title){
            mTitle = (TextView)view.findViewById(R.id.tv_myclass_title);
            mTitle.setText(title);
        }
        public void setmDesc(String desc){
            mDesc = (TextView)view.findViewById(R.id.tv_myclass_desc);
            mDesc.setText(desc);
        }
        public void setmUserReg(String regno){
            mUserReg = (TextView)view.findViewById(R.id.tv_myclass_regno);
            mUserReg.setText(regno);
        }
        public void setmImagePosted(String ImagePostedUrl){
            mImagePosted = (ImageView)view.findViewById(R.id.image_myclass_posted);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightgray);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(ImagePostedUrl).into(mImagePosted);
        }
        public void setmUserProfileImage(String UserImageUrl){
            mUserProfileImage = (CircleImageView) view.findViewById(R.id.image_profile_myclass);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.placeholder(R.color.lightgray);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(UserImageUrl).into(mUserProfileImage);

        }

        public void setmComments(String  Comments) {


            mComments = (TextView)view.findViewById(R.id.tv_my_class_comments);
            mComments.setText(Comments);

        }
    }

}

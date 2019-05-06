package com.glac.academics;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by mwarachael on 2/10/2019.
 */

public class NoticeboadAdapter extends RecyclerView.Adapter<NoticeboadAdapter.ViewHolder> {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<NoticeBoardList> noticeBoardLists;
    private Context context;

    public NoticeboadAdapter(List<NoticeBoardList> noticeBoardLists) {
        this.noticeBoardLists = noticeBoardLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noticeboard_item_display, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new NoticeboadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String user_id_owner = noticeBoardLists.get(position).getUser_id();
        String post = noticeBoardLists.get(position).getPost();
        final String docLink = noticeBoardLists.get(position).getFileUri();

        //getting the owner user id
        firebaseFirestore.collection("Users").document(user_id_owner)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname = task.getResult().getString("fname");
                        String lname = task.getResult().getString("lname");
                        String fullname = fname+" "+lname;
                        holder.setmFullName(fullname);
                    }
                }

            }
        });

        if (docLink.equals("NoFile")){
            holder.mDoc.setVisibility(View.GONE);
        }else {
            holder.mDoc.setVisibility(View.VISIBLE);
        }

        //setting the post

        holder.setmPost(post);
        holder.setmDoc("Doc Link");

        holder.mDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(docLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return noticeBoardLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFullName,mPost,mDoc;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mFullName = (TextView)view.findViewById(R.id.tv_fullname_noticeboard);
            mPost = (TextView)view.findViewById(R.id.tv_post_noticeboard);
            mDoc = (TextView)view.findViewById(R.id.tv_doc_noticeboard);
        }

        public void setmFullName(String FullName) {
            mFullName = (TextView)view.findViewById(R.id.tv_fullname_noticeboard);
            mFullName.setText(FullName);

        }

        public void setmPost(String Post) {
            mPost = (TextView)view.findViewById(R.id.tv_post_noticeboard);
            mPost.setText(Post);

        }

        public void setmDoc(String Link){

            mDoc = (TextView)view.findViewById(R.id.tv_doc_noticeboard);
            mDoc.setText(Link);
        }
    }
}

package com.glac.academics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glac.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by mwarachael on 2/9/2019.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<CommentList> commentLists;
    private Context context;

    public CommentAdapter(List<CommentList> commentLists) {
        this.commentLists = commentLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_display, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String regno = commentLists.get(position).getRegno();
        String comment = commentLists.get(position).getComment();

        holder.setmComment(comment);
        holder.setmRegno(regno);

    }

    @Override
    public int getItemCount() {
        return commentLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mComment,mRegno;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setmComment(String Comment) {
            mComment = (TextView)view.findViewById(R.id.tv_comment_comment);
            mComment.setText(Comment);
        }

        public void setmRegno(String Regno) {
            mRegno = (TextView)view.findViewById(R.id.tv_comment_regno);
            mRegno.setText(Regno);
        }
    }
}

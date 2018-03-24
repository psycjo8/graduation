package kr.ac.kpu.block.smared;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    List<Friend> mFriend;  // email,photo,key 저장
    String stEmail;
    Context context;

    SharedPreferences sharedPreferences;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvNickname;
        public ImageView ivUser;



        public ViewHolder(View itemView) {
            super(itemView);
            tvNickname  = (TextView) itemView.findViewById(R.id.tvNickname);
            ivUser = (ImageView)itemView.findViewById(R.id.ivUser);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendAdapter(List<Friend> mFriend , Context context) {
        this.mFriend = mFriend;
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_friend, parent, false);

        sharedPreferences = context.getSharedPreferences("email",Context.MODE_PRIVATE);
        stEmail =  sharedPreferences.getString("email","");

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

            holder.tvNickname.setText(mFriend.get(position).getNickname());
            String stPhoto = mFriend.get(position).getPhoto();

            if (TextUtils.isEmpty(stPhoto)) {

            } else {
                Picasso.with(context).load(stPhoto).fit().centerInside().into(holder.ivUser);

            }
        }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriend.size();
    }
}
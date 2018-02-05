package kr.ac.kpu.block.smared;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mDataset;
    List<Chat> mChat;
    String stEmail;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.mTextView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter( List<Chat> mChat , String email) {
        this.mChat = mChat;
        this.stEmail = email;
    }

    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getEmail().equals(stEmail)) {
            return 1;
        } else {
            return 2;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_text_view, parent, false);
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mChat.get(position).getText());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
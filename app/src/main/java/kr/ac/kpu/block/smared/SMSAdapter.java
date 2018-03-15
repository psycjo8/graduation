package kr.ac.kpu.block.smared;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.ViewHolder> {

    List<String> mBody;

    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case


        public Button btnSMSDay;
        public TextView tvSMSBody;


        public ViewHolder(View itemView) {
            super(itemView);
            btnSMSDay = (Button) itemView.findViewById(R.id.btnSMSDay);
            tvSMSBody= (TextView) itemView.findViewById(R.id.tvSMSBody);
        }
    }

    public SMSAdapter(List<String> mBody , Context context) {
        this.mBody = mBody;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SMSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sms, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.tvSMSBody.setText(mBody.get(position).toString());

        }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBody.size();
    }
}
package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.ViewHolder> {

    List<Ledger> mLedger;
    String parsing;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public Button btnDelete;
        public Button btnEdit;
        public Button btnDay;
        public TextView tvUseitem;
        public TextView tvPrice;
        public TextView tvPaymemo;
        public TextView tvChoice;

        public ViewHolder(View itemView) {
            super(itemView);
            btnDay = (Button) itemView.findViewById(R.id.btnDay);
            tvUseitem= (TextView) itemView.findViewById(R.id.tvUseitem);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvPaymemo = (TextView) itemView.findViewById(R.id.tvPaymemo);
            tvChoice = (TextView) itemView.findViewById(R.id.tvChoice);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
        }
    }

    public LedgerAdapter(List<Ledger> mLedger , Context context) {
        this.mLedger = mLedger;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
         return 2;
        } else {
            if (mLedger.get(position).getYear().equals(mLedger.get(position - 1).getYear()) &&
                    mLedger.get(position).getMonth().equals(mLedger.get(position - 1).getMonth()) &&
                    mLedger.get(position).getDay().equals(mLedger.get(position - 1).getDay())) {
                return 1;
            } else {
                return 2;
            }
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public LedgerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v;

        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_content, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_ledger, parent, false);
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (position == 0) {
            holder.btnDay.setText(mLedger.get(position).getYear() + "-" + mLedger.get(position).getMonth() + "-" + mLedger.get(position).getDay());
            holder.tvChoice.setText("[ " + mLedger.get(position).getClassfy() + " ]");
            holder.tvUseitem.setText("분류 : " + mLedger.get(position).getUseItem());
            holder.tvPrice.setText("가격 : " + mLedger.get(position).getPrice() + "원");
            holder.tvPaymemo.setText("내용 : " + mLedger.get(position).getPaymemo());
        } else {
            if (mLedger.get(position).getYear().equals(mLedger.get(position - 1).getYear()) &&
                    mLedger.get(position).getMonth().equals(mLedger.get(position - 1).getMonth()) &&
                    mLedger.get(position).getDay().equals(mLedger.get(position - 1).getDay())) {
                holder.tvChoice.setText("[ " + mLedger.get(position).getClassfy() + " ]");
                holder.tvUseitem.setText("분류 : " + mLedger.get(position).getUseItem());
                holder.tvPrice.setText("가격 : " + mLedger.get(position).getPrice() + "원");
                holder.tvPaymemo.setText("내용 : " + mLedger.get(position).getPaymemo());
            } else {
                holder.btnDay.setText(mLedger.get(position).getYear() + "-" + mLedger.get(position).getMonth() + "-" + mLedger.get(position).getDay());
                holder.tvChoice.setText("[ " + mLedger.get(position).getClassfy() + " ]");
                holder.tvUseitem.setText("분류 : " + mLedger.get(position).getUseItem());
                holder.tvPrice.setText("가격 : " + mLedger.get(position).getPrice() + "원");
                holder.tvPaymemo.setText("내용 : " + mLedger.get(position).getPaymemo());
            }
        }
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EditDialog dialog = new EditDialog(context);
            dialog.show();

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLedger.size();
    }
}
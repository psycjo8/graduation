package kr.ac.kpu.block.smared;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.ViewHolder> {

    List<SMS> mBody;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    Context context;
    LedgerContent mledgerContent[] = new LedgerContent[1000];
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case


        public Button btnSMSDay;
        public TextView tvSMSPaymemo;
        public TextView tvSMSPrice;
        public TextView tvSMSTime;
        public Button btnAddSMS;

        public ViewHolder(View itemView) {
            super(itemView);
            btnSMSDay = (Button) itemView.findViewById(R.id.btnSMSDay);
            tvSMSPaymemo= (TextView) itemView.findViewById(R.id.tvSMSPaymemo);
            tvSMSPrice= (TextView) itemView.findViewById(R.id.tvSMSPrice);
            tvSMSTime= (TextView) itemView.findViewById(R.id.tvSMSTime);
            btnAddSMS = (Button) itemView.findViewById(R.id.btnAddSMS);
        }
    }

    public SMSAdapter(List<SMS> mBody , Context context) {
        this.mBody = mBody;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SMSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        for (int i=0; i<1000; i++) {
            mledgerContent[i] = new LedgerContent();
        }
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

      holder.tvSMSPaymemo.setText(mBody.get(position).getPayMemo());
      holder.btnSMSDay.setText(mBody.get(position).getYear()+"-"+mBody.get(position).getMonth()+"-"+mBody.get(position).getDay());
      holder.tvSMSPrice.setText("-" + mBody.get(position).getPrice()+"원");
      holder.tvSMSTime.setText("[신한체크]" + mBody.get(position).getTime());

      mledgerContent[position].setPaymemo(mBody.get(position).getPayMemo());
      mledgerContent[position].setPrice(mBody.get(position).getPrice());
      mledgerContent[position].setUseItem("기타");
      holder.btnAddSMS.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              myRef.child(user.getUid()).child("Ledger").child(mBody.get(position).getYear()).child(mBody.get(position).getMonth()).child(mBody.get(position).getDay()).child("지출").child(mBody.get(position).getTime()).setValue(mledgerContent[position]);
              Toast.makeText(context, "가계부에 추가되었습니다.", Toast.LENGTH_SHORT).show();
          }
      });
        }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBody.size();
    }
}
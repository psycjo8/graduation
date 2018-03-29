package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.ViewHolder> {

    List<Ledger> mLedger;
    String parsing;
    Context context;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    String selectChatuid = "";

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

    public LedgerAdapter(List<Ledger> mLedger , Context context, String selectChatuid) {
        this.mLedger = mLedger;
        this.context = context;
        this.selectChatuid = selectChatuid;
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

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                EditDialog dialogs = new EditDialog(context, mLedger, position, selectChatuid);
                dialogs.show();
                dialogs.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        holder.tvChoice.setText(dialogs.getStClassfy());
                        holder.tvUseitem.setText("분류 : " + dialogs.getStUseitem());
                        holder.tvPrice.setText("가격 : " + dialogs.getStPrice()+"원");
                        holder.tvPaymemo.setText("내용 : " + dialogs.getStPaymemo());
                    }
                });
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectChatuid.equals("")) {
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                    alertdialog.setMessage("정말 삭제 하시겠습니까?");
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myRef.child(user.getUid()).child("Ledger").child(mLedger.get(position).getYear())
                                    .child(mLedger.get(position).getMonth())
                                    .child(mLedger.get(position).getDay())
                                    .child("지출")
                                    .child(mLedger.get(position).getTimes())
                                    .removeValue();
                            Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = alertdialog.create();
                    alert.show();
                }
                else {
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                    TextView textView = new TextView(context);
                    alertdialog.setMessage("정말 삭제 하시겠습니까?");
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chatRef.child(selectChatuid).child("Ledger").child(mLedger.get(position).getYear())
                                    .child(mLedger.get(position).getMonth())
                                    .child(mLedger.get(position).getDay())
                                    .child("지출")
                                    .child(mLedger.get(position).getTimes())
                                    .removeValue();
                            Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = alertdialog.create();
                    alert.show();
                }




            }
        });

        }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLedger.size();
    }
}
package kr.ac.kpu.block.smared;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;
import java.util.List;


/**
 * Created by psycj on 2018-03-28.
 */

public class EditDialog extends Dialog {

    List<Ledger> mLedger;
    int position;
    String selectChatuid="";
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;

    public String getStClassfy() {
        return stClassfy;
    }

    public void setStClassfy(String stClassfy) {
        this.stClassfy = stClassfy;
    }

    public String getStUseitem() {
        return stUseitem;
    }

    public void setStUseitem(String stUseitem) {
        this.stUseitem = stUseitem;
    }

    public String getStPrice() {
        return stPrice;
    }

    public void setStPrice(String stPrice) {
        this.stPrice = stPrice;
    }

    public String getStPaymemo() {
        return stPaymemo;
    }

    public void setStPaymemo(String stPaymemo) {
        this.stPaymemo = stPaymemo;
    }

    String stClassfy = "";
    String stUseitem = "";
    String stPrice = "";
    String stPaymemo = "";

    public EditDialog(Context context, List<Ledger> mLedger, int position, String selectChatuid) {
        super(context);
        this.mLedger = mLedger;
        this.position = position;
        this.selectChatuid = selectChatuid;
    }

            RadioButton rbIncome;
            RadioButton rbConsume;
            TextView date;
            Spinner useitem;
            EditText price;
            EditText payMemo;
            Button submit;
            Button dismiss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        setContentView(R.layout.dialog_edit);

        rbIncome = (RadioButton) findViewById(R.id.rbIncome);
        rbConsume = (RadioButton) findViewById(R.id.rbConsume);
        date = (TextView) findViewById(R.id.date);
        useitem = (Spinner) findViewById(R.id.useitem);
        price = (EditText) findViewById(R.id.price);
        payMemo = (EditText) findViewById(R.id.payMemo);
        submit = (Button) findViewById(R.id.submit);
        dismiss = (Button) findViewById(R.id.dismiss);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();


        if (mLedger.get(position).getClassfy().equals("지출")) {
            rbConsume.setChecked(true);
        } else {
            rbIncome.setChecked(true);
        }

        setSpinner();
        date.setText(mLedger.get(position).getYear() + "-" + mLedger.get(position).getMonth() + "-" + mLedger.get(position).getDay());
        price.setText(mLedger.get(position).getPrice());
        payMemo.setText(mLedger.get(position).getPaymemo());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hashtable<String, String> ledger   // HashTable로 연결
                        = new Hashtable<String, String>();
                ledger.put("useItem", useitem.getSelectedItem().toString());
                ledger.put("price", price.getText().toString());
                ledger.put("paymemo",payMemo.getText().toString());

                if(rbConsume.isChecked()) { stClassfy = "[ 지출 ]"; }
                else { stClassfy = "[ 수입 ]"; }

                stUseitem = useitem.getSelectedItem().toString();
                stPrice = price.getText().toString();
                stPaymemo = payMemo.getText().toString();

                if (selectChatuid.equals("")) {
                    if (rbConsume.isChecked()) {
                        myRef.child(user.getUid()).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("수입")
                                .child(mLedger.get(position).getTimes())
                                .removeValue();
                        myRef.child(user.getUid()).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("지출")
                                .child(mLedger.get(position).getTimes())
                                .setValue(ledger);
                    } else {
                        myRef.child(user.getUid()).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("지출")
                                .child(mLedger.get(position).getTimes())
                                .removeValue();
                        myRef.child(user.getUid()).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("수입")
                                .child(mLedger.get(position).getTimes())
                                .setValue(ledger);
                    }
                } else {
                    if (rbConsume.isChecked()) {
                        chatRef.child(selectChatuid).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("수입")
                                .child(mLedger.get(position).getTimes())
                                .removeValue();
                        chatRef.child(selectChatuid).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("지출")
                                .child(mLedger.get(position).getTimes())
                                .setValue(ledger);
                    } else {
                        chatRef.child(selectChatuid).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("지출")
                                .child(mLedger.get(position).getTimes())
                                .removeValue();
                        chatRef.child(selectChatuid).child("Ledger").child(mLedger.get(position).getYear())
                                .child(mLedger.get(position).getMonth())
                                .child(mLedger.get(position).getDay())
                                .child("수입")
                                .child(mLedger.get(position).getTimes())
                                .setValue(ledger);
                    }
                }
                Toast.makeText(getContext(), "가계부가 수정되었습니다", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public void setSpinner() {
        if(mLedger.get(position).getUseItem().equals("의류비")) {
            useitem.setSelection(0);
        } else if(mLedger.get(position).getUseItem().equals("식비")) {
            useitem.setSelection(1);
        } else if (mLedger.get(position).getUseItem().equals("주거비")) {
            useitem.setSelection(2);
        } else if (mLedger.get(position).getUseItem().equals("교통비")) {
            useitem.setSelection(3);
        } else if (mLedger.get(position).getUseItem().equals("생필품")) {
            useitem.setSelection(4);
        } else if (mLedger.get(position).getUseItem().equals("기타")) {
            useitem.setSelection(5);
        }
    }
}

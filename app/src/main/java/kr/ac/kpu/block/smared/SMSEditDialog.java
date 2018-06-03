package kr.ac.kpu.block.smared;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Created by psycj on 2018-03-28.
 */

public class SMSEditDialog extends Dialog {


    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    String sms;
    long smsdate;
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
    String stYear;
    String stMonth;
    String stDay;
    public SMSEditDialog(Context context, String sms, long smsdate) {
        super(context);
        this.sms = sms;
        this.smsdate = smsdate;
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


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm:ss");
        String sdate;
        String fdate;
        sdate = df.format(smsdate);
        fdate = dft.format(smsdate);
        stYear = sdate.substring(0,4);
        stMonth = sdate.substring(5,7);
        stDay = sdate.substring(8,10);
        StringTokenizer tokenizer = new StringTokenizer(sms, " ");

        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();

        String smsprice = tokenizer.nextToken();
        smsprice.trim();
        smsprice = smsprice.replace(",","");
        smsprice = smsprice.replace("원","");
        String smspayMemo = tokenizer.nextToken();
        String smsstore = tokenizer.nextToken();
        if (smsstore.contains("잔액"))
        {

        } else {
            smspayMemo += smsstore;
        }

        price.setText(smsprice);
        payMemo.setText(smspayMemo);
        date.setText(sdate);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hashtable<String, String> ledger   // HashTable로 연결
                        = new Hashtable<String, String>();
                ledger.put("useItem", useitem.getSelectedItem().toString());
                ledger.put("price", price.getText().toString());
                ledger.put("paymemo",payMemo.getText().toString());


                if(rbConsume.isChecked()) {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("지출").child(fdate).setValue(ledger);
                }
                else {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("수입").child(fdate).setValue(ledger);
                }



                Toast.makeText(getContext(), "가계부가 추가되었습니다", Toast.LENGTH_SHORT).show();
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

}

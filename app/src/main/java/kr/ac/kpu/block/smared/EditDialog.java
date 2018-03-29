package kr.ac.kpu.block.smared;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by psycj on 2018-03-28.
 */

public class EditDialog extends Dialog {

    public EditDialog(Context context) { super(context); }

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

    }
}

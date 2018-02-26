package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class ShareLedgerRegFragment extends android.app.Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    Context context;

    String stUseItem;
    String stPrice;
    String stPaymemo;
    String stChatname;
    String stEmail;
    String stUid;
    Calendar c = Calendar.getInstance(); // Firebase내에 날짜로 저장
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("M");
    SimpleDateFormat day = new SimpleDateFormat("d");
    String stYear = year.format(c.getTime());
    String stMonth = month.format(c.getTime());
    String stDay = day.format(c.getTime());
    int saveItem;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();
        stChatname = "NULL";
        View v = inflater.inflate(R.layout.fragment_ledger_reg_share, container, false);

        Spinner spnUseitem = (Spinner) v.findViewById(R.id.spnUseitem2);
        Button btnSave = (Button) v.findViewById(R.id.btnSave2);
        final EditText etPrice = (EditText) v.findViewById(R.id.etPrice2);
        final EditText etPaymemo = (EditText) v.findViewById(R.id.etPaymemo2);
        CalendarView cvCalender = (CalendarView) v.findViewById(R.id.cvCalender2);
        final RadioButton rbConsume = (RadioButton) v.findViewById(R.id.rbConsume2);
        RadioButton rbIncome = (RadioButton) v.findViewById(R.id.rbIncome2);
        Button btnChoiceLed = (Button) v.findViewById(R.id.btnChoiceLed);
        Button btnOpenChat = (Button) v.findViewById(R.id.btnOpenChat);
        Button btnInvite = (Button) v.findViewById(R.id.btnInvite);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("email",Context.MODE_PRIVATE);
        stEmail = sharedPreferences.getString("email","");
        stUid = sharedPreferences.getString("uid","");

        spnUseitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stUseItem = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cvCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {  // 달력 선택, 날짜 입력
                stYear = Integer.toString(year);
                stMonth = Integer.toString(month+1);
                stDay = Integer.toString(day);
               Toast.makeText(getActivity(), stYear+"-"+stMonth+"-"+stDay, Toast.LENGTH_SHORT).show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stPrice = etPrice.getText().toString();
                stPaymemo = etPaymemo.getText().toString();
                c = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HHmmss");
                String stTime = time.format(c.getTime());
                Hashtable<String, String> ledger   // HashTable로 연결
                        = new Hashtable<String, String>();
                ledger.put("useItem", stUseItem);
                ledger.put("price", stPrice);
                ledger.put("paymemo",stPaymemo);


                if (rbConsume.isChecked()) {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("consume").child(stTime).setValue(ledger);
                } else {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("Income").child(stTime).setValue(ledger);
                }

                Toast.makeText(getActivity(), "저장하였습니다.", Toast.LENGTH_SHORT).show();
                etPrice.setText("");
                etPaymemo.setText("");


            }
        });

                btnChoiceLed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         CharSequence[] test={stChatname,stChatname,stEmail}; // 해야할거
                        AlertDialog.Builder alertdialog= new AlertDialog.Builder(getActivity());

                        alertdialog.setTitle("가계부를 골라주세요");
                        alertdialog.setSingleChoiceItems(test, -1, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {

                                        saveItem = item;
                                        // dialog.cancel();

                                    }
                                });


                        alertdialog.setNeutralButton("가계부 생성", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText editText = new EditText(getActivity());
                                AlertDialog.Builder alertdialog= new AlertDialog.Builder(getActivity());
                                alertdialog.setTitle("가계부 이름을 설정해주세요");
                                alertdialog.setView(editText);
                                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference pushedRef = chatRef.push();
                                        String chatId = pushedRef.getKey();

                                        Hashtable<String, String> makeChat   // HashTable로 연결
                                                = new Hashtable<String, String>();
                                        Map<String, Object> testMap = new HashMap<>();


                                    stChatname = editText.getText().toString();
                                        makeChat.put("chatname",stChatname);
                                    chatRef.child(chatId).setValue(makeChat);
                                    chatRef.child(chatId).child("user").child(stUid).setValue(stEmail);

                                        Toast.makeText(getActivity(), "가계부가 생성되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog alert = alertdialog.create();
                                alert.show();
                            }
                        });

                        alertdialog.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alert = alertdialog.create();
                        alert.show();

                    }
                });










        return v;
    }


}

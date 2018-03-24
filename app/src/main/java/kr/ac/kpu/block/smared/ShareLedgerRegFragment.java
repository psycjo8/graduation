package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class ShareLedgerRegFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    Context context;

    String stUseItem;
    String stPrice; // 금액
    String stPaymemo; // 내용
    String stChatname;// 채팅방 이름 = 가계부 이름
    String stEmail;
    String stUid;
    CharSequence selectChatname = "";
    String selectChatuid;
    String joinChatname;
    Calendar c = Calendar.getInstance(); // Firebase내에 날짜로 저장
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("M");
    SimpleDateFormat day = new SimpleDateFormat("d");
    String stYear = year.format(c.getTime());
    String stMonth = month.format(c.getTime());
    String stDay = day.format(c.getTime());
    int saveItem;
    List<String> listItems = new ArrayList<String>();




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

        spnUseitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 분류 선택
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stUseItem = (String) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cvCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {   // 달력 선택, 날짜 입력
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                stYear = Integer.toString(year);
                stMonth = Integer.toString(month+1);
                stDay = Integer.toString(day);
               Toast.makeText(getActivity(), stYear+"-"+stMonth+"-"+stDay, Toast.LENGTH_SHORT).show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {   // 저장버튼 클릭
            @Override
            public void onClick(View view) {
                if (selectChatname.toString().isEmpty()) {
                    Toast.makeText(getActivity(), "가계부를 선택후 이용해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    stPrice = etPrice.getText().toString();
                    stPaymemo = etPaymemo.getText().toString();
                    c = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("HHmmss");
                    String stTime = time.format(c.getTime());
                    Hashtable<String, String> ledger   // HashTable로 연결
                            = new Hashtable<String, String>();
                    ledger.put("useItem", stUseItem);
                    ledger.put("price", stPrice);
                    ledger.put("paymemo", stPaymemo);


                    if (rbConsume.isChecked()) {
                        chatRef.child(selectChatuid).child("Ledger").child(stYear).child(stMonth).child(stDay).child("지출").child(stTime).setValue(ledger);
                    } else {
                        chatRef.child(selectChatuid).child("Ledger").child(stYear).child(stMonth).child(stDay).child("수입").child(stTime).setValue(ledger);
                    }

                    Toast.makeText(getActivity(), "저장하였습니다.", Toast.LENGTH_SHORT).show();
                    etPrice.setText("");
                    etPaymemo.setText("");
                }

            }
        });

        viewLedgerName();



        btnChoiceLed.setOnClickListener(new View.OnClickListener() {   // 가계부 선택 버튼 클릭
                    @Override
                    public void onClick(View view) {


                        viewLedgerName();
                       final CharSequence[] select = listItems.toArray(new CharSequence[listItems.size()]);
                        AlertDialog.Builder alertdialog= new AlertDialog.Builder(getActivity());

                        alertdialog.setTitle("가계부를 골라주세요");
                        alertdialog.setSingleChoiceItems(select, -1, new DialogInterface.OnClickListener() {

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



                                    stChatname = editText.getText().toString();
                                        makeChat.put("chatname",stChatname);
                                    chatRef.child(chatId).setValue(makeChat);
                                    chatRef.child(chatId).child("user").child(stUid).setValue(stEmail);
                                        listItems.clear(); // 가계부 생성시 초기화 후 다시 가계부 뷰 리스트 채움
                                        viewLedgerName();
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
                                  selectChatname=select[saveItem];
                                  setChatUid();




                            }
                        });
                        alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alert = alertdialog.create();
                        alert.show();
                        listItems.clear();
                    }
                }); // 가계부 선택 버튼 종료


        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectChatname.toString().isEmpty()) {
                    Toast.makeText(getActivity(), "가계부를 선택후 이용해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
                    final EditText editText = new EditText(getActivity());
                    alertdialog.setTitle("초대할 이메일을 입력해주세요");
                    alertdialog.setView(editText);
                    alertdialog.setPositiveButton("초대", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot emailSnapshot : dataSnapshot.getChildren()) {
                                        if (editText.getText().toString().equals(emailSnapshot.child("email").getValue(String.class))) {
                                            inviteUser(emailSnapshot.child("key").getValue(String.class), emailSnapshot.child("email").getValue(String.class));  // CHATS에 UID 키 저장, 이메일 값 저장
                                            Toast.makeText(getActivity(), emailSnapshot.child("nickname").getValue(String.class) + "님을 " + selectChatname + " 가계부에 초대하였습니다.", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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
            }
        });

        btnOpenChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectChatname.toString().isEmpty()) {
                    Toast.makeText(getActivity(), "가계부를 선택후 이용해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String photo = dataSnapshot.child("photo").getValue(String.class);
                            String nickname = dataSnapshot.child("nickname").getValue(String.class);
                            Intent in = new Intent(getActivity(), ChatActivity.class);
                            in.putExtra("chatUid", selectChatuid);
                            in.putExtra("chatName", selectChatname);
                            in.putExtra("photo",photo);
                            in.putExtra("nickname",nickname);
                            startActivity(in);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });




        return v;
    }

    public void viewLedgerName() {

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot userSnapshot : chatSnapshot.getChildren()) {

                        for (DataSnapshot uidSnapshot : userSnapshot.getChildren())
                        {
                            if(uidSnapshot.getKey().equals(stUid)) {
                                joinChatname = chatSnapshot.child("chatname").getValue(String.class);
                                listItems.add(joinChatname);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void inviteUser(String uid, String email) {
        Map<String, Object> invite = new HashMap<>();
        invite.put(uid, email);
        chatRef.child(selectChatuid).child("user").updateChildren(invite);
    }

    public void setChatUid() {
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    if ( chatSnapshot.child("chatname").getValue(String.class).equals(selectChatname) ) {
                        selectChatuid = chatSnapshot.getKey();





                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

package kr.ac.kpu.block.smared;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShareLedgerViewFragment extends android.app.Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    LedgerAdapter mAdapter;
    LedgerAdapter tempAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    Ledger ledger[] = new Ledger[1000];

    int i =0;
    int totalIncome=0;
    int totalConsume=0;
    LedgerContent ledgerContent = new LedgerContent();
    List<Ledger> mLedger ; // 불러온 전체 가계부 목록
    List<Ledger> tempLedger ; // 불러온 부분 가계부 목록
    List<String> listItems = new ArrayList<String>();

    int index=0;  // 년,월 인덱스
    Set<String> selectMonth = new HashSet<String>(); // 년,월 중복제거용
    List<String> monthList; // 중복 제거된 년,월 저장
    String selectChatuid;
    String parsing;
    String joinChatname;
    CharSequence selectChatname = "";
    ArrayAdapter spinneradapter;

    ImageButton ibLastMonth; // 왼쪽 화살표
    TextView tvLedgerMonth; // 년,월 출력부
    ImageButton ibNextMonth; // 오른쪽 화살표
    TextView tvTotalconsume;
    TextView tvTotalincome;
    TextView tvPlusMinus;
    Spinner spnSelectLedger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        for (i=0; i<1000; i++) {
            ledger[i] = new Ledger();
        }
        i=0;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ledger_view_share, container, false);

        spnSelectLedger = (Spinner) v.findViewById(R.id.spnSelectLedger);
        ibLastMonth = (ImageButton) v.findViewById(R.id.ibLastMonth2);
        ibNextMonth = (ImageButton) v.findViewById(R.id.ibNextMonth2);
        tvLedgerMonth = (TextView) v.findViewById(R.id.tvLedgerMonth2);
        tvTotalincome = (TextView) v.findViewById(R.id.tvTotalincome2);
        tvTotalconsume = (TextView) v.findViewById(R.id.tvTotalconsume2);
        tvPlusMinus = (TextView) v.findViewById(R.id.tvPlusMinus2);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvLedger2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLedger = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new LedgerAdapter(mLedger, getActivity());
        mRecyclerView.setAdapter(mAdapter);


        ibLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempLedger = new ArrayList<>();
                if( index != 0) { // 년,월이 제일 처음이 아니면
                    index--;
                   tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                  for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));
                            if (mLedger.get(j).getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(mLedger.get(j).getPrice());
                            } else if (mLedger.get(j).getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(mLedger.get(j).getPrice());
                            }
                        }
                    }
                    tvTotalincome.setText("수입 합계 : " + totalIncome + "원");
                    tvTotalconsume.setText("지출 합계 : " + totalConsume + "원");
                    tvPlusMinus.setText("수익 : " + (totalIncome - totalConsume) + "원");
                    totalIncome=0;
                    totalConsume=0;
                    tempAdapter = new LedgerAdapter(tempLedger,getActivity());
                    mRecyclerView.setAdapter(tempAdapter);



                } else {   // 년,월이 처음이면
                    index = monthList.size() - 1;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));
                            if (mLedger.get(j).getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(mLedger.get(j).getPrice());
                            } else if (mLedger.get(j).getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(mLedger.get(j).getPrice());
                            }
                        }
                    }
                    tvTotalincome.setText("수입 합계 : " + totalIncome + "원");
                    tvTotalconsume.setText("지출 합계 : " + totalConsume + "원");
                    tvPlusMinus.setText("수익 : " + (totalIncome - totalConsume) + "원");
                    totalIncome=0;
                    totalConsume=0;
                    tempAdapter = new LedgerAdapter(tempLedger,getActivity());
                    mRecyclerView.setAdapter(tempAdapter);
                }
            }
        });

        ibNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempLedger = new ArrayList<>();
                if (index != monthList.size() - 1) { // 년, 월이 마지막이 아니면
                    index++;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));

                            if (mLedger.get(j).getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(mLedger.get(j).getPrice());
                            } else if (mLedger.get(j).getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(mLedger.get(j).getPrice());
                            }

                        }
                    }

                    tvTotalincome.setText("수입 합계 : " + totalIncome + "원");
                    tvTotalconsume.setText("지출 합계 : " + totalConsume + "원");
                    tvPlusMinus.setText("수익 : " + (totalIncome - totalConsume) + "원");
                    totalIncome=0;
                    totalConsume=0;
                    tempAdapter = new LedgerAdapter(tempLedger,getActivity());
                    mRecyclerView.setAdapter(tempAdapter);
                } else {   // 년,월이 마지막이면
                    index = 0;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));

                            if (mLedger.get(j).getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(mLedger.get(j).getPrice());
                            } else if (mLedger.get(j).getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(mLedger.get(j).getPrice());
                            }
                        }
                    }
                    tvTotalincome.setText("수입 합계 : " + totalIncome + "원");
                    tvTotalconsume.setText("지출 합계 : " + totalConsume + "원");
                    tvPlusMinus.setText("수익 : " + (totalIncome - totalConsume) + "원");
                    totalIncome=0;
                    totalConsume=0;
                    tempAdapter = new LedgerAdapter(tempLedger,getActivity());
                    mRecyclerView.setAdapter(tempAdapter);
                }
            }
        });


                myRef.child(user.getUid()).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tvLedgerMonth.setText("전체 가계부");
                            ledgerView(dataSnapshot); // 유저 가계부 전체 리스트 생성
                            monthList = new ArrayList(selectMonth); // 년 월만 빼서 따로 리스트 생성
                            Collections.sort(monthList);
                            if(monthList.isEmpty()) {

                            } else {
                                parsing = monthList.get(monthList.size() - 1).replaceAll("[^0-9]", "");
                                index = monthList.size() - 1;
                            }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        viewLedgerName();
        spinneradapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
        spnSelectLedger.setAdapter(spinneradapter);
        spnSelectLedger.setSelection(0);

      /*  spnSelectLedger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectChatname = (String) adapterView.getItemAtPosition(position);
                setChatUid();
            }
        });
       */

        return v;
    }



    public void ledgerView(DataSnapshot dataSnapshot) {

        for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) { // 년

            //     Toast.makeText(getActivity(),yearSnapshot.getKey(),Toast.LENGTH_SHORT).show();
            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) { // 월

                //Toast.makeText(getActivity(),monthSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) { // 일

                    //  Toast.makeText(getActivity(),daySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                    for (DataSnapshot classfySnapshot : daySnapshot.getChildren()) { // 분류

                        // Toast.makeText(getActivity(),classfySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                        for (DataSnapshot timesSnapshot : classfySnapshot.getChildren()) { //
                            ledger[i].setClassfy(classfySnapshot.getKey());
                            ledger[i].setYear(yearSnapshot.getKey());
                            ledger[i].setMonth(monthSnapshot.getKey());
                            selectMonth.add(ledger[i].getYear()+"년 "+ledger[i].getMonth()+"월");

                            ledger[i].setDay(daySnapshot.getKey());
                            ledger[i].setTimes(timesSnapshot.getKey());
                            //     Toast.makeText(getActivity(),timesSnapshot.getKey(),Toast.LENGTH_SHORT).show();

                            ledgerContent = timesSnapshot.getValue(LedgerContent.class);
                            ledger[i].setPaymemo(ledgerContent.getPaymemo()); ;
                            ledger[i].setPrice(ledgerContent.getPrice()); ;
                            ledger[i].setUseItem(ledgerContent.getUseItem()); ;
                            if (ledger[i].getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(ledger[i].getPrice());
                            } else if (ledger[i].getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(ledger[i].getPrice());
                            }

                            mLedger.add(ledger[i]);
                            mRecyclerView.scrollToPosition(0);
                            mAdapter.notifyItemInserted(mLedger.size() - 1);
                            i++;

                        }
                    }
                }
            }
        }
        tvTotalincome.setText("수입 합계 : " + totalIncome + "원");
        tvTotalconsume.setText("지출 합계 : " + totalConsume + "원");
        tvPlusMinus.setText("수익 : " + (totalIncome - totalConsume) + "원");
        totalIncome=0;
        totalConsume=0;

    }




    public void viewLedgerName() {

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot userSnapshot : chatSnapshot.getChildren()) {

                        for (DataSnapshot uidSnapshot : userSnapshot.getChildren())
                        {
                            if(uidSnapshot.getKey().equals(user.getUid())) {
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






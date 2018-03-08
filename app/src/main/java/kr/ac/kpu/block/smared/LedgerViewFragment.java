package kr.ac.kpu.block.smared;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class LedgerViewFragment extends android.app.Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    LedgerAdapter mAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    Ledger ledger[] = new Ledger[1000];

    int i =0;
    int caseCheck=1; // 1일경우 일반 가계부 뷰, 2일경우 공유 가계부 뷰
    LedgerContent ledgerContent = new LedgerContent();
    List<Ledger> mLedger ; // 불러온 전체 가계부 목록

    int index=0;  // 년,월 인덱스
    Set<String> selectMonth = new HashSet<String>(); // 년,월 중복제거용
    List<String> monthList; // 중복 제거된 년,월 저장
    String selectChatuid;
    String parsing;

    ImageButton ibLastMonth; // 왼쪽 화살표
    TextView tvLedgerMonth; // 년,월 출력부
    ImageButton ibNextMonth; // 오른쪽 화살표


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
   //     Bundle bundle = getArguments();
  //        caseCheck = bundle.getInt("caseCheck",1);
  //      selectChatuid = bundle.getString("chatUid");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ledger_view, container, false);
        ibLastMonth = (ImageButton) v.findViewById(R.id.ibLastMonth);
        ibNextMonth = (ImageButton) v.findViewById(R.id.ibNextMonth);
        tvLedgerMonth = (TextView) v.findViewById(R.id.tvLedgerMonth);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvLedger);
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
                if( index != 0) {
                    index--;
                   tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");
                    Toast.makeText(getActivity(), parsing, Toast.LENGTH_SHORT).show();
                } else {
                    index = monthList.size() - 1;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");
                    Toast.makeText(getActivity(), parsing, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != monthList.size() - 1) {
                    index++;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");
                    Toast.makeText(getActivity(), parsing, Toast.LENGTH_SHORT).show();
                } else {
                    index = 0;
                    tvLedgerMonth.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");
                    Toast.makeText(getActivity(), parsing, Toast.LENGTH_SHORT).show();
                }
            }
        });

        switch (caseCheck) {
            case 1:
                myRef.child(user.getUid()).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ledgerView(dataSnapshot); // 유저 가계부 전체 리스트 생성
                        monthList = new ArrayList(selectMonth); // 년 월만 빼서 따로 리스트 생성
                        Collections.sort(monthList);
                        tvLedgerMonth.setText(monthList.get(monthList.size()-1));
                        parsing = monthList.get(monthList.size()-1).replaceAll("[^0-9]", "");
                        index = monthList.size()-1;
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                break;
            case 2:
                chatRef.child(selectChatuid).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ledgerView(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                break;
           default: break;

        }

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

                            mLedger.add(ledger[i]);
                            mRecyclerView.scrollToPosition(mLedger.size()-1);
                            mAdapter.notifyItemInserted(mLedger.size() - 1);
                            i++;

                        }
                    }
                }
            }
        }
    }






}






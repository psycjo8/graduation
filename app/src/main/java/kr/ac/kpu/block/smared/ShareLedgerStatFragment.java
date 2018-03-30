package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import static android.content.ContentValues.TAG;


public class ShareLedgerStatFragment extends android.app.Fragment {

    PieChart pieChart;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    Ledger ledger[] = new Ledger[1000];
    LedgerContent ledgerContent = new LedgerContent();


    int index=0;  // 년,월 인덱스
    Set<String> selectMonth = new HashSet<String>(); // 년,월 중복제거용
    List<String> monthList; // 중복 제거된 년,월 저장
    List<Ledger> mLedger ;
    List<Ledger> tempLedger ; // 불러온 전체 가계부 목록
    List<String> listItems = new ArrayList<String>();
    String parsing;
    String selectChatuid="";
    String joinChatname;

    ImageButton ibLastMonth2; // 왼쪽 화살표
    TextView tvLedgerMonth2; // 년,월 출력부
    ImageButton ibNextMonth2; // 오른쪽 화살표
    Spinner spnSelectLedger;
    CharSequence selectChatname = "";
    ArrayAdapter<String> spinneradapter;
    int i =0;
    int j =0;
    int count =0;

    float clothPrice=0;
    float foodPrice=0;
    float transPrice=0;
    float etcPrice=0;
    float marketPrice=0;
    float homePrice=0;


    float cloth=0f;
    float food=0f;
    float home=0f;
    float trans=0f;
    float market=0f;
    float etc=0f;

    float total=0f;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();



        View v = inflater.inflate(R.layout.fragment_ledger_stat_share, container, false);
        mLedger = new ArrayList<>();
        ibLastMonth2 = (ImageButton) v.findViewById(R.id.ibLastMonth2);
        ibNextMonth2 = (ImageButton) v.findViewById(R.id.ibNextMonth2);
        tvLedgerMonth2 = (TextView) v.findViewById(R.id.tvLedgerMonth2);
        spnSelectLedger = (Spinner) v.findViewById(R.id.spnSelectLedger);


        i = 0;
        pieChart = (PieChart)v.findViewById(R.id.piechart);

        ibLastMonth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tempLedger = new ArrayList<>();
                if( index != 0) { // 년,월이 제일 처음이 아니면
                    index--;
                    tvLedgerMonth2.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", ""); // 날짜를 20182 이런형식으로 파싱

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));
                        }

                    }
                    selectChart();
                } else {   // 년,월이 처음이면
                    index = monthList.size() - 1;
                    tvLedgerMonth2.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));

                        }
                    }
                    selectChart();
                }
            }
        });

        ibNextMonth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempLedger = new ArrayList<>();
                if (index != monthList.size() - 1) { // 년, 월이 마지막이 아니면
                    index++;
                    tvLedgerMonth2.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));
                        }
                    }
                    selectChart();
                } else {   // 년,월이 마지막이면
                    index = 0;
                    tvLedgerMonth2.setText(monthList.get(index));
                    parsing= monthList.get(index).replaceAll("[^0-9]", "");

                    for (int j=0; j<mLedger.size(); j++) {
                        if( parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth()) ) {
                            tempLedger.add(mLedger.get(j));
                        }
                    }
                    selectChart();
                }
            }
        });
        viewLedgerName("init");

        spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
        spnSelectLedger.setAdapter(spinneradapter);
        spnSelectLedger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                      @Override
                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                          if (count != 0) {

                                                              selectChatname = (String) parent.getItemAtPosition(position);

                                                              mLedger.clear(); // 가계부 초기화
                                                              listItems.clear(); // 참여중인 가계부 목록 초기화
                                                              selectMonth.clear();
                                                              monthList.clear(); // 년,월 선택 초기화
                                                              viewLedgerName(selectChatname);
                                                          }
                                                          count = 1;
                                                      }

                                                      @Override
                                                      public void onNothingSelected(AdapterView<?> parent) {

                                                      }
                                                  }


        );

        return v;
    }


    public void ledgerView(DataSnapshot dataSnapshot) {

        for (i=0; i<1000; i++) {
            ledger[i] = new Ledger();
        }
        i=0;
        for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) { // 년

            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) { // 월

                for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) { // 일

                    for (DataSnapshot classfySnapshot : daySnapshot.getChildren()) { // 분류

                        for (DataSnapshot timesSnapshot : classfySnapshot.getChildren()) { //
                            ledgerContent = timesSnapshot.getValue(LedgerContent.class);

                            ledger[i].setClassfy(classfySnapshot.getKey());
                            ledger[i].setYear(yearSnapshot.getKey());
                            ledger[i].setMonth(monthSnapshot.getKey());
                            selectMonth.add(ledger[i].getYear()+"년 "+ledger[i].getMonth()+"월");

                            ledger[i].setDay(daySnapshot.getKey());
                            ledger[i].setTimes(timesSnapshot.getKey());
                            //     Toast.makeText(getActivity(),timesSnapshot.getKey(),Toast.LENGTH_SHORT).show();


                            ledger[i].setPaymemo(ledgerContent.getPaymemo()); ;
                            ledger[i].setPrice(ledgerContent.getPrice()); ;
                            ledger[i].setUseItem(ledgerContent.getUseItem()); ;


                            mLedger.add(ledger[i]);

                            i++;

                        }
                    }
                }
            }
        }

        clothPrice=0;
        foodPrice=0;
        transPrice=0;
        etcPrice=0;
        marketPrice=0;
        homePrice=0;


        cloth=0f;
        food=0f;
        home=0f;
        trans=0f;
        market=0f;
        etc=0f;
        total=0f;



        for (j=0; j<i; j++) {
            if (ledger[j].getClassfy().equals("지출")) {
                if (ledger[j].getUseItem().equals("의류비")) {
                    clothPrice += Integer.parseInt(ledger[j].getPrice());
                } else if (ledger[j].getUseItem().equals("식비")) {
                    foodPrice += Integer.parseInt(ledger[j].getPrice());
                } else if (ledger[j].getUseItem().equals("주거비")) {
                    homePrice += Integer.parseInt(ledger[j].getPrice());
                } else if (ledger[j].getUseItem().equals("교통비")) {
                    transPrice += Integer.parseInt(ledger[j].getPrice());
                } else if (ledger[j].getUseItem().equals("생필품")) {
                    marketPrice += Integer.parseInt(ledger[j].getPrice());
                } else if (ledger[j].getUseItem().equals("기타")) {
                    etcPrice += Integer.parseInt(ledger[j].getPrice());
                }
            }
        }

        total = clothPrice + foodPrice + homePrice + transPrice + marketPrice + etcPrice;
        cloth = (clothPrice / total) * 100;
        food = (foodPrice / total) * 100;
        home = (homePrice / total) * 100;
        trans = (transPrice / total) * 100;
        market = (marketPrice / total) * 100;
        etc = (etcPrice / total) * 100;


        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if (cloth !=0) {
            yValues.add(new PieEntry(cloth, "의류비"));
        }
        if (food !=0) {
            yValues.add(new PieEntry(food, "식비"));
        }
        if (home !=0) {
            yValues.add(new PieEntry(home, "주거비"));
        }
        if (trans !=0) {
            yValues.add(new PieEntry(trans, "교통비"));
        }
        if (market !=0) {
            yValues.add(new PieEntry(market, "생필품"));
        }
        if (etc !=0) {
            yValues.add(new PieEntry(etc, "기타"));
        }

        Description description = new Description();
        description.setText("소비 분류"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                PieEntry pe = (PieEntry) e;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                if (pe.getLabel().equals("의류비")) {
                    alertDialog.setMessage("의류비 총계 : " + (int)clothPrice + "원");
                } else if (pe.getLabel().equals("식비")) {
                    alertDialog.setMessage("식비 총계 : " + (int)foodPrice + "원");
                } else if (pe.getLabel().equals("주거비")) {
                    alertDialog.setMessage("주거비 총계 : " + (int)homePrice + "원");
                } else if (pe.getLabel().equals("교통비")) {
                    alertDialog.setMessage("교통비 총계 : " + (int)transPrice + "원");
                } else if (pe.getLabel().equals("생필품")) {
                    alertDialog.setMessage("생필품비 총계 : " + (int)marketPrice + "원");
                } else if (pe.getLabel().equals("기타")) {
                    alertDialog.setMessage("기타 비용 총계 : " + (int)etcPrice + "원");
                }
                AlertDialog alert = alertDialog.create();
                alert.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void selectChart() {
        clothPrice=0;
        foodPrice=0;
        transPrice=0;
        etcPrice=0;
        marketPrice=0;
        homePrice=0;


        cloth=0f;
        food=0f;
        home=0f;
        trans=0f;
        market=0f;
        etc=0f;
        total=0f;

        for (int j = 0; j < tempLedger.size(); j++) {
            if (tempLedger.get(j).getUseItem().equals("의류비")) {
                clothPrice += Integer.parseInt(tempLedger.get(j).getPrice());
            } else if (tempLedger.get(j).getUseItem().equals("식비")) {
                foodPrice += Integer.parseInt(tempLedger.get(j).getPrice());
            } else if (tempLedger.get(j).getUseItem().equals("주거비")) {
                homePrice += Integer.parseInt(tempLedger.get(j).getPrice());
            } else if (tempLedger.get(j).getUseItem().equals("교통비")) {
                transPrice += Integer.parseInt(tempLedger.get(j).getPrice());
            } else if (tempLedger.get(j).getUseItem().equals("생필품")) {
                marketPrice += Integer.parseInt(tempLedger.get(j).getPrice());
            } else if (tempLedger.get(j).getUseItem().equals("기타")) {
                etcPrice += Integer.parseInt(tempLedger.get(j).getPrice());
            }
        }
        total = clothPrice + foodPrice + homePrice + transPrice + marketPrice + etcPrice;
        cloth = (clothPrice / total) * 100;
        food = (foodPrice / total) * 100;
        home = (homePrice / total) * 100;
        trans = (transPrice / total) * 100;
        market = (marketPrice / total) * 100;
        etc = (etcPrice / total) * 100;

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if (cloth !=0) {
            yValues.add(new PieEntry(cloth, "의류비"));
        }
        if (food !=0) {
            yValues.add(new PieEntry(food, "식비"));
        }
        if (home !=0) {
            yValues.add(new PieEntry(home, "주거비"));
        }
        if (trans !=0) {
            yValues.add(new PieEntry(trans, "교통비"));
        }
        if (market !=0) {
            yValues.add(new PieEntry(market, "생필품"));
        }
        if (etc !=0) {
            yValues.add(new PieEntry(etc, "기타"));
        }

        Description description = new Description();
        description.setText("소비 분류"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                PieEntry pe = (PieEntry) e;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                if (pe.getLabel().equals("의류비")) {
                    alertDialog.setMessage("의류비 총계 : " + (int)clothPrice + "원");
                } else if (pe.getLabel().equals("식비")) {
                    alertDialog.setMessage("식비 총계 : " + (int)foodPrice + "원");
                } else if (pe.getLabel().equals("주거비")) {
                    alertDialog.setMessage("주거비 총계 : " + (int)homePrice + "원");
                } else if (pe.getLabel().equals("교통비")) {
                    alertDialog.setMessage("교통비 총계 : " + (int)transPrice + "원");
                } else if (pe.getLabel().equals("생필품")) {
                    alertDialog.setMessage("생필품비 총계 : " + (int)marketPrice + "원");
                } else if (pe.getLabel().equals("기타")) {
                    alertDialog.setMessage("기타 비용 총계 : " + (int)etcPrice + "원");
                }
                AlertDialog alert = alertDialog.create();
                alert.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    public void setChatUid() { // 선택된 가계부 이름으로 부터 가계부 키를 찾고 화면 출력
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    if ( chatSnapshot.child("chatname").getValue(String.class).equals(selectChatname) ) {
                        selectChatuid = chatSnapshot.getKey();

                        chatRef.child(selectChatuid).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                tvLedgerMonth2.setText("전체 가계부");
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



                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void viewLedgerName(final CharSequence chatname) { // 현재 참여중인 가계부 이름을 읽어옴


        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot userSnapshot : chatSnapshot.getChildren()) {

                        for (DataSnapshot uidSnapshot : userSnapshot.getChildren()) {
                            if (uidSnapshot.getKey().equals(user.getUid())) {
                                if (chatname.equals("init")) {
                                    joinChatname = chatSnapshot.child("chatname").getValue(String.class);
                                    listItems.add(joinChatname);
                                    spinneradapter.notifyDataSetChanged();
                                    selectChatname = listItems.get(0).toString();
                                } else {
                                    joinChatname = chatSnapshot.child("chatname").getValue(String.class);
                                    listItems.add(joinChatname);
                                    spinneradapter.notifyDataSetChanged();
                                    selectChatname = chatname;

                                }
                            }
                        }
                    }
                }


                setChatUid();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    }

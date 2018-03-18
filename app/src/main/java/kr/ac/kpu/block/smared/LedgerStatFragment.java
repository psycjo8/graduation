package kr.ac.kpu.block.smared;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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


public class LedgerStatFragment extends android.app.Fragment {

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
    String parsing;


    ImageButton ibLastMonth2; // 왼쪽 화살표
    TextView tvLedgerMonth2; // 년,월 출력부
    ImageButton ibNextMonth2; // 오른쪽 화살표

    int i =0;
    int j =0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        for (i=0; i<1000; i++) {
            ledger[i] = new Ledger();
        }

        View v = inflater.inflate(R.layout.fragment_ledger_stat, container, false);
        mLedger = new ArrayList<>();
        ibLastMonth2 = (ImageButton) v.findViewById(R.id.ibLastMonth2);
        ibNextMonth2 = (ImageButton) v.findViewById(R.id.ibNextMonth2);
        tvLedgerMonth2 = (TextView) v.findViewById(R.id.tvLedgerMonth2);
        i = 0;
        pieChart = (PieChart)v.findViewById(R.id.piechart);
        myRef.child(user.getUid()).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvLedgerMonth2.setText("전체 가계부");
                ledgerView(dataSnapshot);
                monthList = new ArrayList(selectMonth); // 년 월만 빼서 따로 리스트 생성
                Collections.sort(monthList);
                if(monthList.isEmpty()) {

                } else {
                    parsing = monthList.get(monthList.size() - 1).replaceAll("[^0-9]", "");
                    index = monthList.size() - 1;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


        return v;
    }

    public void ledgerView(DataSnapshot dataSnapshot) {

        float cloth=0f;
        float food=0f;
        float home=0f;
        float trans=0f;
        float market=0f;
        float etc=0f;
        float total=0f;
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



        for (j=0; j<i; j++) {
            if (ledger[j].getUseItem().equals("의류비")) {
                cloth ++;
            }
            else if (ledger[j].getUseItem().equals("식비")) {
                food ++;
            }
            else if (ledger[j].getUseItem().equals("주거비")) {
                home ++;
            }
            else if (ledger[j].getUseItem().equals("교통비")) {
                trans ++;
            }
            else if (ledger[j].getUseItem().equals("생필품")) {
                market ++;
            }
            else if (ledger[j].getUseItem().equals("기타")) {
                etc ++;
            }
        }

        total = cloth + food + home + trans + market + etc;
        cloth = (cloth / total) * 100;
        food = (food / total) * 100;
        home = (home / total) * 100;
        trans = (trans / total) * 100;
        market = (market / total) * 100;
        etc = (etc / total) * 100;

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
    }

    public void selectChart() {
        float cloth = 0f;
        float food = 0f;
        float home = 0f;
        float trans = 0f;
        float market = 0f;
        float etc = 0f;
        float total = 0f;

        for (int j = 0; j < tempLedger.size(); j++) {
            if (tempLedger.get(j).getUseItem().equals("의류비")) {
                cloth++;
            } else if (tempLedger.get(j).getUseItem().equals("식비")) {
                food++;
            } else if (tempLedger.get(j).getUseItem().equals("주거비")) {
                home++;
            } else if (tempLedger.get(j).getUseItem().equals("교통비")) {
                trans++;
            } else if (tempLedger.get(j).getUseItem().equals("생필품")) {
                market++;
            } else if (tempLedger.get(j).getUseItem().equals("기타")) {
                etc++;
            }
        }
        total = cloth + food + home + trans + market + etc;
        cloth = (cloth / total) * 100;
        food = (food / total) * 100;
        home = (home / total) * 100;
        trans = (trans / total) * 100;
        market = (market / total) * 100;
        etc = (etc / total) * 100;

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
    }
}

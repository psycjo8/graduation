package kr.ac.kpu.block.smared;

import android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LedgerViewFragment extends android.app.Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    LedgerAdapter mAdapter;
    LedgerAdapter tempAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference chatRef;
    FirebaseUser user;



    int totalIncome=0;
    int totalConsume=0;
    LedgerContent ledgerContent = new LedgerContent();
    List<Ledger> mLedger ; // 불러온 전체 가계부 목록
    List<Ledger> tempLedger ; // 불러온 부분 가계부 목록

    int index=0;  // 년,월 인덱스
    Set<String> selectMonth = new HashSet<String>(); // 년,월 중복제거용
    List<String> monthList; // 중복 제거된 년,월 저장
    String parsing;


    ImageButton ibLastMonth; // 왼쪽 화살표
    TextView tvLedgerMonth; // 년,월 출력부
    ImageButton ibNextMonth; // 오른쪽 화살표
    TextView tvTotalconsume;
    TextView tvTotalincome;
    TextView tvPlusMinus;
    Button btnExport;

    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int MULTIPLE_PERMISSIONS = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        chatRef = database.getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();
        checkPermissions();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ledger_view, container, false);

        ibLastMonth = (ImageButton) v.findViewById(R.id.ibLastMonth);
        ibNextMonth = (ImageButton) v.findViewById(R.id.ibNextMonth);
        tvLedgerMonth = (TextView) v.findViewById(R.id.tvLedgerMonth);
        tvTotalincome = (TextView) v.findViewById(R.id.tvTotalincome);
        tvTotalconsume = (TextView) v.findViewById(R.id.tvTotalconsume);
        tvPlusMinus = (TextView) v.findViewById(R.id.tvPlusMinus);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvLedger);
        btnExport = (Button) v.findViewById(R.id.btnExport);
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
        mRecyclerView.scrollToPosition(0);

        ibLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLedger.size() != 0) {
                    tempLedger = new ArrayList<>();
                    if (index != 0) { // 년,월이 제일 처음이 아니면
                        index--;
                        tvLedgerMonth.setText(monthList.get(index));
                        parsing = monthList.get(index).replaceAll("[^0-9]", "");

                        for (int j = 0; j < mLedger.size(); j++) {
                            if (parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth())) {
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
                        totalIncome = 0;
                        totalConsume = 0;
                        tempAdapter = new LedgerAdapter(tempLedger, getActivity());
                        mRecyclerView.setAdapter(tempAdapter);


                    } else {   // 년,월이 처음이면
                        index = monthList.size() - 1;
                        tvLedgerMonth.setText(monthList.get(index));
                        parsing = monthList.get(index).replaceAll("[^0-9]", "");

                        for (int j = 0; j < mLedger.size(); j++) {
                            if (parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth())) {
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
                        totalIncome = 0;
                        totalConsume = 0;
                        tempAdapter = new LedgerAdapter(tempLedger, getActivity());
                        mRecyclerView.setAdapter(tempAdapter);
                    }
                }
            }
        });

        ibNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLedger.size() != 0) {
                    tempLedger = new ArrayList<>();
                    if (index != monthList.size() - 1) { // 년, 월이 마지막이 아니면
                        index++;
                        tvLedgerMonth.setText(monthList.get(index));
                        parsing = monthList.get(index).replaceAll("[^0-9]", "");

                        for (int j = 0; j < mLedger.size(); j++) {
                            if (parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth())) {
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
                        totalIncome = 0;
                        totalConsume = 0;
                        tempAdapter = new LedgerAdapter(tempLedger, getActivity());
                        mRecyclerView.setAdapter(tempAdapter);
                    } else {   // 년,월이 마지막이면
                        index = 0;
                        tvLedgerMonth.setText(monthList.get(index));
                        parsing = monthList.get(index).replaceAll("[^0-9]", "");

                        for (int j = 0; j < mLedger.size(); j++) {
                            if (parsing.equals(mLedger.get(j).getYear() + mLedger.get(j).getMonth())) {
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
                        totalIncome = 0;
                        totalConsume = 0;
                        tempAdapter = new LedgerAdapter(tempLedger, getActivity());
                        mRecyclerView.setAdapter(tempAdapter);
                    }
                }
            }


        });
                myRef.child(user.getUid()).child("Ledger").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tvLedgerMonth.setText("전체 가계부");
                        mLedger.clear();
                        selectMonth.clear();
                        mAdapter.notifyDataSetChanged();
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

                btnExport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("저장할 가계부 이름을 설정해주세요");
                        EditText editName = new EditText(getActivity());
                        alertDialog.setView(editName);
                        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              String ledgerName = editName.getText().toString();
                              if(ledgerName.isEmpty()) {
                                  Toast.makeText(getActivity(), "파일 이름을 지정해 주세요", Toast.LENGTH_SHORT).show();
                              } else {
                                  saveExcel(ledgerName);
                              }
                            }
                        });
                        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                });

        return v;
    }



    public void ledgerView(DataSnapshot dataSnapshot) {

        Ledger ledger = new Ledger();
        for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) { // 년

            //     Toast.makeText(getActivity(),yearSnapshot.getKey(),Toast.LENGTH_SHORT).show();
            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) { // 월

                //Toast.makeText(getActivity(),monthSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) { // 일

                    //  Toast.makeText(getActivity(),daySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                    for (DataSnapshot classfySnapshot : daySnapshot.getChildren()) { // 분류


                        // Toast.makeText(getActivity(),classfySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                        for (DataSnapshot timesSnapshot : classfySnapshot.getChildren()) { //
                            ledger.setClassfy(classfySnapshot.getKey());
                            ledger.setYear(yearSnapshot.getKey());
                            ledger.setMonth(monthSnapshot.getKey());
                            selectMonth.add(ledger.getYear()+"년 "+ledger.getMonth()+"월");

                            ledger.setDay(daySnapshot.getKey());
                            ledger.setTimes(timesSnapshot.getKey());
                            //     Toast.makeText(getActivity(),timesSnapshot.getKey(),Toast.LENGTH_SHORT).show();

                            ledgerContent = timesSnapshot.getValue(LedgerContent.class);
                            ledger.setPaymemo(ledgerContent.getPaymemo()); ;
                            ledger.setPrice(ledgerContent.getPrice()); ;
                            ledger.setUseItem(ledgerContent.getUseItem()); ;
                            if (ledger.getClassfy().equals("지출")) {
                                totalConsume += Integer.parseInt(ledger.getPrice());
                            } else if (ledger.getClassfy().equals("수입")) {
                                totalIncome += Integer.parseInt(ledger.getPrice());
                            }

                            mLedger.add(ledger);
                            ledger = new Ledger();
                            mAdapter.notifyItemInserted(mLedger.size() - 1);


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

    private void saveExcel(String ledgerName){
        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet(); // 새로운 시트 생성

        Row row = sheet.createRow(0); // 새로운 행 생성
        Cell cell;

        cell = row.createCell(0); // 1번 셀 생성
        cell.setCellValue("날짜"); // 1번 셀 값 입력

        cell = row.createCell(1); // 2번 셀 생성
        cell.setCellValue("소비 분류"); // 2번 셀 값 입력

        cell = row.createCell(2); // 2번 셀 생성
        cell.setCellValue("분류"); // 2번 셀 값 입력

        cell = row.createCell(3); // 2번 셀 생성
        cell.setCellValue("금액"); // 2번 셀 값 입력

        cell = row.createCell(4); // 2번 셀 생성
        cell.setCellValue("내용"); // 2번 셀 값 입력


        for(int i = 0; i < mLedger.size() ; i++){ // 데이터 엑셀에 입력
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(mLedger.get(i).getYear() + "-" + mLedger.get(i).getMonth() + "-" + mLedger.get(i).getDay());
            cell = row.createCell(1);
            cell.setCellValue(mLedger.get(i).getClassfy());
            cell = row.createCell(2);
            cell.setCellValue(mLedger.get(i).getUseItem());
            cell = row.createCell(3);
            cell.setCellValue(mLedger.get(i).getPrice());
            cell = row.createCell(4);
            cell.setCellValue(mLedger.get(i).getPaymemo());
        }

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/SmaRed/Excel");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File xlsFile = new File(Environment.getExternalStorageDirectory() + "/SmaRed/Excel",ledgerName+".xls");

        try{
            FileOutputStream os = new FileOutputStream(xlsFile);
            workbook.write(os); // 외부 저장소에 엑셀 파일 생성
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getActivity(),xlsFile.getAbsolutePath()+"에 저장되었습니다",Toast.LENGTH_SHORT).show();

        Uri path = Uri.fromFile(xlsFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM,path);
        startActivity(Intent.createChooser(shareIntent,"엑셀 내보내기"));


    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(getActivity(), "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }



}






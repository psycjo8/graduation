package kr.ac.kpu.block.smared;

import android.content.Context;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class LedgerViewFragment extends android.app.Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    LedgerAdapter mAdapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    Ledger ledger[] = new Ledger[1000];

    int i =0;


    LedgerContent ledgerContent = new LedgerContent();
    List<Ledger> mLedger ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        for (i=0; i<1000; i++) {
            ledger[i] = new Ledger();
        }
        i=0;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ledger_view, container, false);
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

        myRef.child(user.getUid()).child("Ledger").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) { // 년
                   ledger[i].setYear(yearSnapshot.getKey());
               //     Toast.makeText(getActivity(),yearSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                    for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) { // 월
                       ledger[i].setMonth(monthSnapshot.getKey());
                        //Toast.makeText(getActivity(),monthSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                        for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) { // 일
                            ledger[i].setDay(daySnapshot.getKey());
                         //  Toast.makeText(getActivity(),daySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                            for (DataSnapshot classfySnapshot : daySnapshot.getChildren()) { // 분류
                               ledger[i].setClassfy(classfySnapshot.getKey());

                               // Toast.makeText(getActivity(),classfySnapshot.getKey(),Toast.LENGTH_SHORT).show();
                                for (DataSnapshot timesSnapshot : classfySnapshot.getChildren()) { //
                                    ledger[i].setTimes(timesSnapshot.getKey());
                               //     Toast.makeText(getActivity(),timesSnapshot.getKey(),Toast.LENGTH_SHORT).show();

                                   ledgerContent = timesSnapshot.getValue(LedgerContent.class);
                                   ledger[i].setPaymemo(ledgerContent.getPaymemo()); ;
                                   ledger[i].setPrice(ledgerContent.getPrice()); ;
                                   ledger[i].setUseItem(ledgerContent.getUseItem()); ;

                                   mLedger.add(ledger[i]);
                                    mAdapter.notifyItemInserted(mLedger.size() - 1);
                                    i++;

                                    }
                                }
                            }
                        }
                    }

                }





            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
         });

        return v;
    }
}






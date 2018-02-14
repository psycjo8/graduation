package kr.ac.kpu.block.smared;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LedgerViewFragment extends android.app.Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView ;
    LinearLayoutManager mLayoutManager;
    FirebaseDatabase database;
    LedgerAdapter mAdapter;

    List<Ledger> mLedger;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        mAdapter = new LedgerAdapter(mLedger,getActivity());
        mRecyclerView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");



        return v;
    }

}

package kr.ac.kpu.block.smared;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {
    RecyclerView mRecyclerView ;
    LinearLayoutManager mLayoutManager;
    FirebaseDatabase database;
    List<Friend> mFriend;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvFriend);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriend= new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mChat,email);
        mRecyclerView.setAdapter(mAdapter);
       return v;
    }


}

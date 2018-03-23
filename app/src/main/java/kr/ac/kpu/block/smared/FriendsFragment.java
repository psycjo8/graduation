package kr.ac.kpu.block.smared;

import android.app.Fragment;
import android.os.Bundle;
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


public class FriendsFragment extends Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView ;
    LinearLayoutManager mLayoutManager;

    FirebaseDatabase database;

    List<Friend> mFriend;
    FriendAdapter mAdapter;
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
        mAdapter = new FriendAdapter(mFriend,getActivity());
        mRecyclerView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                Log.d(TAG, "Value is: " + value);

                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    String value2 = dataSnapshot.getValue().toString();
                    Log.d(TAG, "Value is: " + value2);
                    Friend friend = dataSnapshot2.getValue(Friend.class);

                    mFriend.add(friend);
                    mAdapter.notifyItemInserted(mFriend.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
       return v;
    }


}

package kr.ac.kpu.block.smared;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView ;
    LinearLayoutManager mLayoutManager;

    FirebaseDatabase database;
    String stChatId;
    List<Friend> mFriend;
    FriendAdapter mAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Intent in = getIntent();
        stChatId = in.getStringExtra("chatUid");
        mRecyclerView = (RecyclerView) findViewById(R.id.rvFriend);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriend= new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new FriendAdapter(mFriend,this);
        mRecyclerView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference chatRef = database.getReference("chats").child(stChatId).child("user");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    final String value = dataSnapshot2.getKey();
                    myRef.child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                                String value2 = dataSnapshot.getValue().toString();
                                Log.d(TAG, "Value is: " + value2);
                                Friend friend = dataSnapshot.getValue(Friend.class);

                                mFriend.add(friend);
                                mAdapter.notifyItemInserted(mFriend.size() - 1);
                            }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


}

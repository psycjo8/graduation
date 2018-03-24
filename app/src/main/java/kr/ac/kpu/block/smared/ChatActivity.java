package kr.ac.kpu.block.smared;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView; //
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText etText;
    Button btnSend;
    Button btnViewFriend;
    String email;
    String photo;
    String nickname;
    FirebaseDatabase database;
    List<Chat> mChat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        database = FirebaseDatabase.getInstance(); // Firebase Database 연결
        etText = (EditText) findViewById(R.id.etText);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnViewFriend = (Button) findViewById(R.id.btnViewFriend);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvChat);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 정보 추출

        Intent in = getIntent();
        final String stChatId = in.getStringExtra("chatUid");
                photo = in.getStringExtra("photo");
                nickname = in.getStringExtra("nickname");
        if (user != null) {
            email = user.getEmail();

        }

        btnViewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ChatActivity.this,FriendActivity.class);
                in.putExtra("chatUid",stChatId);
                startActivity(in);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stText = etText.getText().toString();

                if (stText.equals("") || stText.isEmpty()) { // 공백 체크
                    Toast.makeText(ChatActivity.this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar c = Calendar.getInstance(); // Firebase내에 날짜로 저장
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    DatabaseReference myRef = database.getReference("chats").child(stChatId).child("chat").child(formattedDate);


                    Hashtable<String, String> chat   // HashTable로 연결
                            = new Hashtable<String, String>();
                    chat.put("email", email);
                    chat.put("text",stText);
                    chat.put("photo",photo);
                    chat.put("nickname",nickname);
                    myRef.setValue(chat);
                    etText.setText("");


                }

            }
        });

        Button btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });






        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChat= new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mChat,email,ChatActivity.this);
        mRecyclerView.setAdapter(mAdapter);



        DatabaseReference myRef = database.getReference("chats").child(stChatId).child("chat");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                mChat.add(chat);
                mRecyclerView.scrollToPosition(mChat.size()-1);
                mAdapter.notifyItemInserted(mChat.size() - 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}

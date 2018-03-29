package kr.ac.kpu.block.smared;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    String stEmail;
    String stPassword;
    String stNickname;
    EditText etEmail;
    EditText etPassword;
    ProgressBar pbLogin;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();


        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail.setText("test@naver.com");
        etPassword.setText("lookup");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // 회원가입 버튼 클릭 시
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
                final EditText email = new EditText(MainActivity.this);
                final EditText password = new EditText(MainActivity.this);
                final EditText nickname = new EditText(MainActivity.this);
                LinearLayout layout = new LinearLayout(MainActivity.this);

                email.setHint("Email을 입력해주세요");
                password.setHint("비밀번호을 입력해주세요");
                nickname.setHint("닉네임을 입력해주세요");

                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(email);
                layout.addView(password);
                layout.addView(nickname);
                alertdialog.setView(layout);

                alertdialog.setTitle("회원가입");
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stEmail = email.getText().toString();
                        stPassword = password.getText().toString();
                        stNickname = nickname.getText().toString();
                        if(stEmail.isEmpty() || stPassword.isEmpty() || stNickname.isEmpty()) {
                            Toast.makeText(MainActivity.this, "양식을 모두 채워주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser(stEmail,stPassword);
                        }
                    }
                });





                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alert = alertdialog.create();
                alert.show();




            }
        });

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   // 로그인 버튼 클릭 시
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();

                if(stEmail.isEmpty() || stEmail.equals("") || stPassword.isEmpty() || stPassword.equals("") ) {
                    Toast.makeText(MainActivity.this, "입력이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    userLogin(stEmail,stPassword);
                }

                //Toast.makeText(MainActivity.this, "LOGIN", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onStart() { // 유저 체크
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void registerUser(String email, String password) { // 회원 가입
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, "회원가입 성공",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Hashtable<String, String> profile   // HashTable로 연결
                                    = new Hashtable<String, String>();
                            profile.put("email", stEmail);
                            profile.put("photo","https://firebasestorage.googleapis.com/v0/b/smared-d1166.appspot.com/o/users%2Fnoimage.jpg?alt=media&token=a07b849c-87c6-4840-9364-be7b8ca7d8ef");
                            profile.put("key",user.getUid());
                            profile.put("nickname",stNickname);
                            myRef.child(user.getUid()).setValue(profile);
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "회원가입 실패",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void userLogin(String email, String password) { // 로그인 체크 - firebase
        pbLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbLogin.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "로그인 성공",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedPreferences = getSharedPreferences("email",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uid",user.getUid());
                            editor.putString("email",user.getEmail());
                            editor.apply();



                           // updateUI(user);
                            Intent in = new Intent(MainActivity.this, TabActivity.class);
                            startActivity(in);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                          //  updateUI(null);
                        }

                        // ...
                    }

                });

    }

}
package kr.ac.kpu.block.smared;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class ProfileFragment extends Fragment {
    ImageView ivUser;
    TextView tvNickname;
    Button btnChangePhoto;
    Button btnChangeNickname;
    Button btnLogout;
    Button btnWithdrawal;
    private StorageReference mStorageRef;
    Bitmap bitmap;
    String stUid;
    String stEmail;
    String stNickname;
    String TAG = getClass().getSimpleName();
    int regStatus = 1;
    ProgressBar pbLogin;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvNickname = (TextView) v.findViewById(R.id.tvNickname);
        btnChangePhoto = (Button) v.findViewById(R.id.btnChangePhoto);
        btnLogout =  (Button) v.findViewById(R.id.btnLogout);
        btnChangeNickname = (Button) v.findViewById(R.id.btnChangeNickname);
        btnWithdrawal = (Button) v.findViewById(R.id.btnWithdrawal);

        ivUser  = (ImageView) v.findViewById(R.id.ivUser);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("email",Context.MODE_PRIVATE);
        stUid = sharedPreferences.getString("uid","");
        stEmail = sharedPreferences.getString("email","");

        pbLogin = (ProgressBar) v.findViewById(R.id.pbLogin);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("users").child(stUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(regStatus==0) {
                getActivity().finish();
                }   else {

                    String value = dataSnapshot.getValue().toString();
                    String stPhoto = dataSnapshot.child("photo").getValue().toString();
                    stNickname = dataSnapshot.child("nickname").getValue().toString();
                    tvNickname.setText("닉네임 : " + stNickname);

                    if (TextUtils.isEmpty(stPhoto)) {
                        pbLogin.setVisibility(getView().GONE);
                    } else {

                        Picasso.with(getActivity()).load(stPhoto).fit().centerInside().into(ivUser, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                // Index 0 is the image view.
                                Log.d(TAG, "SUCCESS");
                                pbLogin.setVisibility(getView().GONE);
                            }
                        });
                    }


                    Log.d(TAG, "Value is: " + value);
                }  }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),    // 외부 저장소 권한 요청
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        // Inflate the layout for this fragment


        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
                pbLogin.setVisibility(getView().VISIBLE);
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(),"로그아웃 되었습니다",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        btnChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
                final EditText etNickname = new EditText(getActivity());
                alertdialog.setTitle("닉네임 변경");
                alertdialog.setView(etNickname);

                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stNickname = etNickname.getText().toString();
                        tvNickname.setText("닉네임 : "+ stNickname);
                        myRef.child("users").child(stUid).child("nickname").setValue(stNickname);
                    }
                });
                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alertdialog.show();
            }
        });


        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
                alertdialog.setMessage("정말 탈퇴하시겠습니까?");

                alertdialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            regStatus=0;
                                            myRef.child("users").child(stUid).removeValue();

                                            Log.d(TAG, "User account deleted.");

                                            Toast.makeText(getActivity(),"계정이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    }
                                });
                    }
                });
                alertdialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertdialog.show();

            }
        });




        return v;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  // 이미지뷰 파일 업로드
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Uri image = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),image);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            pbLogin.setVisibility(getView().GONE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {    // 외부저장소 권한 응답
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void uploadImage() {
        StorageReference profileRef = mStorageRef.child("users").child(stUid+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String photoUrl = String.valueOf(downloadUrl);
                Log.d("url",photoUrl);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                Hashtable<String, String> profile   // HashTable로 연결
                        = new Hashtable<String, String>();
                profile.put("email", stEmail);
                profile.put("key",stUid);
                profile.put("photo",photoUrl);
                profile.put("nickname",stNickname);
                myRef.child(stUid).setValue(profile);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.getValue().toString();
                        Log.d("profile",s);
                        if (dataSnapshot != null ) {

                            Toast.makeText(getActivity(), "사진 업로드 완료",Toast.LENGTH_SHORT).show();
                            ivUser.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}

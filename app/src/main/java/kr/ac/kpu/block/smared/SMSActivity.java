package kr.ac.kpu.block.smared;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SMSActivity extends AppCompatActivity {
    static final int SMS_RECEIVE_PERMISSON=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //권한이 부여되어 있는지 확인
        int permissonCheck= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_SMS}, SMS_RECEIVE_PERMISSON);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }





        Uri allMessage = Uri.parse("content://sms/inbox");
        Cursor cur = this.getContentResolver().query(allMessage,null,null,null,null);
        int count = cur.getCount();
        Toast.makeText(this, "SMS Count = " + count , Toast.LENGTH_SHORT).show();
        String msg = "";
        String date = "";
        String protocol = "";

        while (cur.moveToNext()) {

            if (cur.getString(cur.getColumnIndex("body")).contains("신한체크승인")) {
                msg = cur.getString(cur.getColumnIndex("body"));
                protocol = cur.getString(cur.getColumnIndex("address"));
                date = cur.getString(cur.getColumnIndex("date"));
                Toast.makeText(this, protocol + "", Toast.LENGTH_SHORT).show();

            }




        }

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]){
        switch(requestCode){
            case SMS_RECEIVE_PERMISSON:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "SMS권한 승인함", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "SMS권한 거부함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



}

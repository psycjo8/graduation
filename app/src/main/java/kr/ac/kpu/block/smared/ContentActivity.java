package kr.ac.kpu.block.smared;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.Hashtable;

public class ContentActivity extends AppCompatActivity  {

    TextView OCRResult;
    TextView OCRToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        String result="";
        String toast = "";
        Intent in = getIntent();
        OCRResult = (TextView) findViewById(R.id.OCRResult);
        OCRToast = (TextView) findViewById(R.id.OCRToast);

        result = in.getStringExtra("result");
        toast = in.getStringExtra("finalResult");

        OCRResult.setText("[ OCR 인식 결과 ]\n" + result);
        OCRToast.setText(toast);
    }
}

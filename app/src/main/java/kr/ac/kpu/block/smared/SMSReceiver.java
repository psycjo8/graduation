package kr.ac.kpu.block.smared;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by psycj on 2018-05-16.
 */


public class SMSReceiver extends BroadcastReceiver {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAzHjou4I:APA91bGmqKJ-XhpwAg4VfKkDVkUlBeGLv661C1bCgEz3k-Ak0j89p_zhvyYPb_sx-H9cOlO-g6zcyeWySxPeusz9bpWLORr6RI49F4xaYkPX5G4-kbbVwqiwf7FiBxygzaySIwwuQIIo";

    static final String logTag = "SmsReceiver";
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {


            //Bundel 널 체크

            Bundle bundle = intent.getExtras();

            if (bundle == null) {

                return;

            }


            //pdu 객체 널 체크

            Object[] pdusObj = (Object[]) bundle.get("pdus");

            if (pdusObj == null) {

                return;

            }


            //message 처리

            SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];

            for (int i = 0; i < pdusObj.length; i++) {

                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                if ( smsMessages[i].getMessageBody().contains("신한체크승인") ) {
                    NotificationManager notificationManager =
                            (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                    Intent intents = new Intent(context.getApplicationContext(),TabActivity.class);
                    intents.putExtra("sms",smsMessages[i].getMessageBody());
                    intents.putExtra("smsdate",smsMessages[i].getTimestampMillis());
                    Notification.Builder builder = new Notification.Builder(context.getApplicationContext());
                    intents.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendnoti = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setSmallIcon(R.drawable.logo).setTicker("Ticker").setWhen(System.currentTimeMillis())
                            .setNumber(1).setContentTitle("SmaRed").setContentText("[신한체크카드 사용] 가계부에 추가하시겠습니까?")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendnoti).setAutoCancel(true).setOngoing(true);
                    notificationManager.notify(1, builder.build());
                    Toast.makeText(context, smsMessages[i].getMessageBody(), Toast.LENGTH_SHORT).show();

                }


            }
        }
    }
}








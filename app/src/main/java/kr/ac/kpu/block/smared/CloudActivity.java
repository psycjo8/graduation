/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.ac.kpu.block.smared;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CloudActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyC6FyPlYCwLuwVhE8s3Td_zbbbwcMr41Oc";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = CloudActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;
    Bitmap image; //사용되는 이미지
    File imgFile = new File("/storage/emulated/0/SmaRed/s2.jpg");



    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    Context context;

    String stUseItem;
    String stPrice;
    String stPaymemo;
    Calendar c = Calendar.getInstance(); // Firebase내에 날짜로 저장
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("MM");
    SimpleDateFormat day = new SimpleDateFormat("dd");
    String stYear = year.format(c.getTime());
    String stMonth = month.format(c.getTime());
    String stDay = day.format(c.getTime());

    static EditText etPaymemo ;
    static CalendarView cvCalender;
    static ArrayAdapter<String> spinneradapter;
    static Spinner spnPrice;
    static List<String> listItems = new ArrayList<String>();
    Button btnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        etPaymemo = (EditText) findViewById(R.id.etPaymemo);
        cvCalender = (CalendarView) findViewById(R.id.cvCalender);
        final Spinner spnUseitem = (Spinner) findViewById(R.id.spnUseitem);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        final RadioButton rbConsume = (RadioButton) findViewById(R.id.rbConsume);
        RadioButton rbIncome = (RadioButton) findViewById(R.id.rbIncome);
        spnPrice = (Spinner) findViewById(R.id.spnPrice);
        btnFinish = (Button) findViewById(R.id.btnFinish);

        spnUseitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stUseItem = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cvCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                stYear = Integer.toString(year);
                stMonth = Integer.toString(month+1);
                stDay = Integer.toString(day);
                Toast.makeText(CloudActivity.this, stYear+"-"+stMonth+"-"+stDay, Toast.LENGTH_SHORT).show();
            }
        });

        spnPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stPrice = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPaymemo = etPaymemo.getText().toString();
                c = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HHmmss");
                String stTime = time.format(c.getTime());
                Hashtable<String, String> ledger   // HashTable로 연결
                        = new Hashtable<String, String>();
                ledger.put("useItem", stUseItem);
                ledger.put("price", stPrice);
                ledger.put("paymemo",stPaymemo);


                if (rbConsume.isChecked()) {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("지출").child(stTime).setValue(ledger);
                } else {
                    myRef.child(user.getUid()).child("Ledger").child(stYear).child(stMonth).child(stDay).child("수입").child(stTime).setValue(ledger);
                }

                Toast.makeText(CloudActivity.this, "저장하였습니다.", Toast.LENGTH_SHORT).show();


            }
        });

        if(imgFile.exists()) {
            image = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); //샘플이미지파일
        }

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(CloudActivity.this, TabActivity.class);
                startActivity(in);
            }
        });
        spinneradapter = new ArrayAdapter<String>(CloudActivity.this, R.layout.support_simple_spinner_dropdown_item, listItems);
        spnPrice.setAdapter(spinneradapter);




        uploadImage(image);
    }

    public void uploadImage(Bitmap image) {
        if (image != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                image,
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (Exception e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
          //      Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
        //    Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<CloudActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(CloudActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            CloudActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {

                String payMemo;
                String payMemoResult="";
                String price;
                String priceResult="";
                String date;
                String dateResult= "";
                String finalResult = "";
                payMemo = result.replaceAll("[^.*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*\\n]","");
                price = result.replaceAll("[^0-9\\.\\,\\n]","");
                date = result.replaceAll("[^0-9\\.\\,\\-\\n]","");
                dateResult += extractDate(date);
                payMemoResult += extractPaymemo(payMemo);
                priceResult += extractPrice(price);
                finalResult = dateResult + priceResult + payMemoResult;

                etPaymemo.setText(payMemoResult);

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading


        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message =("I found these things:\n\n");

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message += labels.get(0).getDescription();
        } else {
            message += "nothing";
        }

        return message;
    }


    public static String extractDate(String str) {

        List<String> list = new ArrayList<String>();
        String dateResult= "";
        Matcher matcher ;

        if (str.isEmpty()) {
            matcher = null;
        } else {
            String patternStr = "(19|20)\\d{2}[- /.]*(0[1-9]|1[012])[- /.]*(0[1-9]|[12][0-9]|3[01])"; // 날자를 패턴으로 지정

            int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(patternStr, flags);
            matcher = pattern.matcher(str);

            int count = 0;
            while (matcher.find()) {
                list.add(matcher.group());
            }
        }

        for(int i=0; i<list.size(); i++) {
            dateResult += list.get(i);
        }

        String parts[] = dateResult.split("-");

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milliTime = calendar.getTimeInMillis();
        cvCalender.setDate(milliTime, true, true);

        return dateResult;
    }

    public static String extractPaymemo(String str) {
        String result="";

        StringTokenizer stringTokenizer = new StringTokenizer(str,"\n");
        result = stringTokenizer.nextToken();

        return result;
    }
    public static String extractPrice(String str) {
        String result="";
        String temp;
        StringTokenizer stringTokenizer = new StringTokenizer(str,"\n");
        while(stringTokenizer.hasMoreTokens()){
            temp = stringTokenizer.nextToken();
            if(temp.contains(",") && temp.contains("0")) {
                temp = temp.replaceAll("\\,", "");
                temp = temp.replaceAll("\\.", "");
                listItems.add(temp);
                spinneradapter.notifyDataSetChanged();
            }
        }

        return result;
    }

}

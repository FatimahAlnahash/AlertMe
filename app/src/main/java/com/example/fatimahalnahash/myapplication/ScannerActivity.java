package com.example.fatimahalnahash.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult, textViewChecking;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    Food food = new Food();
    boolean warning =false;
    ArrayList<String> allergyFound = new ArrayList<>();

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<String> result = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(json);

                    String ingredients = root.getString("ingredients");
                    Log.d("demo", ingredients);
                    String[]ingr = ingredients.split(" |\\,");

                    for (int i = 0; i < ingr.length; i++) {
                        if(allergyList.contains(ingr[i])){
                           // textViewChecking.setText("Do Not Eat !!!");
                            //Toast.makeText(ScannerActivity.this, "Do Not Eat!!!!!!!", Toast.LENGTH_SHORT).show();
                        }
                        if(ingr[i].equalsIgnoreCase(" ") || ingr[i].equalsIgnoreCase("is") ||
                                ingr[i].equalsIgnoreCase("and/or") || ingr[i].equalsIgnoreCase("are") ){
                            //Log.d("demo", "ingr: " + ingr[i]);
                        }else {
                            food.addIngredients(ingr[i]);
                            result.add(ingr[i]);
                        }
                    }
                  //  result.add(food);
                 /*   String stuff="";

                    for (int i = 0; i <allergyList.size() ; i++) {
                        if(food.hasIngredients(allergyList.get(i))){
                            stuff+=" "+ allergyList.get(i) + " ";
                            allergyFound.add(allergyList.get(i));
                            warning=true;
                        }
                    }*/


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null) {
                for (int i = 0; i <result.size() ; i++) {
                    //Log.d("demo", "result: " + result.get(i));
                    String stuff="";

                    for (int j = 0; j <allergyList.size() ; j++) {
                        if(food.hasIngredients(allergyList.get(j))){
                            stuff +=" "+ allergyList.get(j) + " ";
                            allergyFound.add(allergyList.get(j));
                            warning=true;
                            //textViewChecking.setText("Do Not Eat !!!");
                        }
                    }
                    if(warning){
                        textViewChecking.setText("Do Not Eat !!!");
                        txtResult.setText("Do Not Eat. it contains: " + stuff);
                       /* try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        //  Toast.makeText(ScannerActivity.this, "Do Not Eat!!!!!!!", Toast.LENGTH_SHORT).show();

                    }
                    //txtResult.setText("Do Not Eat. it contains: " + result.toString());
                }
            } else {
                Log.d("-", "null result");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    ArrayList<String> allergyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Intent intent = getIntent();
        allergyList =  intent.getStringArrayListExtra("allergyList");


        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        textViewChecking = findViewById(R.id.textViewChecking);
        barcodeDetector = new BarcodeDetector.Builder(this)
                //.setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(ScannerActivity.this,
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create vibrate
                           // Log.d("demo", "List of Allergies" + allergyList.toString());
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            txtResult.setText(qrcodes.valueAt(0).displayValue);

                            String barValue = qrcodes.valueAt(0).displayValue; //https://www.datakick.org/api/items/000000000000
                            String url = "https://www.datakick.org/api/items/" + qrcodes.valueAt(0).displayValue;

                            if(isConnected()){
                                //Toast.makeText(ScannerActivity.this, "Connected To Internet", Toast.LENGTH_SHORT).show();
                                new GetDataAsync().execute(url);
                            }else{
                                Toast.makeText(ScannerActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }
}

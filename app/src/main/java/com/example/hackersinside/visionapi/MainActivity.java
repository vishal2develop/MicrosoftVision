package com.example.hackersinside.visionapi;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
public VisionServiceClient visionServiceClient=new VisionServiceRestClient("f64c3b398db1425882a7853a30325790");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.baby2);

        ImageView imageView=(ImageView) findViewById(R.id.image);
        Button identify=(Button) findViewById(R.id.process);


        imageView.setImageBitmap(bitmap);

        //Convert image to stream
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream=new ByteArrayInputStream(outputStream.toByteArray());

        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AsyncTask<InputStream,String,String> vision= new AsyncTask<InputStream, String, String>() {
                    ProgressDialog dialog=new ProgressDialog(MainActivity.this);
                    @Override
                    protected String doInBackground(InputStream... params) {
                        try
                        {
                            publishProgress("Recognizing...");
                            String[] features={"Description"};
                            String[] details= {};

                            AnalysisResult result=visionServiceClient.analyzeImage(params[0],features,details);
                            String strResult=new Gson().toJson(result);
                            return strResult;

                        }
                        catch (Exception e)
                        {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        dialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        dialog.dismiss();

                        AnalysisResult result=new Gson().fromJson(s,AnalysisResult.class);
                        TextView description=(TextView) findViewById(R.id.description);
                        StringBuilder stringBuilder=new StringBuilder();

                        for(Caption caption:result.description.captions)
                        {
                            stringBuilder.append(caption.text);

                        }
                        description.setText(stringBuilder);
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        dialog.setMessage(values[0]);
                    }
                };
            vision.execute(inputStream);
            }
        });

    }
}

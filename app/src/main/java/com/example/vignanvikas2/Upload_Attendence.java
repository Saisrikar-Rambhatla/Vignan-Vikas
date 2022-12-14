package com.example.vignanvikas2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Upload_Attendence extends AppCompatActivity{
    private Button uploadCsv,uploadJson;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_attendence);
        uploadCsv=findViewById(R.id.convertCsvToJson);
        uploadJson=findViewById(R.id.navigateFirebase);
        uploadCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://csvjson.com/csv2json";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        uploadJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://console.firebase.google.com/u/0/project/vignan-vikas/database/vignan-vikas-default-rtdb/data";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
}
  package com.example.vignanvikas2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

  public class MainActivity extends AppCompatActivity implements View.OnClickListener {
      CardView uploadNotice,addAttendance,uploadNotes;
      Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice = findViewById(R.id.addNotice);
        uploadNotice.setOnClickListener(this);
        addAttendance=findViewById(R.id.updateAttendence);
        addAttendance.setOnClickListener(this);
        uploadNotes=findViewById(R.id.uploadBooks);
        uploadNotes.setOnClickListener(this);
    }

      @Override
      public void onClick(View view) {
          switch (view.getId())
          {
              case R.id.addNotice:
                  intent=new Intent(MainActivity.this,UploadNotice.class);
                   startActivity(intent);
                  break;
              case R.id.updateAttendence:
                  intent=new Intent(MainActivity.this,Upload_Attendence.class);
                  startActivity(intent);
                  break;
              case R.id.uploadBooks:
                  intent=new Intent(MainActivity.this,UploadNotes.class);
                  startActivity(intent);
                  break;
          }
      }
  }
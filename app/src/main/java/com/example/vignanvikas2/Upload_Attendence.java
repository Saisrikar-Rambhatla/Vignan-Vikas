package com.example.vignanvikas2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Upload_Attendence extends AppCompatActivity {
    private Spinner attendSection;
    private Spinner attendDepartment;
    private CardView addAttendance;
    private Button uploadAttendence;
    private EditText fileTitle;
    private String department;
    private String section;
    private final int REQ=1;
    private Uri attendanceData;
    private Bitmap bitMap;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl="";
    private ProgressDialog pd;
    private TextView fileTextView;
    private String fileName,title;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_attendence);
        //reference= FirebaseDatabase.getInstance().getReference().child(department).child(section);
        //storageReference= FirebaseStorage.getInstance().getReference();
        attendDepartment=findViewById(R.id.attendDepartment);
        attendSection=findViewById(R.id.attendSection);
        addAttendance=findViewById(R.id.addAttendance);
        fileTitle=findViewById(R.id.fileTitle);
        fileTextView=findViewById(R.id.fileTextView);
        pd=new ProgressDialog(this);
        uploadAttendence=findViewById(R.id.uploadAttendenceButton);
        String []itemsDept=new String[]{"Select Department","CSE","ECE","Mechanical","Civil","EIE"};
        String []itemsSection=new String[]{"Select Section","A","B","C"};
        attendDepartment.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,itemsDept));
        attendDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department=attendDepartment.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        attendSection.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,itemsSection));
        attendSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                section=attendSection.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addAttendance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                openGallery();
            }
        });
        uploadAttendence.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                title=fileTitle.getText().toString();
                if(attendanceData == null){
                    Toast.makeText(Upload_Attendence.this,"Please upload file",Toast.LENGTH_SHORT).show();
                }
                else if(section.equals("Select Section")){
                    Toast.makeText(Upload_Attendence.this,"Please Select Section",Toast.LENGTH_SHORT).show();
                }
                else if(department.equals("Select Department")){
                    Toast.makeText(Upload_Attendence.this,"Please Select Department",Toast.LENGTH_SHORT).show();
                }
                else if(title.isEmpty()){
                    fileTitle.setError("Empty");
                    fileTitle.requestFocus();
                }
                else{
                    reference= FirebaseDatabase.getInstance().getReference();
                    storageReference= FirebaseStorage.getInstance().getReference();
                    pd.setMessage("Uploading...");
                    pd.show();
                    uploadFile();
                }
            }
        });
    }

    private void uploadFile() {
        /*ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImage=baos.toByteArray();
        final StorageReference filePath,sRef;*/
        pd.setTitle("Please wait");
        pd.setMessage("Uploading file");
        pd.show();
        StorageReference filePath=storageReference.child("Attendance/"+fileName+"-"+System.currentTimeMillis()+".csv");
        //final UploadTask uploadTask=filePath.putBytes(finalImage);
        filePath.putFile(attendanceData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri=uriTask.getResult();
                        uploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Upload_Attendence.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadData(String downloadUrl) {
        reference=reference.child("Attendance/").child(department+"/"+section);
        final String uniqueKey = reference.push().getKey();
        HashMap data=new HashMap();
        data.put("fileTitle",title);
        data.put("fileUrl",downloadUrl);
        reference.child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(Upload_Attendence.this,"File uploaded sucessfully",Toast.LENGTH_SHORT).show();
                fileTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Upload_Attendence.this,"Failed to upload url",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select Pdf File"),REQ);
    }
    @SuppressLint("Range")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            attendanceData=data.getData();
            if(attendanceData.toString().startsWith("content://")){
                Cursor cursor = Upload_Attendence.this.getContentResolver().query(attendanceData, null, null, null, null);
                try {
                    if(cursor!=null && cursor.moveToFirst()){
                        fileName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(attendanceData.toString().startsWith("file://")){
                fileName = new File(attendanceData.toString()).getName();

            }
            fileTextView.setText(fileName);
        }
    }
}
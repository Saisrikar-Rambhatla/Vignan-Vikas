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

import java.io.File;
import java.util.HashMap;

public class UploadNotes extends AppCompatActivity {
    private Spinner dept;
    private EditText faculty;
    private Spinner subject;
    private Button uploadNotes;
    private CardView addNotes;
    private EditText fileTitle;
    private String department;
    private String sub;
    private final int REQ=1;
    private Uri notesData;
    private Bitmap bitMap;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl="";
    private ProgressDialog pd;
    private TextView fileTextView;
    private String fileName,title;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notes);
        dept=findViewById(R.id.department);
        subject=findViewById(R.id.subject);
        faculty=findViewById(R.id.faculty);
        fileTitle=findViewById(R.id.fileTitle);
        fileTextView=findViewById(R.id.fileTextView);
        addNotes=findViewById(R.id.addNotes);
        pd=new ProgressDialog(this);
        uploadNotes=findViewById(R.id.uploadNotesButton);
        String []itemsDept=new String[]{"Select Department","CSE","ECE","Mechanical","Civil","EIE"};
        String []itemsSubject=new String[]{"Select Section","Analog and Digital Electornics","Operating systems","DBMS","COA","Computer networks","Machine learning","Data mining","Data Structures and Algorithms"};
        dept.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,itemsDept));
        dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department=dept.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        subject.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,itemsSubject));
        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sub=subject.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addNotes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                openGallery();
            }
        });
        uploadNotes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                title=fileTitle.getText().toString();
                if(notesData == null){
                    Toast.makeText(UploadNotes.this,"Please upload file",Toast.LENGTH_SHORT).show();
                }
                else if(sub.equals("Select Section")){
                    Toast.makeText(UploadNotes.this,"Please Select Subject",Toast.LENGTH_SHORT).show();
                }
                else if(department.equals("Select Department")){
                    Toast.makeText(UploadNotes.this,"Please Select Department",Toast.LENGTH_SHORT).show();
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
        StorageReference filePath=storageReference.child("Notes/"+fileName+"-"+System.currentTimeMillis()+".pdf");
        //final UploadTask uploadTask=filePath.putBytes(finalImage);
        filePath.putFile(notesData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                Toast.makeText(UploadNotes.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String downloadUrl) {
        reference=reference.child("Notes/").child(department+"/"+sub);
        final String uniqueKey = reference.push().getKey();
        HashMap data=new HashMap();
        data.put("fileTitle",title);
        data.put("fileUrl",downloadUrl);
        reference.child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadNotes.this,"File uploaded sucessfully",Toast.LENGTH_SHORT).show();
                fileTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadNotes.this,"Failed to upload url",Toast.LENGTH_SHORT).show();
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
            notesData=data.getData();
            if(notesData.toString().startsWith("content://")){
                Cursor cursor = UploadNotes.this.getContentResolver().query(notesData, null, null, null, null);
                try {
                    if(cursor!=null && cursor.moveToFirst()){
                        fileName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(notesData.toString().startsWith("file://")){
                fileName = new File(notesData.toString()).getName();

            }
            fileTextView.setText(fileName);
        }
    }
}
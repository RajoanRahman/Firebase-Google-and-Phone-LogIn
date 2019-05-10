package com.example.user.firebasegoogleandphonelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    Button logOut,submitButton;
    FirebaseAuth mAuth;
    EditText nameEdit,ageEdit,hobbyEdit;
    TextView googleNameText,googleEmailName;
    FirebaseFirestore db;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent=getIntent();

        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        loadingBar=new ProgressDialog(this);

        logOut=findViewById(R.id.log_out);
        submitButton=findViewById(R.id.submit_button);
        nameEdit=findViewById(R.id.edit_text_name);
        ageEdit=findViewById(R.id.edit_text_age);
        hobbyEdit=findViewById(R.id.edit_text_hobby);
        googleNameText=findViewById(R.id.google_text_name);
        googleEmailName=findViewById(R.id.google_email_name);

        String name=intent.getStringExtra("googleName");
        googleNameText.setText(name);

        String email=intent.getStringExtra("googleEmail");
        googleEmailName.setText(email);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendUserToMain();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Loading");
                loadingBar.setMessage("Wait,while uploading your data to FireStore");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                saveDataToFireStrore();
            }
        });
    }

    private void saveDataToFireStrore() {

        String name=nameEdit.getText().toString();
        String age=ageEdit.getText().toString();
        String hobby=hobbyEdit.getText().toString();

        HashMap<String,Object> map=new HashMap<>();
        map.put("Name",name);
        map.put("Age",age);
        map.put("Hobby",hobby);

        db.collection("Users").add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this,"Your data has uploaded successfully",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
    }

    private void sendUserToMain() {
        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
        startActivity(intent);
    }
}

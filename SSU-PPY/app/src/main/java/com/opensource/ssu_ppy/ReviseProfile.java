package com.opensource.ssu_ppy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviseProfile extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseFirestore myDb;
    private FirebaseUser user;
    private FirebaseUser userId;
    private Button reviseButton;
    private TextView myName;
    private TextView myAge;
    private TextView myStudentNum;
    private TextView mySex;
    private EditText reviseHobby;
    private EditText reviseMbti;
    private EditText reviseMajor;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_profile);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        myDb = FirebaseFirestore.getInstance();

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("회원정보수정");
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        reviseButton = (Button) findViewById(R.id.reviseProfileButton);
        myName = (TextView) findViewById(R.id.myName);
        myAge = (TextView) findViewById(R.id.myAge);
        reviseMajor = (EditText) findViewById(R.id.reviseMajor);
        myStudentNum = (TextView) findViewById(R.id.myStudentNum);
        reviseHobby = (EditText) findViewById(R.id.reviseHobby);
        reviseMbti = (EditText) findViewById(R.id.reviseMbti);
        mySex = (TextView) findViewById(R.id.mySex);

        db.collection("user").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String name = (String) document.getData().get("name");
                            String age = (String) document.getData().get("age");
                            String studentNum = (String) document.getData().get("studentNum");
                            Boolean sex = (Boolean) document.getData().get("is_man");
                            if(sex){
                                mySex.setText("남자");
                            }else{
                                mySex.setText("여자");
                            }
                            myName.setText(name);
                            myAge.setText(age);
                            myStudentNum.setText(studentNum);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
        reviseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviseProfile();
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    public void reviseProfile() {
        userId = auth.getCurrentUser();
        UserModel user = new UserModel();
        user.setMajor(reviseMajor.getText().toString());
        user.setHobby(reviseHobby.getText().toString());
        user.setMbti(reviseMbti.getText().toString().toUpperCase());
        if(user.getMajor().equals("")) { Toast.makeText(ReviseProfile.this, "학과(부)를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getHobby().equals("")) { Toast.makeText(ReviseProfile.this, "취미를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getMbti().equals("")) { Toast.makeText(ReviseProfile.this, "MBTI를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(!user.getMbti().equals("ISTJ") && !user.getMbti().equals("ISFJ") && !user.getMbti().equals("INFJ") && !user.getMbti().equals("INTJ") &&
                !user.getMbti().equals("ISTP") && !user.getMbti().equals("ISFP") && !user.getMbti().equals("INFP") && !user.getMbti().equals("INTP") &&
                !user.getMbti().equals("ESTP") && !user.getMbti().equals("ESFP") && !user.getMbti().equals("ENFP") && !user.getMbti().equals("ENTP") &&
                !user.getMbti().equals("ESTJ") && !user.getMbti().equals("ESFJ") && !user.getMbti().equals("ENFJ") && !user.getMbti().equals("ENTJ")) {
            Toast.makeText(ReviseProfile.this, "존재하지 않는 MBTI 입니다.", Toast.LENGTH_SHORT).show(); return;
        }
        db.collection("user").document(userId.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            user.setName((String) document.getData().get("name"));
                            user.setAge((String) document.getData().get("age"));
                            user.setStudentNum((String) document.getData().get("studentNum"));
                            user.setIs_man((Boolean) document.getData().get("is_man"));
                            myDb.collection("user").document(userId.getUid()).set(user);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }
}
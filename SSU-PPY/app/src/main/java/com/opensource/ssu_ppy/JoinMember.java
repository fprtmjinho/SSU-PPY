package com.opensource.ssu_ppy;

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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinMember extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Button joinButton;
    private EditText joinEmail;
    private EditText joinPwd;
    private EditText joinName;
    private EditText joinAge;
    private EditText joinMajor;
    private EditText joinStudentNum;
    private EditText joinHobby;
    private EditText joinMbti;
    private CheckBox joinIsMan;
    private CheckBox joinIsWoman;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("회원정보수정");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        joinButton = (Button) findViewById(R.id.join_member_button);
        joinEmail = (EditText) findViewById(R.id.join_email);
        joinPwd = (EditText) findViewById(R.id.join_password);
        joinName = (EditText) findViewById(R.id.join_name);
        joinAge = (EditText) findViewById(R.id.join_age);
        joinMajor = (EditText) findViewById(R.id.join_major);
        joinStudentNum = (EditText) findViewById(R.id.join_student_num);
        joinHobby = (EditText) findViewById(R.id.join_hobby);
        joinMbti = (EditText) findViewById(R.id.join_mbti);
        joinIsMan = (CheckBox) findViewById(R.id.join_is_man);
        joinIsWoman = (CheckBox) findViewById(R.id.join_is_woman);
        joinIsMan.setOnClickListener(genderCheckBox);
        joinIsWoman.setOnClickListener(genderCheckBox);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = joinEmail.getText().toString();
                String[] isSoongsil = email.split("@");
                if(isSoongsil.length == 2) {
                    if(isSoongsil[1].equals("soongsil.ac.kr")) {
                        joinMember(email, joinPwd.getText().toString());
                    } else {
                        Toast.makeText(JoinMember.this,"숭실대 전자메일로만 로그인 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JoinMember.this,"이메일 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    View.OnClickListener genderCheckBox = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean is_checked = ((CheckBox) view).isChecked();
            switch (view.getId()) {
                case R.id.join_is_man:
                    joinIsWoman.setChecked(false);
                    break;
                case R.id.join_is_woman:
                    joinIsMan.setChecked(false);
                    break;
            }
        }
    };

    public void joinMember(String email, String password) {
        UserModel user = new UserModel();
        user.setName(joinName.getText().toString());
        user.setAge(joinAge.getText().toString());
        user.setMajor(joinMajor.getText().toString());
        user.setHobby(joinHobby.getText().toString());
        user.setMbti(joinMbti.getText().toString().toUpperCase());
        user.setStudentNum(joinStudentNum.getText().toString());
        if(email.equals("")) { Toast.makeText(JoinMember.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(password.equals("")) { Toast.makeText(JoinMember.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getName().equals("")) { Toast.makeText(JoinMember.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getAge().equals("")) { Toast.makeText(JoinMember.this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getMajor().equals("")) { Toast.makeText(JoinMember.this, "학과(부)를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getStudentNum().equals("")) { Toast.makeText(JoinMember.this, "학번을 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getHobby().equals("")) { Toast.makeText(JoinMember.this, "취미를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getMbti().equals("")) { Toast.makeText(JoinMember.this, "MBTI를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(!user.getMbti().equals("ISTJ") && !user.getMbti().equals("ISFJ") && !user.getMbti().equals("INFJ") && !user.getMbti().equals("INTJ") &&
                !user.getMbti().equals("ISTP") && !user.getMbti().equals("ISFP") && !user.getMbti().equals("INFP") && !user.getMbti().equals("INTP") &&
                !user.getMbti().equals("ESTP") && !user.getMbti().equals("ESFP") && !user.getMbti().equals("ENFP") && !user.getMbti().equals("ENTP") &&
                !user.getMbti().equals("ESTJ") && !user.getMbti().equals("ESFJ") && !user.getMbti().equals("ENFJ") && !user.getMbti().equals("ENTJ")) {
            Toast.makeText(JoinMember.this, "존재하지 않는 MBTI 입니다.", Toast.LENGTH_SHORT).show(); return;
        }
        if(!joinIsMan.isChecked() && !joinIsWoman.isChecked()) {
            Toast.makeText(JoinMember.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setIs_man(joinIsMan.isChecked());

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(JoinMember.this, "이메일 인증을 진행해주세요", Toast.LENGTH_SHORT).show();
                                                db.collection("user").document(auth.getUid()).set(user);
                                                ArrayList<String> myRoom = new ArrayList<String>();
                                                Map<String, ArrayList<String>> data = new HashMap<>();
                                                data.put("myRoom",myRoom);
                                                db.collection("userOpenChat").document(auth.getUid()).set(data);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // 메인 화면으로 이동
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(JoinMember.this, "인증 이메일 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(JoinMember.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*public void createUser2(String email, String password) {
        UserModel user = new UserModel();
        user.setName(joinName.getText().toString());
        user.setAge(joinAge.getText().toString());
        user.setMajor(joinMajor.getText().toString());
        user.setHobby(joinHobby.getText().toString());
        user.setMbti(joinMbti.getText().toString().toUpperCase());
        user.setStudentNum(joinStudentNum.getText().toString());
        if(email.equals("")) { Toast.makeText(JoinMember.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(password.equals("")) { Toast.makeText(JoinMember.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getName().equals("")) { Toast.makeText(JoinMember.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getAge().equals("")) { Toast.makeText(JoinMember.this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getMajor().equals("")) { Toast.makeText(JoinMember.this, "학과(부)를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getStudentNum().equals("")) { Toast.makeText(JoinMember.this, "학번을 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getHobby().equals("")) { Toast.makeText(JoinMember.this, "취미를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(user.getMbti().equals("")) { Toast.makeText(JoinMember.this, "MBTI를 입력해주세요.", Toast.LENGTH_SHORT).show(); return; }
        if(!user.getMbti().equals("ISTJ") && !user.getMbti().equals("ISFJ") && !user.getMbti().equals("INFJ") && !user.getMbti().equals("INTJ") &&
                !user.getMbti().equals("ISTP") && !user.getMbti().equals("ISFP") && !user.getMbti().equals("INFP") && !user.getMbti().equals("INTP") &&
                !user.getMbti().equals("ESTP") && !user.getMbti().equals("ESFP") && !user.getMbti().equals("ENFP") && !user.getMbti().equals("ENTP") &&
                !user.getMbti().equals("ESTJ") && !user.getMbti().equals("ESFJ") && !user.getMbti().equals("ENFJ") && !user.getMbti().equals("ENTJ")) {
            Toast.makeText(JoinMember.this, "존재하지 않는 MBTI 입니다.", Toast.LENGTH_SHORT).show(); return;
        }
        if(!joinIsMan.isChecked() && !joinIsWoman.isChecked()) {
            Toast.makeText(JoinMember.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setIs_man(joinIsMan.isChecked());
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(JoinMember.this, "이메일 인증을 진행해주세요", Toast.LENGTH_SHORT).show();
                            db.collection("user").document(auth.getUid()).set(user);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // 메인 화면으로 이동
                            finish();
                            startActivity(intent);
                        } else {
                        }
                    }
                });
    }*/
}
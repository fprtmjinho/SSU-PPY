package com.opensource.ssu_ppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button switchJoinButton;
    EditText loginEmail;
    EditText loginPwd;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.login_button);
        switchJoinButton = (Button) findViewById(R.id.switch_join_button);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPwd = (EditText) findViewById(R.id.login_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loginWithPassword2(loginEmail.getText().toString(),loginPwd.getText().toString());
                loginWithPassword(loginEmail.getText().toString()+"@soongsil.ac.kr",loginPwd.getText().toString());
            }
        });

        switchJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinMember.class); // 회원 가입 뷰로 이동
                finish();
                startActivity(intent);
            }
        });
    }

    void loginWithPassword(String email, String password) {
        if(email.equals("")) { Toast.makeText(MainActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show(); return; }
        if(password.equals("")) { Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show(); return; }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(auth.getCurrentUser().isEmailVerified()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SelectMenu.class); // 메뉴 선택 뷰로 이동
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "이메일에서 인증을 진행해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                            FirebaseUser user = auth.getCurrentUser();
                        } else {
                            Toast.makeText(MainActivity.this, "아이디, 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /*void loginWithPassword2(String email, String password) {
        if(email.equals("")) { Toast.makeText(MainActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show(); return; }
        if(password.equals("")) { Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show(); return; }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SelectMenu.class); // 메뉴 선택 뷰로 이동
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "아이디, 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/
}
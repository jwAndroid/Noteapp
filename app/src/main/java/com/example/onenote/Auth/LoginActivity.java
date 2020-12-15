package com.example.onenote.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onenote.MainActivity;
import com.example.onenote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText lEmail,lPassword;
    Button loginNow;
    TextView forgetPass,createAcc;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("로그인");

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);
        spinner = findViewById(R.id.progressBar3);
        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        showWarning();

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);

                String mEmail = lEmail.getText().toString().trim();
                String mPassword = lPassword.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {

                    Toast.makeText(LoginActivity.this ,
                            "All fileds are required!"
                            ,Toast.LENGTH_SHORT).show();

                }else if(mPassword.length() < 6){
                    Toast.makeText(LoginActivity.this ,
                            "Password at least 6 charters"
                            ,Toast.LENGTH_SHORT).show();
                }else{
                    login(mEmail , mPassword);
                }

            }
        });
    }

    private void login(String email , String passWord) {

        /* 현재uid에 남아있는 노트데이터 삭제진행 유저가 isAnonymous 일떄의 진행처리
           하나의 유저가 isAnonymous 와 real 일수도 있으니까 당연히 하나로 통합시켜줘야한다.
           그렇기떄문에 만약 isAnonymous일떄를 통합시켜준다 */

        if(firebaseAuth.getCurrentUser().isAnonymous()){
            FirebaseUser user = firebaseAuth.getCurrentUser(); /* firebaseAuth.getCurrentUser()).isAnonymous()의 user */

            firebaseFirestore
                    .collection("notes")
                    .document(user.getUid()) /* document user.getUid()의 collection 을 .delete()   */
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG , user.getUid()+"의 모든 임시 메모가 삭제됨");

                        }
                    });
            // delete Temp user 임시유저 삭제진행
            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG , user.getUid()+"의 임시 유저가 삭제됨");

                }
            });
        }

        //진짜 로그인와 유저객체 로그인진행
        firebaseAuth.signInWithEmailAndPassword(email,passWord)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                });


    }

    private void showWarning() {
        final AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("안내문")
                .setMessage("회원가입시 임시메모를 동기화 할 수 있습니다.")
                .setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext() , RegisterActivity.class));
                        finish();
                    }
                }).setNegativeButton("로그인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        warning.show();
    }
}
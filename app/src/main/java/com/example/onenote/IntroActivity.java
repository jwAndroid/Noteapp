package com.example.onenote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "IntroActivity" ;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        /*파베 어스객체 생성 */

        /*스플래시를 위한 핸들러 객체생성 2000후 진행 */
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (firebaseUser != null){
                /*getCurrentUser 존재한다면 바로 넘어가주고 , (이미 존재한다면)*/
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }else{
                /* 그렇지않으면 firebaseAuth.signInAnonymously() 로 익명로그인 */
                firebaseAuth
                        .signInAnonymously()
                        .addOnSuccessListener(authResult -> {
                            /*Success 리스너 >> 후에 진행 */
                            startActivity(new Intent(getApplicationContext() , MainActivity.class));
                            Log.d(TAG , "signInAnonymously()");
                            finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(IntroActivity.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

        },2000);
    }


}
package com.example.onenote.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onenote.MainActivity;
import com.example.onenote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText rUserName,rUserEmail,rUserPass,rUserConfPass;
    Button syncAccount;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    // 17
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass = findViewById(R.id.password);
        rUserConfPass = findViewById(R.id.passwordConfirm);

        syncAccount = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        firebaseAuth = FirebaseAuth.getInstance();

        loginAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                finish();
            }
        });

        syncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO : register() 동작진행.
                register();

            }
        });


    }

    private void register() {
        /* 유저 editText  */
        final String uUsername = rUserName.getText().toString();
        String uUserEmail = rUserEmail.getText().toString();
        String uUserPass = rUserPass.getText().toString();
        String uConfPass = rUserConfPass.getText().toString();

        if(uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
            Toast.makeText(RegisterActivity.this, "All Fields Are Required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!uUserPass.equals(uConfPass)){
            rUserConfPass.setError("Password Do not Match.");
        }

        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(uUserEmail,uUserPass);
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "Notes are Register.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this , MainActivity.class));

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        //UserProfileChangeRequest 를 이용하여 drawerLayout에 uUsername data를 set해주는작업 이부분같은경우는 user profile을 realTime db로 처리가능
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uUsername) // name을 set
                                .build();
                        user.updateProfile(request); /* 마지막으로 request를 받아 updateProfile */

                        startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                        /*drawerLayout에 user name 이 안나오는 버그 , 한번더 intent를 진행하여 username을 set */
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to Connect. Try Again", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegisterActivity.this, "Failed to Connect. Try Again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }



}
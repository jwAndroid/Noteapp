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
    FirebaseAuth firebaseAuth; /* text값 >> auth진행 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inIt();

        loginAct.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
            finish();
        });

        syncAccount.setOnClickListener(v -> {
            //TODO : register() 동작진행.
            register();
        });


    }//...........

    private void inIt(){
        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass = findViewById(R.id.password);
        rUserConfPass = findViewById(R.id.passwordConfirm);
        syncAccount = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void register() {
        /* 개인정보 할당 */
        final String uUsername = rUserName.getText().toString();
        String uUserEmail = rUserEmail.getText().toString();
        String uUserPass = rUserPass.getText().toString();
        String uConfPass = rUserConfPass.getText().toString();

        /* isEmpty 제어 */
        if(uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
            Toast.makeText(RegisterActivity.this, "4가지 모두 넣어주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        /*패스워드가 다르다면 제어 */
        if(!uUserPass.equals(uConfPass)){
            rUserConfPass.setError("패스워드가 다릅니다");
        }
        progressBar.setVisibility(View.VISIBLE);

        /* 여기부터 진행 파라메터로 유저한테 받은 이메일과 패스워드 파라메터로 던져서 EmailAuthProvider로써 크리덴셜을 만들어준다.
        * 이 크리덴셜을 만들었다면 ,linkWithCredential 로써 Auth객체와 연동후 제어  */
        AuthCredential credential = EmailAuthProvider.getCredential(uUserEmail,uUserPass);
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .linkWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    /*성공했다면 메인으로 넘겨주고 */
                    startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //UserProfileChangeRequest 를 이용하여 drawerLayout에
                    // uUsername data를 set해주는작업 이부분같은경우는 user profile을 realTime db로 처리가능

                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(uUsername) // name을 set
                            .build();
                    user.updateProfile(request); /* 마지막으로 request를 받아 updateProfile */
                    startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                    /*drawerLayout에 user name 이 안나오는 버그 , 한번더 intent를 진행하여 username을 set */
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    finish();

                })
                .addOnFailureListener(e -> progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }



}
package com.example.onenote;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";

    FirebaseFirestore firebaseFirestore;   /* FirebaseFirestore setting  */
    EditText noteTitle,noteContent; /*  noteTitle,noteContent : content_add_note 쪽 editText */
    ProgressBar progressBarSave;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        noteContent = findViewById(R.id.addNoteContent); /*  noteContent : content_add_note 쪽 editText */
        noteTitle = findViewById(R.id.addNoteTitle); /*  noteTitle: content_add_note 쪽 editText */
        progressBarSave = findViewById(R.id.progressBar);   /*  FirebaseFirestore 의 save작업 progressBar */

//        user = FirebaseAuth.getInstance().getCurrentUser();

        /* FirebaseFirestore 로 save 작업  */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            String nTitle = noteTitle.getText().toString();
            String nContent = noteContent.getText().toString();

            /* 둘중하나가 isEmpty 이라면 제어 */
            if (nTitle.isEmpty() || nContent.isEmpty()){
                Toast.makeText(AddNoteActivity.this , "제목과 내용을 작성해주세요." , Toast.LENGTH_SHORT).show();
                return;
            }

            progressBarSave.setVisibility(View.VISIBLE); /*  progressBar start */

            /*  DocumentReference로의 DB SETTING 시작  */
            DocumentReference documentReference = firebaseFirestore //레퍼런스 가져와서
                    .collection("notes")
                    .document(user.getUid())
                    .collection("myNotes")
                    .document(); /*이쪽으로 써주고 */

            /*  DocumentReference DB collection ,notes로 document() 진행 */
            Map<String , Object> hashMap = new HashMap<>(); /*  Hash로 nTitle , nContent 를 documentReference.set(hashMap)  */
            hashMap.put("title" , nTitle);
            hashMap.put("content" , nContent);
            //해쉬테이블로 묶어서

            /*  해쉬 데이터를 레퍼런스에 set --- > addOnCompleteListener or addOnFailureListener 제어 */
            documentReference.set(hashMap)
                    .addOnCompleteListener(task -> {
                        /* Complete 일때 onBackPressed() */
                            Toast.makeText(AddNoteActivity.this , "업로드 성공" , Toast.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            finish();

                        }).addOnFailureListener(e -> {
                            Toast.makeText(AddNoteActivity.this , "업로드 실패" , Toast.LENGTH_SHORT).show();
                            Log.d(TAG , e.getMessage());
                            progressBarSave.setVisibility(View.GONE);
                        });


        });
    }

    /* onCreateOptionsMenu에서 close부분 제어  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.close){
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
package com.example.onenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = "EditNoteActivity" ;
    Intent intent;
    EditText editNoteTitle , editNoteContent;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteContent = findViewById(R.id.editNoteContent);
        progressBar = findViewById(R.id.progressBar2);

        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        String noteTitle = intent.getStringExtra("title");
        String noteContent = intent.getStringExtra("content");
        String noteId = intent.getStringExtra("noteId"); //스냅샷 고유 id
        Log.d(TAG, "onCreate: " +noteTitle + noteContent + noteId );

        editNoteTitle.setText(noteTitle); //기존값 세팅
        editNoteContent.setText(noteContent);

        FloatingActionButton fab = findViewById(R.id.saveEditedNote);
        fab.setOnClickListener(v -> {

            String nTitle = editNoteTitle.getText().toString();
            String nContent = editNoteContent.getText().toString();

            if(nTitle.isEmpty() || nContent.isEmpty()){
                Toast.makeText(EditNoteActivity.this, "빈 텍스트는 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.VISIBLE);

            DocumentReference documentReference = firebaseFirestore
                    .collection("notes")
                    .document(user.getUid())
                    .collection("myNotes")
                    .document(noteId);

            /*  DocumentReference DB collection ,notes로 document() 진행 */
            Map<String , Object> hashMap = new HashMap<>(); /*  Hash로 nTitle , nContent 를 documentReference.set(hashMap)  */
            hashMap.put("title" , nTitle);
            hashMap.put("content" , nContent);

            /*  이쪽에서는 set 이 아니라 update를 진행해야한다
            : documentReference.update --- > addOnCompleteListener or addOnFailureListener 제어 */
            documentReference.update(hashMap)
                    .addOnCompleteListener(task -> {
                        /* HashMap - > documentReference update후 startActivity */
                        Toast.makeText(EditNoteActivity.this , "수정 완료" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditNoteActivity.this , MainActivity.class));
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EditNoteActivity.this , "업로드 실패" , Toast.LENGTH_SHORT).show();
                        Log.d(TAG , e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    });



        });

    }
}
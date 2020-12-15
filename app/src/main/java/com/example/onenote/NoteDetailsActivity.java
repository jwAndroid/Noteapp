package com.example.onenote;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class NoteDetailsActivity extends AppCompatActivity {

    //Intent 전역생성
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        /* https://charactermail.tistory.com/134 */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        intent = getIntent();

        TextView content = findViewById(R.id.noteDetailsContent);
        TextView title = findViewById(R.id.noteDetailsTitle);
        /* 스크롤 가능한 textView */
        content.setMovementMethod(new ScrollingMovementMethod());

        /* NoteListAdapter에서의 getIntent */
        String intentContent = intent.getStringExtra("content");
        String intentTitle = intent.getStringExtra("title");
        String intentNoteId = intent.getStringExtra("noteId");
        int intentColorCode = intent.getIntExtra("colorCode",0);

        /* 이쪽에서의 setText */
        content.setText(intentContent);
        title.setText(intentTitle);
        /* getIntent color code */
        content.setBackgroundColor(getResources().getColor(intentColorCode,null));

        /* FloatingActionButton setting */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteDetailsActivity.this , EditNoteActivity.class);
                intent.putExtra("title" ,intentTitle);
                intent.putExtra("content" ,intentContent);
                intent.putExtra("noteId" ,intentNoteId);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
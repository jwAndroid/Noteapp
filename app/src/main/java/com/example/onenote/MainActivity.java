package com.example.onenote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onenote.Adapter.NoteListAdapter;
import com.example.onenote.Auth.LoginActivity;
import com.example.onenote.Auth.RegisterActivity;
import com.example.onenote.Model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity" ;
    //DrawerLayout 전역setting
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;

    NoteListAdapter adapter;
    RecyclerView noteLists;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirestoreRecyclerAdapter<Note , NoteViewHolder> firestoreRecyclerNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText searchBox = findViewById(R.id.serchBox);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //TODO : GET USER.UID(); --> 유저의 객체마다 유아이디를 따로 변수값으로
        //TODO : 저장해서 쿼리 document에 장착해주기 --> 회원의 NOTE document 생성

        noteLists = findViewById(R.id.noteList);

        /* 중요 : User 개인의 uid에 접근해서 myNotes 데이터 접근 Query 후에 options  /// Query > user uid > myNotes > ... */
        Query query = firebaseFirestore.collection("notes")
                .document(user.getUid())
                .collection("myNotes")
                .orderBy("title" , Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query , Note.class)
                .build();

        firestoreRecyclerNoteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {

                String noteSnapshotId = firestoreRecyclerNoteAdapter.getSnapshots().getSnapshot(position).getId();
                String intentTitle = note.getTitle();
                String intentContent = note.getContent();
                final int colorCode = getRandomColor();

                holder.noteTitle.setText(intentTitle);
                holder.noteContent.setText(intentContent);
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(colorCode , null));

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this , NoteDetailsActivity.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("colorCode",colorCode);
                        intent.putExtra("noteId" , noteSnapshotId);
                        startActivity(intent);
                    }
                });

                /* 중요 : holder.view 의대한 값으로 findviewById 진행 후 menuIcon 클릭이벤트 정의  */
                ImageView menuIcon = holder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View clickView) {
                        /* PopupMenu 객체생성후 클릭이벤트 뷰의대한getContext,clickView의 파라메터
                           add를 통해 EditNote 과 Delete PopupMenu 생성후 각부분 제어 */
                        PopupMenu menu = new PopupMenu(clickView.getContext() , clickView);
                        menu.setGravity(Gravity.END); // setGravity END쪽으로
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                           /* EditNote --> EditNoteActivity 로 intent 하면서 세가지 data putExtra 후EditNoteActivity 쪽 그대로 동일하게 실행  */
                                Intent intent = new Intent(MainActivity.this , EditNoteActivity.class);
                                intent.putExtra("title" ,intentTitle);
                                intent.putExtra("content" ,intentContent);
                                intent.putExtra("noteId" ,noteSnapshotId);
                                startActivity(intent);

                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                /* collection "notes"쪽에서 --> document(noteId)쪽의 delete()후 성공or실패 제어 */
                                DocumentReference documentReference = firebaseFirestore
                                        .collection("notes")
                                        .document(user.getUid())
                                        .collection("myNotes")
                                        .document(noteSnapshotId);

                                documentReference.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MainActivity.this,
                                                        intentTitle+ ":" + "Delete!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this ,
                                                        "OnFailure"+e.getMessage() ,
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                return false;
                            }
                        });

                        menu.show();

                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent , false);
                return new NoteViewHolder(view);
            }
        };/* .................... firestoreRecyclerNoteAdapter .................... */


           /*
             TODO 1 : 어댑터 view click -- > intent note activity --> editText data insert ! && save && firebase server save --> 리사이클러뷰 셋
             TODO 2 : RANDOM으로 리사이클러뷰 아이템 BACKGROUND COLOR SETTING
             TODO 3 : 테스트 LIST 삭제후 어댑터에 셋팅

             NoteListAdapter 어댑터 객체생성 파라메터에 context , test list , test list
             리사이클러뷰와 어댑터 세팅 레이아웃 매니저로 두가지 형태를 쓸수있다.
             첫째로 noteLists.setLayoutManager(new LinearLayoutManager(MainActivity.this));
             둘쨰로 StaggeredGridLayoutManager
                  */

        //Drawer setting
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open , R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(firestoreRecyclerNoteAdapter);

        /* isAnonymous 와 real 일떄의 유저profile data set  */
        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if(user.isAnonymous()){
            userEmail.setVisibility(View.GONE); // 당연히 GONE 처리를 진행
            username.setText("임시계정");

        }else {
            userEmail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }

        /* Drawer -> addNote -> addNoteActivity를 굳이 거치지않아도 동작되게 처리 */
        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  case R.id.addNote 와 동일하게 intent  */
                startActivity(new Intent(MainActivity.this , AddNoteActivity.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG , "SearchBox afterTextChanged : " + s.toString());
                Query query;
                if (s.toString().isEmpty()){
                    query = firebaseFirestore.collection("notes")
                            .document(user.getUid())
                            .collection("myNotes")
                            .orderBy("title" , Query.Direction.DESCENDING);

                }else{
                    query = firebaseFirestore.collection("notes")
                            .document(user.getUid())
                            .collection("myNotes")
                            .whereEqualTo("title" , s.toString().toLowerCase().trim())
                            .orderBy("title" , Query.Direction.DESCENDING);

                }

                FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query , Note.class)
                        .build();
                firestoreRecyclerNoteAdapter.updateOptions(options);

            }
        });

    }// --------------------onCreate -------------------------

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    /* NavigationItemSelected 리스너 item.getItemId() 로써 nav_menu 의 id를 get */

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){

            case R.id.addNote :
                startActivity(new Intent(MainActivity.this , AddNoteActivity.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;

            case R.id.login :
                if(user.isAnonymous()){
                    startActivity(new Intent(MainActivity.this , LoginActivity.class));
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }else {
                    Toast.makeText(this, "이미 같은계정으로 로그인되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.register :
                if(user.isAnonymous()){
                    startActivity(new Intent(MainActivity.this , RegisterActivity.class));
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }else {
                    Toast.makeText(this, "이미 같은계정으로 로그인되어있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.logout :
                if (!user.isAnonymous()){
                    checkUser();
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    finish();
                    Log.d(TAG , "signOut()");
                }else{
                    Toast.makeText(MainActivity.this , "임시 계정은 로그아웃 할수없습니다!" , Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rating :
                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://velog.io/@cjd9408er"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up , R.anim.slide_down);
                break;
                /*- 닫을 때는 반대로 해준다.
                finish();
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                출처: https://jhshjs.tistory.com/32 [독학하는 1인 개발자]
                */

            case R.id.shareapp :
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://velog.io/@cjd9408er");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                overridePendingTransition(R.anim.slide_up , R.anim.slide_down);
                break;

            default:
                Toast.makeText(MainActivity.this , "TEST! " , Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        // if user is isAnonymous or not
        if(user.isAnonymous()){
            displayAlert();
        }else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext() , IntroActivity.class));
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("안내문")
                .setMessage("임시계정으로 로그인 되어있습니다. 로그아웃시, 임시 노트가 삭제됩니다.")
                .setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                        finish();
                    }
                }).setNegativeButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ToDO: delete all the notes created by the Anon user
                        // TODO: delete the anon user

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),IntroActivity.class));
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            }
                        });
                    }
                });

        warning.show();
    }

    //onCreateOptionsMenu ( @menu / option_menu ) 옵션아이템 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu , menu); // R.menu.option_menu 를 inflater.inflate
        return super.onCreateOptionsMenu(menu);
    }

    //onOptionsItemSelected 생성한 settings 의 클릭이벤트 생성
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings){
            Toast.makeText(MainActivity.this , "Option TEST! " , Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private int getRandomColor() {
        /* colors 리소스 data를 List<Integer> colorList 에 add해주고 , Random random = new Random(); 생성한후에
           random.nextInt(colorList.size()); 리스트사이즈만큼의 int number 저장한후에 colorList.get(number); 로 리턴
           즉 , 리스트의 랜덤한 값 리턴 -> 랜덤한 color를 리턴할 수 있다
            TODO : XmlPullParser */

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.black);
        colorList.add(R.color.colorPrimary);
        colorList.add(R.color.colorPrimaryDark);
        colorList.add(R.color.lightGreen);
        colorList.add(R.color.lightPurple);
        colorList.add(R.color.greenlight);

        Random random = new Random();
        int number = random.nextInt(colorList.size());
        return colorList.get(number);
    }


    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerNoteAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreRecyclerNoteAdapter != null) {
            firestoreRecyclerNoteAdapter.stopListening();
        }
    }

}
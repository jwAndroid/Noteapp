package com.example.onenote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onenote.NoteDetailsActivity;
import com.example.onenote.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
리사이클러뷰 아이템 : note_view_layout

*/

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    Context context;
    List<String> titles;
    List<String> content;
    /*
    어차피 data는 String으로 들어가기 떄문에 List를 String으로 잡아준다.
    */

    public NoteListAdapter() { }

    //컨스트럭터 두 list받아서 생성
    public NoteListAdapter(Context context, List<String> title , List<String> content){
        this.context = context;
        this.titles = title;
        this.content = content;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent , false);
        /* parent.getContext()  == context */
        return new ViewHolder(view);
    }

    //minimal sdk 23
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        /*holder의 noteTitle , noteContent 를 각 List /titles,content 의 position의 값을 get  */
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));
        final int colorCode = getRandomColor();
        /* holder의 mCardView 에 holder.view.getResources()
                .getColor() holder의 값으로 들어가야함. 그후에 getRandomColor() 를 세팅한다.
                그리고 만약 밑의 홀더에서 랜덤클래스를 사용하고 int colorCode = getRandomColor();
                이쪽에서도 랜덤클래스를 사용하면 2번의 랜덤을 거치게 되기떄문에 하나의 랜덤으로 통일시켜준다.
                 */
        holder.mCardView.setCardBackgroundColor(holder.view.getResources()
                .getColor(colorCode , null)
        );

        /*holder의 view의 대한 클릭이벤트 테스트*/
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , NoteDetailsActivity.class);
                /* NoteDetailsActivity 로 Intent 할떄 3가지를 putExtra 한다.
                   당연히 List의 내용 , 제목 , 그리고 컬러를 넘긴다.  */

                intent.putExtra("title",titles.get(position));
                intent.putExtra("content",content.get(position));
                intent.putExtra("colorCode",colorCode);
                context.startActivity(intent);

            }
        });

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
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        /* 아이템의 각 view 잡아주기*/

        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;

        }
    }


}

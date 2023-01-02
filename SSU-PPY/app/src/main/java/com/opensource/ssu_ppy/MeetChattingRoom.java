package com.opensource.ssu_ppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class MeetChattingRoom extends AppCompatActivity {
    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    FirestoreRecyclerOptions<Chat> options;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    androidx.appcompat.widget.Toolbar toolbar;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    MeetChatAdapter adapter;
    Button sendMessageButton;
    EditText meetChatContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_chatting_room);
        Intent intent = getIntent();
        String roomNum = intent.getStringExtra("roomNum");

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("미팅방");
        toolbar.setBackgroundColor(Color.parseColor("#E6CCF1"));
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        sendMessageButton = (Button) findViewById(R.id.meetSendMessage);
        meetChatContent = (EditText) findViewById(R.id.meetChatContent);
        recyclerView = (RecyclerView) findViewById(R.id.meet_chatting_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Query query = db.collection("meeting").document(roomNum)
                        .collection("chatContents").orderBy("timestamp",Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();

        adapter = new MeetChatAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        recyclerView.setAdapter(adapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignInfoToChat(roomNum);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public void assignInfoToChat(String roomNum) {
        System.out.println(user.getUid());
        db.collection("user").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel temp = document.toObject(UserModel.class);
                        Date date = new Date();
                        boolean is_man = temp.isIs_man();
                        String gender;
                        if(is_man) gender="남자";
                        else gender="여자";
                        Chat chat = new Chat(gender,meetChatContent.getText().toString(),user.getUid(),date);
                        meetChatContent.setText(null);
                        db.collection("meeting").document(roomNum)
                                .collection("chatContents").document(date.toString()+temp.getName()).set(chat);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
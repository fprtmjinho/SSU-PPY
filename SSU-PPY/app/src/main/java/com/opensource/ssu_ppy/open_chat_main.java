package com.opensource.ssu_ppy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class open_chat_main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    OpenChatAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<OpenChatItem> openChatItems;

    androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat_main);

        db = FirebaseFirestore.getInstance();
        final Button createOpChatButton = (Button) findViewById(R.id.create_opchat_button);
        final Button searchOpChatButton = (Button) findViewById(R.id.switch_search_opchat_button);
        openChatItems = new ArrayList<OpenChatItem>();

        recyclerView = (RecyclerView) findViewById(R.id.show_open_chat_recylerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.white_line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new OpenChatAdapter(getApplicationContext());
        bringMyRoom(adapter,openChatItems);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setSideNavBar();
        getHeaderInfo();

        adapter.setOnItemClickListener(new OpenChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OpenChatAdapter.ViewHolder holder, View view, int position) {
                OpenChatItem item = adapter.getItem(position);
                Toast.makeText(getApplicationContext(),"아이템 선택됨 : " + item.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), OpenChattingRoom.class);
                intent.putExtra("item",item);
                startActivity(intent);
            }
        });

        createOpChatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showAddChatDlg(adapter,openChatItems);
                adapter.notifyDataSetChanged();
            }
        });

        searchOpChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchOpenChatting.class);
                finish();
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new OpenChatAdapter.OnItemLongClickListener(){
            @Override
            public void onItemLongClick(OpenChatAdapter.ViewHolder holder, View view, int position) {
                showRoomInfoDlg(adapter, position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void showAddChatDlg(OpenChatAdapter adapter, ArrayList<OpenChatItem> openChatItems) {
        final LinearLayout linear = (LinearLayout) View.inflate(open_chat_main.this, R.layout.opchat_create_dialog, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(open_chat_main.this);
        dlg.setTitle("오픈채팅방 생성"); //제목

        if(linear.getParent() != null) ((ViewGroup) linear.getParent()).removeView(linear); // 다이얼로그 여러번 생성시 중복된 뷰그룹 들어가 발생하는 에러처리 부분
        dlg.setView(linear);

        dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                //토스트 메시지
                EditText room_create= (EditText)linear.findViewById(R.id.room_create);
                String room_name= room_create.getText().toString();
                if(room_name.equals("")) { Toast.makeText(open_chat_main.this, "방 이름을 입력하세요", Toast.LENGTH_SHORT).show(); return; }
                RadioGroup room_set= (RadioGroup)linear.findViewById(R.id.room_set);
                int checkedId = room_set.getCheckedRadioButtonId();
                RadioButton room_create_hobby= (RadioButton)room_set.findViewById(checkedId);
                String room_hobby;
                try {
                    room_hobby = room_create_hobby.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(open_chat_main.this, "방 분류를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                OpenChatItem item  = new OpenChatItem();
                item.setName(room_name);
                item.setHobby(room_hobby);
                item.addUidList(user.getUid());

                /*
                마지막 document 번호의 다음 값을 document id값으로 지정하여 오픈채팅방 정보 저장
                */
                db.collection("openChat")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                long idx=0;
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        idx = Long.parseLong(document.getId());
                                        System.out.println(document.getId());
                                    }
                                } else {
                                }
                                idx++;
                                item.setRoomNum(String.valueOf(idx));
                                db.collection("openChat").document(String.valueOf(idx)).set(item);
                                openChatItems.add(item);
                                adapter.addItem(item);
                                adapter.notifyDataSetChanged();
                                addUserOpChat(idx);
                            }
                        });
                Toast.makeText(open_chat_main.this, room_name, Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dlg.show();
    }

    public void addUserOpChat(long idx) {
        DocumentReference ref =  db.collection("userOpenChat").document(user.getUid());
        //ref.update("myroom", FieldValue.arrayRemove());
        ref.update("myRoom", FieldValue.arrayUnion(String.valueOf(idx)));
    }

    public void bringMyRoom(OpenChatAdapter adapter, ArrayList<OpenChatItem> openChatItems) {
        db.collection("userOpenChat").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            ArrayList<String> myRoom = (ArrayList<String>) document.get("myRoom");
                            if(myRoom.size()!=0) {
                                getChatData(adapter, openChatItems, myRoom);
                            }
                        }
                    }
                });
    }

    public void getChatData(OpenChatAdapter adapter, ArrayList<OpenChatItem> openChatItems, ArrayList<String> myRoom) {
        db.collection("openChat")
                .whereIn("roomNum",myRoom)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                OpenChatItem item = document.toObject(OpenChatItem.class);
                                openChatItems.add(item);
                                adapter.addItem(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    public void showRoomInfoDlg(OpenChatAdapter adapter, int position) {
        final LinearLayout linear = (LinearLayout) View.inflate(open_chat_main.this, R.layout.room_info_dialog, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(open_chat_main.this);
        String Title = (String) adapter.getItem(position).getName();
        dlg.setTitle(Title); //제목

        TextView hobby = (TextView) linear.findViewById(R.id.roomHobbyText);

        String hobbyText = (String) adapter.getItem(position).getHobby();

        hobby.setText(hobbyText);

        if (linear.getParent() != null)
            ((ViewGroup) linear.getParent()).removeView(linear); // 다이얼로그 여러번 생성시 중복된 뷰그룹 들어가 발생하는 에러처리 부분
        dlg.setView(linear);

        System.out.println(adapter.getItem(position).getName());
        System.out.println(adapter.getItem(position).getHobby());
        dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //뷰 클릭시 따온 정보로 파이어스토어에서 삭제하기
                FirebaseFirestore myDb = FirebaseFirestore.getInstance();
                db.collection("userOpenChat").document(user.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    ArrayList<String> roomList = new ArrayList<>();
                                    roomList = (ArrayList<String>) document.getData().get("myRoom");
                                    long idx = 0;
                                    for (String room : roomList){
                                        idx++;
                                        if (room.equals(adapter.getItem(position).getRoomNum())){
                                            Map<String,Object> updates = new HashMap<>();
                                            updates.put("uidList", FieldValue.delete());

                                            DocumentReference ref= myDb.collection("userOpenChat").document(user.getUid());
                                            ref.update("myRoom", FieldValue.arrayRemove(adapter.getItem(position).getRoomNum()));
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                db.collection("openChat").document(adapter.getItem(position).getRoomNum())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    ArrayList<String> uidList = new ArrayList<>();
                                    uidList = (ArrayList<String>) document.getData().get("uidList");
                                    long idx = 0;
                                    for (String uid : uidList){
                                        idx++;
                                        if (uid.equals(user.getUid())){
                                            Map<String,Object> updates = new HashMap<>();
                                            updates.put("uidList", FieldValue.delete());

                                            DocumentReference ref= myDb.collection("openChat").document(adapter.getItem(position).getRoomNum());
                                            ref.update("uidList", FieldValue.arrayRemove(user.getUid()));
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

            }
        });
        dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dlg.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //추가된 소스, ToolBar에 menu.xml을 인플레이트함
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.basebar, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back) {
            onBackPressed();
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                Toast.makeText(getApplicationContext(),"로그아웃 하였습니다",Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.menu_item2:
                Intent intent = new Intent(getApplicationContext(),ReviseProfile.class);
                startActivity(intent);
                break;
            case R.id.menu_item3:
                show_register_student_dlg();
                break;
        }
        return false;
    }

    public void setSideNavBar() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText("오픈채팅");
        toolbar.setBackgroundColor(Color.parseColor("#BFD0FB"));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_dehaze_24);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setHeaderView(UserModel temp) {
        headerView = navigationView.getHeaderView(0);
        View headerBody = headerView.findViewById(R.id.header_body);
        TextView headerName = (TextView) headerView.findViewById(R.id.header_name);
        TextView headerEmail = (TextView) headerView.findViewById(R.id.header_email);
        headerName.setText(temp.getName());
        headerEmail.setText(user.getEmail());
        if(temp.isIs_man()) headerBody.setBackgroundColor(Color.parseColor("#BFD0FB"));
        else headerBody.setBackgroundColor(Color.parseColor("#E6CCF1"));
    }

    public void getHeaderInfo() {
        System.out.println(user.getUid());
        db.collection("user").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel temp = document.toObject(UserModel.class);
                        setHeaderView(temp);
                    }
                }
            }
        });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void show_register_student_dlg() {
        AlertDialog.Builder deleteMemberDlg = new AlertDialog.Builder(this);
        deleteMemberDlg.setTitle("정말 탈퇴 하시겠습니까?");
        deleteMemberDlg.setIcon(R.drawable.ic_round_dehaze_24);
        deleteMemberDlg.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
            }
        });
        deleteMemberDlg.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteMemberDlg.show();
    }

    private void deleteMember() {
        Toast.makeText(getApplicationContext(),"탈퇴 하였습니다",Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        db.collection("user").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        mAuth.getCurrentUser().delete();

        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),SelectMenu.class);
        finish();
        startActivity(intent);
        super.onBackPressed();
    }
}

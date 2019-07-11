package com.example.mysnsproject.chatting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysnsproject.Commnet.CommentActivity;
import com.example.mysnsproject.Commnet.CommentData;
import com.example.mysnsproject.Commnet.CommentEditActivity;
import com.example.mysnsproject.FriendList.FriendListActivity;
import com.example.mysnsproject.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private DataInputStream socket_in;
    private DataOutputStream socket_out = null;
    private EditText input;
    private Button button;
    private TextView output;
    private String data;
    //String name = "name";
    private ArrayList<Chatdata> chatlistArraylist = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerview;
    private String chatid;
    private String chatotherperson;
    public ArrayList<String> Check1 = new ArrayList<>();
    private Context context;
    private String data1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        chatid = intent.getStringExtra("chatid");//로그인한 유저의 아이디 받아오기
        chatotherperson = intent.getStringExtra("name");
        Log.d("startactvity_login_id", chatid);
        Log.d("chatotherpersonchn", chatotherperson);



        input = (EditText) findViewById(R.id.input);
        button = (Button) findViewById(R.id.button);
        a();

        //friendlistRecyclerView가 무엇인지 설정하기
        chatRecyclerview = (RecyclerView) findViewById(R.id.chat_recyclerview);

        //friendlistRecyclerView의 레이아웃 매니저 설정하기
        chatRecyclerview.setLayoutManager(new LinearLayoutManager(this));


        chatAdapter = new ChatAdapter(this, chatlistArraylist);
        chatRecyclerview.setAdapter(chatAdapter);

        SharedPreferences sf = getSharedPreferences("1111",MODE_PRIVATE);

        String text1 = sf.getString(chatotherperson,"");
        data1 = text1;

        Log.d("쉐어드 저장된값", text1);

        try{
            JSONArray jarray = new JSONArray(text1);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String id = jObject.getString("id");
                Log.d("해제한ID", id);
                String contents = jObject.getString("content");
                Log.d("해제한contents", contents);

                //chatdata 데이터 값을 생성해주는 부분
                final Chatdata chatData = new Chatdata();
                //데이터들을 저장하는 부분
                chatData.setChat(contents);
                chatData.setChatuser_id(id);
                chatlistArraylist.add(chatData);
                Log.d("어레이에 추가된 데이터 아이디",chatlistArraylist.get(i).getChatuser_id());
                Log.d("어레이에 추가된 데이터 채팅",chatlistArraylist.get(i).getChat());
                chatAdapter.notifyDataSetChanged();
            }
        }
        catch (JSONException e){

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = input.getText().toString();

                if (data != null) {//입력한값이 있을때
                    JSONObject bookInfo = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    try {
                        bookInfo.put("content", data);
                        bookInfo.put("id", chatid);
                        jsonArray.put(bookInfo);
                        send(jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("json", "생성한 json : " + e);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("퍼즈", "퍼즈상태");
        //데이터를 제이슨 어레이로 변형해서 저장시키기
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < chatlistArraylist.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", chatlistArraylist.get(i).getChatuser_id());
                jsonObject.put("content", chatlistArraylist.get(i).getChat());
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.d("퍼즈 제이슨 어레이", jsonArray.toString());
        //쉐이드에 저장하기
        SharedPreferences prefsEditor = getSharedPreferences("1111", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsEditor.edit();
        editor.putString(chatotherperson, jsonArray.toString());
        editor.commit();

    }

    public void send(final String a) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket_out.writeUTF(a);//압력한 값이 들어간다.
                    Log.d("checkvalue", a);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void a() {
        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("192.168.0.15", 9000);//자신의 아이피 주소와 포트번호 입력
                    socket_out = new DataOutputStream(socket.getOutputStream());
                    socket_in = new DataInputStream(socket.getInputStream());
                    System.out.println("소켓 생성");
                    Log.d("데이터값", "" + socket_in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        data = socket_in.readUTF();
                        Log.d("제이슨", data);

                        JSONArray jarray = new JSONArray(data);   // JSONArray 생성
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                            String id = jObject.getString("id");
                            Log.d("ID", id);
                            String contents = jObject.getString("content");
                            Log.d("contents", contents);

                            //chatdata 데이터 값을 생성해주는 부분
                            final Chatdata chatData = new Chatdata();
                            //데이터들을 저장하는 부분
                            chatData.setChat(contents);
                            chatData.setChatuser_id(id);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatlistArraylist.add(chatData);
                                    chatAdapter.notifyDataSetChanged();
                                }
                            });

                            Log.d("제이슨", jarray.toString());
                            Log.d("제이슨1", data);
                            Log.d("asdfasdfasdf", "" + chatData.getChat());

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.start();
    }


    //채팅 어댑터 클래스
    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.CustomViewHolder> {

        private ArrayList<Chatdata> chatListDataArrayList = null;
        private Activity context = null;
        private Object LoginID;

        public ChatAdapter(Activity context, ArrayList<Chatdata> list) {
            this.context = context;
            this.chatListDataArrayList = list;
        }

        //뷰홀더 클래스 변수선언 및 변수가 무엇을 뜻하는지 정함
        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView chatname;
            protected TextView chat;
            protected ImageView user_image;
            protected LinearLayout linearLayoutm;
            protected LinearLayout linearLayout_destination;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                this.user_image = (ImageView) itemView.findViewById(R.id.chat_item_image);
                this.chatname = (TextView) itemView.findViewById(R.id.chat_item_list_name);
                this.chat = (TextView) itemView.findViewById(R.id.chat_item_list_chat);
                this.linearLayoutm = (LinearLayout) itemView.findViewById(R.id.chat_item_linearlayout);
                this.linearLayout_destination = (LinearLayout) itemView.findViewById(R.id.linearLayout_destination);
            }
        }

        @NonNull
        @Override
        public ChatAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, null);
            ChatAdapter.CustomViewHolder viewHolder = new ChatAdapter.CustomViewHolder(view);
            return viewHolder;
        }

        //데이터들이 보여지는 부분
        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.CustomViewHolder customViewHolder, final int i) {
            //자신이 보냈을때
            if (chatListDataArrayList.get(i).getChatuser_id().equals(chatid)) {
                Log.d("!!!!!!!!!!!!!", "11111111111");
                customViewHolder.chat.setText(chatListDataArrayList.get(i).getChat());
                customViewHolder.chat.setBackgroundResource(R.drawable.rightbubble);
                customViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                customViewHolder.chat.setTextSize(10);
                customViewHolder.linearLayoutm.setGravity(Gravity.RIGHT);
                //상대방이 보냈을때
            } else {
                customViewHolder.chat.setText(chatListDataArrayList.get(i).getChat());
                customViewHolder.chat.setBackgroundResource(R.drawable.leftbubble);
                customViewHolder.chatname.setText(chatListDataArrayList.get(i).getChatuser_id());
                customViewHolder.user_image.setImageBitmap(new FriendListActivity.ImageRoader().getBitmapImg( chatListDataArrayList.get(i).getChatuser_id()+".jpg"));

            }
            Log.d("유저아이디", chatid);
            Log.d("유저아이디1", chatListDataArrayList.get(i).getChatuser_id());

        }

        @Override
        public int getItemCount() {
            return (null != chatListDataArrayList ? chatListDataArrayList.size() : 0);
        }
    }
}

package com.example.mysnsproject.FriendList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mysnsproject.PostListActivity.startActivity;
import com.example.mysnsproject.R;
import com.example.mysnsproject.chatting.ChatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FriendprofileActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "54.180.24.94";
    private ImageView friendprofile;
    private TextView friendid;
    private TextView friendname;
    private ArrayList<FriendListData> friendListDataArrayList = null;
    private String TAG = "FriendprofileActivity";
    private Button frienddelete;
    private FriendListActivity.FriendListAdapter friendlistAdapter1;
    private Button friendchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendprofile);
        friendid = (TextView) findViewById(R.id.friendprofile_textview_ID1);
        friendname = (TextView) findViewById(R.id.friendprofile_textview_friend_name1);
        friendprofile = (ImageView) findViewById(R.id.friendprofile_image);
        frienddelete = (Button) findViewById(R.id.friendprofile_delete_btn);
        friendchat = (Button) findViewById(R.id.friendprofile_change_btn);

        Intent intent = getIntent();
      //ArrayList<String> temp = intent.getStringArrayListExtra("어레이");
        //Log.d("test", "temp" + temp);
        final String temp1 = intent.getStringExtra("4");//인덱스값 받아오는 부분
        Log.d("고유의 idx값", temp1);
        final String temp2 = intent.getStringExtra("1");//친구 아이디 받아오는 부분
        Log.d("친구아이디", temp2);
        final String temp3 = intent.getStringExtra("2");//로그인한 유저 아이디 받아오는 부분
        Log.d("로그인한 유저 아이디", temp3);
        String temp4 = intent.getStringExtra("3");//친구 이름 받아오는 부분
        Log.d("친구이름", temp4);

        //친구프로필사진 가져오는 부분
        friendprofile.setImageBitmap(new FriendprofileActivity.friendprofileimage().
                getBitmapImg(temp2 + ".jpg"));//파일이름
        friendid.setText(temp2);
        friendname.setText(temp4);

        //친구삭제버튼을 눌렀을때 생기는 일
        frienddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Frienddelete frienddeletestart = new Frienddelete();
                frienddeletestart.execute("http://" + IP_ADDRESS + "/frienddelete.php", temp1);
                finish();
            }
        });

        //친구랑 대화하기
        friendchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatmove = new Intent(getApplicationContext(), ChatActivity.class);
                chatmove.putExtra("chatid",temp3);
                chatmove.putExtra("name",temp2);
                startActivity(chatmove);

            }
        });
    }

    //이미지 불러오는 부분
    private static class friendprofileimage {
        private final String serverUrl = "http://54.180.24.94/";

        public friendprofileimage() {
            new FriendListActivity.ThreadPolicy();
        }

        public Bitmap getBitmapImg(String imgStr) {
            Bitmap bitmapImg = null;
            try {
                URL url = new URL(serverUrl +
                        URLEncoder.encode(imgStr, "utf-8"));
                // Character is converted to 'UTF-8' to prevent broken
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmapImg = BitmapFactory.decodeStream(is);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return bitmapImg;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static class ThreadPolicy {

        //쓰레드를 사용해서 네트워크를 원할하게 해줌
        public ThreadPolicy() {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //php에 삭제할 데이터 보내는 부분
    private class Frienddelete extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FriendprofileActivity.this,
                    "Please Wait", null, true, true);
        }
        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            // ChangeName.setText(result);
            Log.d("MyProfileActivityResult",result);
        }


        @Override
        protected String doInBackground(String... params) {
            String idx = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "idx=" + idx;

            Log.d("idx",idx);

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }
}

package com.example.mysnsproject.FriendList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysnsproject.ProfileActivity;
import com.example.mysnsproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class FriendListActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "FriendListPhpTest";
    private ArrayList <FriendListData> friendlistArraylist;
    private FriendListAdapter friendlistAdapter;
    private RecyclerView friendlistRecyclerView;
    private String friendlistJsonString;
    private String login_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        //인텐트로 받아온 값 가져오기
        Intent startactivitymove = getIntent();
        login_id = startactivitymove.getExtras().getString("friendlistmyid");
        Log.d("friendlistmyid", "" + login_id);

        //friendlistRecyclerView가 무엇인지 설정하기
        friendlistRecyclerView = (RecyclerView) findViewById(R.id.friend_list_recyclerview);

        //friendlistRecyclerView의 레이아웃 매니저 설정하기
        friendlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //friendlistArraylist의 배열 선언
        friendlistArraylist = new ArrayList<>();

        friendlistAdapter = new FriendListAdapter(this, friendlistArraylist);
        friendlistRecyclerView.setAdapter(friendlistAdapter);


        //friendlistArraylist데이터 초기화 해주기
        friendlistArraylist.clear();
        //friendlistArraylist 데이터 호출
        friendlistAdapter.notifyDataSetChanged();

        //GetFriendListData를 실행해서 친구 목록 가져오기
        GetFriendListData getFriendListData = new GetFriendListData();
        getFriendListData.execute("http://"+IP_ADDRESS+"/friend.php", login_id);


    }
    //생명주기를 이용해서 재실행 시키기
    @Override
    protected void onRestart() {
        super.onRestart();
        //이전에 데이터를 삭제하고 재실행 시키기
        friendlistArraylist.clear();
        friendlistAdapter.notifyDataSetChanged();
        GetFriendListData getFriendListData = new GetFriendListData();
        getFriendListData.execute("http://"+IP_ADDRESS+"/friend.php", login_id);
    }
    //친구 데이터를 불러오기
    private class GetFriendListData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(FriendListActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //결과값 로그에 출력시키기
            Log.d(TAG, "response - " + result);

            if (result == null){
               // Toast.makeText(FriendListActivity.this,result,Toast.LENGTH_SHORT).show();
            }
            else {
                //friendlistJsonString에 결과값 담기
                friendlistJsonString = result;
                //showResult 메소드 실행시키기
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String login_id = (String) params[1];
            String postParameters = "login_id=" + login_id;
            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
    //결과값을 출력시키게 해주는 메소드
    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_ID = "friendemail";
        String TAG_NAME = "friendname";
        String TAG_Number = "idx";
        //  String TAG_COUNTRY ="country";
        try {
            JSONObject jsonObject = new JSONObject(friendlistJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                //php에서 받아온 제이슨 데이터를 풀어주는 부분
                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String number = item.getString(TAG_Number);

                //Friendlistdata에 데이터 값을 생성해주는 부분
                FriendListData friendListData = new FriendListData() {
                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {

                    }
                };
                //데이터들을 저장하는 부분
                friendListData.setFriend_id(id);
                friendListData.setFriend_name(name);
                friendListData.setMy_id(login_id);
                friendListData.setIdx(number);
                Log.d("friendidx",""+number);
                friendlistArraylist.add(friendListData);
                friendlistAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    //이미지 불러오는 부분
    public static class ImageRoader {
        private final String serverUrl = "http://54.180.24.94/";
        public ImageRoader() {
            new ThreadPolicy();
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

    //친구리스트에 대한 리사이클러뷰의 어댑터
    class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.CustomViewHolder> {
        private ArrayList<String> mArrayList;
        private ArrayList<FriendListData> friendListDataArrayList = null;
        private Activity context = null;

        public FriendListAdapter(Activity context, ArrayList<FriendListData> list){
            this.context = context;
            this.friendListDataArrayList = list;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            protected TextView friend_id;
            protected TextView friend_name;
            protected ImageView friend_image;
            protected TextView friend_idx;
            protected LinearLayout friend_linearlayout;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                this.friend_id = (TextView) itemView.findViewById(R.id.friendlist_item_list_friendid);
                this.friend_name = (TextView) itemView.findViewById(R.id.friendlist_item_list_friendname);
                this.friend_image = (ImageView) itemView.findViewById(R.id.friendlist_item_list_imageview);
                this.friend_linearlayout = (LinearLayout) itemView.findViewById(R.id.friendlist_item_list_linearlayout);
                this.friend_idx = (TextView) itemView.findViewById(R.id.friendlist_item_list_friend_idx);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                //데이터를 넘기기위해 배열로 설정
                Log.d("클릭했을띠ㅐ 로그인한 유저 아이디", "" + login_id);
                Intent intent = new Intent(v.getContext(),FriendprofileActivity.class);
                intent.putExtra("1", friend_id.getText().toString());//친구아이디를 보내주기
                intent.putExtra("2", login_id);
                intent.putExtra("3", friend_name.getText().toString());
                intent.putExtra("4", friend_idx.getText().toString());
                intent.putExtra("어레이", mArrayList);
                context.startActivity(intent);
            }
        }
        @NonNull
        @Override
        public FriendListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friendist_item_list,null);
            FriendListAdapter.CustomViewHolder viewHolder = new FriendListAdapter.CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
            //친구 아이디가 보여지는 부분
            customViewHolder.friend_id.setText(friendListDataArrayList.get(i).getFriend_id());
            //친구 이름이 보여지는 부분
            customViewHolder.friend_name.setText(friendListDataArrayList.get(i).getFriend_name());
            //친구 사진이 보여지는 부분
            customViewHolder.friend_image.setImageBitmap(new FriendListActivity.ImageRoader().getBitmapImg(friendListDataArrayList.get(i).getFriend_id()+".jpg"));
            //친구 인덱스가 생성되는 부분
            customViewHolder.friend_idx.setText(friendListDataArrayList.get(i).getIdx());
        }

        //데이터들이 보여지는 부분
        @Override
        public int getItemCount() {
            return (null != friendListDataArrayList ? friendListDataArrayList.size() : 0);
        }
    }

}

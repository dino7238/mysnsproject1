package com.example.mysnsproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchFriend extends AppCompatActivity {

    private static String IP_ADDRESS = "54.180.24.94";
    private EditText SearchEmailEditText;
    private Button SearchEamilButton;
    private String TAG = "SearchFriend";
    private TextView textview_outputname;
    private TextView textview_outputid;
    private Button button_add;
    private String mJsonString;
    private String login_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        //스타트엑티비티 페이지에서 로그인 아이디 값 가져오기
        Intent startactivitymove = getIntent();
        login_id = startactivitymove.getExtras().getString("searchfriend");
        Log.d("searchfriend", "" + login_id);

        SearchEamilButton = (Button) findViewById(R.id.Search_email_BT);
        SearchEmailEditText = (EditText) findViewById(R.id.Search_email_ET);
        textview_outputname = (TextView) findViewById(R.id.SearchActiviy_textview_name);
        textview_outputid = (TextView) findViewById(R.id.SearchActiviy_textview_ID);
        button_add = (Button) findViewById(R.id.SearchActiviy_button_add);

        SearchEamilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SearchEmailEditText.getText().toString().equals(login_id)){
                    Toast.makeText(SearchFriend.this,"본인은 검색 할 수 없습니다.",Toast.LENGTH_SHORT).show();
                }else {
                    String Eamil = SearchEmailEditText.getText().toString();
                    searchfriend Searchfriend = new searchfriend();
                    Searchfriend.execute( "http://"+IP_ADDRESS+"/searchfriend.php", Eamil);
                    Log.d("SearchFriend_InputEamil", "" + Eamil);
                }
            }
        });

    }


    //친구 추가 하는 부분
    public class addfriend extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchFriend.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            Log.d("SearchFriendADDResult",result);
            //php서버에서 추가라는 값을 받을때 현재 엑티비티의 Email값이 메인 엑티비티로 보내지게 만드는 코드
            if(result.equals("이미 추가된 아이디입니다.")) {
                Toast.makeText(SearchFriend.this, "이미 추가된 아이디입니다.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(SearchFriend.this, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        //이부분은 다시 공부해야한다.
        @Override
        protected String doInBackground(String... params) {

            String myemail = (String) params[1];
            String friendemail = (String) params[2];
            String friendname = (String) params[3];
            //  String phonenumber = (String) params[4];
            String serverURL = (String) params[0];
            String postParameters = "myemail=" + myemail + "&friendemail=" + friendemail + "&friendname=" + friendname;

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

    //아이디를 검색하고 결과를 php에서 주고 받는 부분
    private class searchfriend extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SearchFriend.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result.equals("없는 아이디입니다.")){
                Toast.makeText(SearchFriend.this,"존재하지 않는 아이디입니다.",Toast.LENGTH_LONG).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        //php와 안드로이드 스튜디오 연결해주는 부분
        @Override
        protected String doInBackground(String... params) {

            String searchid = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "searchid=" + searchid;

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
    //제이슨으로 데이터 받아와 지는 부분
    public void showResult(){

        String TAG_JSON="webnautes";
        String TAG_ID = "email";
        String TAG_NAME = "name";
        //  String TAG_COUNTRY ="country";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                //아이디,이름 받아와지는 부분
                final String id = item.getString(TAG_ID);
                final String name = item.getString(TAG_NAME);
                textview_outputid.setText(id);
                textview_outputname.setText(name);

                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String myemail = login_id;
                        String friendemail = id;
                        String friendname = name;

                        Log.d("addbuttonmyemail", "" + myemail);
                        Log.d("addbuttonfriendemail", "" + friendemail);
                        addfriend ADDFRIEND = new addfriend();
                        ADDFRIEND.execute("http://" + IP_ADDRESS + "/addfriend.php", myemail, friendemail, friendname);

                    }
                });
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }


}




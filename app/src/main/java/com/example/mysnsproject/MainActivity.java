package com.example.mysnsproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysnsproject.PostListActivity.startActivity;
import com.example.mysnsproject.chatting.ChatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnRegist;
    private Button btnLogin;
    private EditText Email;
    private EditText Password;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //친구찾기 버튼

        Email = (EditText) findViewById(R.id.LoginEmail);
        Password =(EditText) findViewById(R.id.LoginPassword);

        btnRegist = (Button) findViewById(R.id.btnRegist);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Email.getText().toString();
                String password = Password.getText().toString();

                LoginID loginID = new LoginID();
                loginID.setLogin_id(name);

                if (Email.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                    Email.requestFocus();
                    return;
                }
                if (Password.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    Password.requestFocus();
                    return;
                }
                InsertData1 task = new InsertData1();
                task.execute("http://" + IP_ADDRESS + "/login.php", name, password);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setResult를 통해 받아온 요청번호, 상태, 데이터
        Log.d("RESULT", requestCode + "");
        Log.d("RESULT", resultCode + "");
        Log.d("RESULT", data + "");

        if(requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(MainActivity.this, "회원가입을 완료했습니다!", Toast.LENGTH_SHORT).show();
            Email.setText(data.getStringExtra("email"));
        }
    }

    class InsertData1 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String name = Email.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
            if(result.equals("로그인 성공")) {
                Intent startactivitymove = new Intent(getApplicationContext(), startActivity.class);
                startactivitymove.putExtra("name",name);

                new ChatActivity().a();

                startActivity(startactivitymove);

                Log.d("1111111111",name);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[1];
            String password = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "name=" + name + "&password=" + password;
            Log.d("doingbackground" + name, "doingbackground" + name +"password"+password);

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



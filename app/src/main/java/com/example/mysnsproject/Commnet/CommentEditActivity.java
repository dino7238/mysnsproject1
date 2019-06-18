package com.example.mysnsproject.Commnet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysnsproject.ApiClient;
import com.example.mysnsproject.ApiInterface;
import com.example.mysnsproject.ImageClass;
import com.example.mysnsproject.PosteditActivity;
import com.example.mysnsproject.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentEditActivity extends AppCompatActivity {
    private EditText commentedit;
    private Button editbtn;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "Commenteditphp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_edit);
        commentedit = (EditText) findViewById(R.id.commentedit_edittext_edit);
        editbtn = (Button) findViewById(R.id.commentedit_btn_btn);

        //인텐트로 값 가져오는 부분
        Intent intent = getIntent();
        final String commentidx = intent.getStringExtra("commenteditidx");//코멘트 번호 받아오기
        Log.d("commenteditidx", commentidx);

        final String commentcomment = intent.getStringExtra("commentcomment");//코멘트 내용 받아오기
        Log.d("commentcomment", commentcomment);

        commentedit.setText(commentcomment);

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editcomment editcomment = new editcomment();
                editcomment.execute("http://" + IP_ADDRESS + "/commentedit.php", commentidx, commentedit.getText().toString());
                finish();
            }
        });

    }
    //작성된 게시글을 mysql에 저장시키기 위한 부분
    public class editcomment extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CommentEditActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("PostEditActivityResult", result);
        }

        //이부분은 다시 공부해야한다.
        @Override
        protected String doInBackground(String... params) {

            String idx = (String) params[1];
            String contents = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "idx=" + idx + "&contents=" + contents;

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
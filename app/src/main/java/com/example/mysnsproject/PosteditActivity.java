package com.example.mysnsproject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysnsproject.PostListActivity.startActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosteditActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private ImageView edit_write_profile_image;
    private TextView edit_write_profile_id;
    private TextView eidt_write_profile_name;
    private EditText edit_post_EditText;
    private Button edit_postedit_btn;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "PostWriteActivity";
    private ImageView edit_selectimageview;
    private String AAAA = "B";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postedit);

        edit_write_profile_image = (ImageView) findViewById(R.id.postedit_imageview_writerimage);
        edit_write_profile_id = (TextView) findViewById(R.id.postedit_textview_writerid);
        edit_post_EditText = (EditText) findViewById(R.id.postedit_edittext);
        edit_postedit_btn = (Button) findViewById(R.id.postedit_button_add);
        edit_selectimageview = (ImageView) findViewById(R.id.postedit_button_imageselect);

        //인텐트로 값 가져오는 부분
        Intent intent = getIntent();
        final String temp1 = intent.getStringExtra("writeid");//글쓴이 아이디 받아오기
        Log.d("writeid", temp1);

        final String temp2 = intent.getStringExtra("postidx");//게시글 번호 받아오기
        Log.d("postidx", temp2);

        final String temp3 = intent.getStringExtra("postcontent");//게시글에 작성된 내용 받아오기
        Log.d("postcontent", temp3);

        //각각의 아이템에 값을 넣는부분
        edit_write_profile_image.setImageBitmap(new startActivity.startactivity_imageload().getBitmapImg(temp1 + ".jpg"));
        edit_write_profile_id.setText(temp1);
        edit_post_EditText.setText(temp3);
        edit_selectimageview.setImageBitmap(new startActivity.startactivity_imageload().getBitmapImg(temp2 + ".jpg"));

        edit_selectimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_selectimage();
                AAAA = "A";
            }
        });
        edit_postedit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editpost task12 = new editpost();
                task12.execute("http://" + IP_ADDRESS + "/postedit.php", temp2, edit_post_EditText.getText().toString());
                Toast.makeText(PosteditActivity.this, "게시글 수정 완료!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    //이미지 선택하게 만드는 메소드
    private void edit_selectimage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        Log.d("qqweq", "qqweq" + 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            Log.d("qqweq", "qqweq" + path);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                edit_selectimageview.setImageBitmap(bitmap);
                edit_selectimageview.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //작성된 게시글을 mysql에 저장시키기 위한 부분
    public class editpost extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PosteditActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("PostEditActivityResult", result);
            //사진부분 합치기(사진을 올릴려고할때 변수 AAAA값을 A가 아닐때는 그냥 올리고 AAAA값이 A일때는 사진도 같이 올리기)
            if (AAAA == "A") {
                String Image = imageToString();
                String Title = result;
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<ImageClass> call = apiInterface.uploadImage(Title, Image);

                Log.d("Image", "" + Image);
                Log.d("Title", "" + Title);

                call.enqueue(new Callback<ImageClass>() {
                    @Override
                    public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {

                        ImageClass imageClass = response.body();
                        Toast.makeText(PosteditActivity.this, "Server Response:" + imageClass.getResponse(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<ImageClass> call, Throwable t) {
                    }
                });
            }
        }

        private String imageToString() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgByte, Base64.DEFAULT);
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


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

public class PostwriteActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private String login_id;
    private ImageView write_profile_image;
    private TextView write_profile_id;
    private TextView write_profile_name;
    private EditText post_EditText;
    private Button postadd_btn;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "PostWriteActivity";
    private ImageView selectimageview;
    private String AAAA = "A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postwrite);
        AAAA = "B";

        Intent startactivitymove = getIntent();
        login_id = startactivitymove.getExtras().getString("postwrite");
        Log.d("postwrite", "" + login_id);

        write_profile_image = (ImageView) findViewById(R.id.postwrite_imageview_writerimage);
        write_profile_id = (TextView) findViewById(R.id.postwrite_textview_writerid);
        write_profile_name = (TextView) findViewById(R.id.postwrite_textview_writername);
        post_EditText = (EditText) findViewById(R.id.postwrite_edittext);
        postadd_btn = (Button) findViewById(R.id.postwrite_button_add);

        //프로필 사진 생성되는 부분
        write_profile_image.setImageBitmap(new PostwriteActivity.write_ImageRoader().
                getBitmapImg(login_id + ".jpg"));//파일이름

        //게시글 작성 화면에 진입시 작성자의 이름을 가져오는 부분
        Writername writername = new Writername();
        writername.execute("http://" + IP_ADDRESS + "/myprofileload.php", login_id);
        //게시글 작성 화면에 진입시 작성자의 아이디가 login_id로 출력되게 만듬
        write_profile_id.setText(login_id);
        //작성버튼을 누르면 mysql에 데이터를 보내기
        postadd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_EditText.getText().toString().length() == 0) {
                    Toast.makeText(PostwriteActivity.this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    addpost task12 = new addpost();
                    task12.execute("http://" + IP_ADDRESS + "/addpost.php", login_id, post_EditText.getText().toString());
                    Toast.makeText(PostwriteActivity.this, "게시글 작성 완료!", Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
        selectimageview = (ImageView) findViewById(R.id.postwrite_button_imageselect);
        selectimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage0();
                AAAA = "A";
            }
        });

    }

    //이미지 선택하게 만드는 메소드
    private void selectimage0() {
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
                selectimageview.setImageBitmap(bitmap);
                selectimageview.setVisibility(View.VISIBLE);
                //Img_title.setVisibility(View.VISIBLE);
                //BnChoose.setEnabled(false);
                // BnUpload.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // uploadImage();
        }
    }

    //이미지 불러오는 부분
    public class write_ImageRoader {
        private final String serverUrl = "http://54.180.24.94/";

        public write_ImageRoader() {
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
    public class ThreadPolicy {

        // For smooth networking
        public ThreadPolicy() {

            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
    }

    //http통신하는 부분(데이터(이름) 받아오는 부분)
    public class Writername extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostwriteActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            Log.d("MyProfileActivityResult", result);
        }

        //이부분은 다시 공부해야한다.
        @Override
        protected String doInBackground(String... params) {
            String myemail = (String) params[1];
            //  String phonenumber = (String) params[4];
            String serverURL = (String) params[0];
            String postParameters = "myemail=" + myemail;

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

    //작성된 게시글을 mysql에 저장시키기 위한 부분
    public class addpost extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostwriteActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("PostWriteActivityResult", result);
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
                        Toast.makeText(PostwriteActivity.this, "Server Response:" + imageClass.getResponse(), Toast.LENGTH_LONG).show();

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


        @Override
        protected String doInBackground(String... params) {

            String writeid = (String) params[1];
            String contents = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "writeid=" + writeid + "&contents=" + contents;

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

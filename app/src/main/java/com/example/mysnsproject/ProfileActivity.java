package com.example.mysnsproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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

public class ProfileActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "54.180.24.94";
    private ImageView profile;
    private Bitmap bitmap;
    public String email;
    private Button profilechangebtn;
    private EditText ChangeName;
    private BottomNavigationView bottomNavigationView;
    private Button logoutbtn;
    private ImageView home;
    private String TAG="ProfileActiviy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 위젯에 대한 참조.
        ChangeName = (EditText) findViewById(R.id.profile_text_name);

        //인텐트로 데이터값을 받아옴
        Intent startactivitymove = getIntent();
       // Log.d("1111111111", startactivitymove.getExtras().getString("profile"));

        //email변수의값을 설정해줌
        email = startactivitymove.getExtras().getString("profile");
        //Log.d("1111111111",name);

        //프로필 화면에 진입시 최초에 데이터를 불러오게 만드는 부분
        myprofileload myprofileload = new myprofileload();
        myprofileload.execute("http://" + IP_ADDRESS + "/myprofileload.php", email);


        //profilechangebtn이 어떤것인지 설정
        profilechangebtn = (Button) findViewById(R.id.profile_change_btn);

        //logoutbtn이 어떤것인지 설정
        logoutbtn = (Button) findViewById(R.id.profile_logout_btn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutstart = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(logoutstart);
                //기존에 실행되었던 모든 액티비티 종료시키기
                ActivityCompat.finishAffinity(ProfileActivity.this);
            }
        });

        //프로필 사진 부분
        profile = (ImageView) findViewById(R.id.profile_image_btn);
        //프로필 사진 생성되는 부분
        profile.setImageBitmap(new ImageRoader().
        getBitmapImg(email + ".jpg"));//파일이름
        //프로필 사진 선택할때 갤러리에 있는 이미지 불러오게 하는 메소드
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage1();
            }
        });
        //프로필채인지 버튼을 눌렀을때 업로드 이미지 메소드와 변경된 이름을 저장시킨다.
        profilechangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Changename = ChangeName.getText().toString();
                myprofilenamechange myprofilenamechange = new myprofilenamechange();
                myprofilenamechange.execute("http://" + IP_ADDRESS + "/myprofilechange.php", email, Changename);
            }
        });


        home = (ImageView) findViewById(R.id.profile_home_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //이미지 불러오는 부분
    public class ImageRoader {
        private final String serverUrl = "http://54.180.24.94/";
        public ImageRoader() {
            new ThreadPolicy();
        }
        public Bitmap getBitmapImg(String imgStr) {
            Bitmap bitmapImg = null;
            try {
                URL url = new URL(serverUrl +
                        URLEncoder.encode(imgStr, "utf-8"));

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


        public ThreadPolicy() {

            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
    }

    //이미지 선택하게 만드는 메소드
    private void selectImage1()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
        Log.d("qqweq","qqweq"+1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            Uri path = data.getData();
            Log.d("qqweq","qqweq"+path);

            try{
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                profile.setImageBitmap(bitmap);
                profile.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage();
        }
    }
    //서버로 이미지 전송하는 부분
    private void uploadImage(){
        String Image = imageToString();
        String Title = email;
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ImageClass> call = apiInterface.uploadImage(Title,Image);

        Log.d("Image",""+Image);
        Log.d("Title",""+Title);

        call.enqueue(new Callback<ImageClass>() {
            @Override
            public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {

                ImageClass imageClass = response.body();
                Toast.makeText(ProfileActivity.this,"Server Response:"+imageClass.getResponse(),Toast.LENGTH_LONG).show();

            }
            @Override
            public void onFailure(Call<ImageClass> call, Throwable t) {
            }
        });
    }
    private String imageToString()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }

    //http통신하는 부분(데이터(이름) 받아오는 부분)
    public class myprofileload extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ProfileActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            ChangeName.setText(result);
            Log.d("MyProfileActivityResult",result);
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
    //php에 이름 변경시 변경하게 만드는 코드
    public class myprofilenamechange extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ProfileActivity.this,
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

        //이부분은 다시 공부해야한다.
        @Override
        protected String doInBackground(String... params) {
            String myemail = (String) params[1];
            String mychangename = (String) params[2];
            String serverURL = (String) params[0];
            String postParameters = "myemail=" + myemail + "&mychangename=" + mychangename;

            Log.d("myemail",""+myemail);
            Log.d("mychangename",""+mychangename);

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

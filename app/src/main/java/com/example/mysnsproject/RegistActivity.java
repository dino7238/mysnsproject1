package com.example.mysnsproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "phptest";

    private EditText Name;
    private EditText Email;
    private EditText Password;
    private EditText PasswordConfrim;
    private EditText PhoneNumber;
    private TextView Emailcheck;
    public Button idcheckbutton;
    private ImageView setImage;
    public String AAA;
    public Integer A = 0;
    private Button buttonInsert;
    public Integer AA = 0;
    private ImageView profileImg;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap;
    public Integer AAAA = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        AA = 0;
        Name = (EditText) findViewById(R.id.etname);
        Email = (EditText) findViewById(R.id.etEmail);
        Password = (EditText) findViewById(R.id.etPassword);
        PasswordConfrim = (EditText) findViewById(R.id.etPasswordConfirm);
        PhoneNumber = (EditText) findViewById(R.id.etnumber);
        idcheckbutton = (Button) findViewById(R.id.idcheckbutton);
        setImage = (ImageView) findViewById(R.id.setImage);
        buttonInsert = (Button) findViewById(R.id.btnDone);
        profileImg = (ImageView) findViewById(R.id.profileimageview);
        //mTextViewResult = (TextView) findViewById(R.id.textView_main_result);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Log.d("kkkkkkkkkkkkkkk",""+AA);
        Log.d("stringAAAAAAAAAAA", "" + A);
        //버튼이 인에이블 되게 만들어 주는 코드
        buttonInsert.setEnabled(false);

        //아이디 중복체크 확인하는 버튼
        idcheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Email.getText().toString();
                //중복확인 버튼을 입력했을때 중복체크하는 php서버의 값을 가져오는 부분
                InsertData1 task2 = new InsertData1();
                task2.execute("http://" + IP_ADDRESS + "/checktest.php", Email.getText().toString());
                Log.d("AAAAAAAAAAA123213", "" + name);
                //Log.d("AAAAAAAAAAA", "" + task2.execute("http://" + IP_ADDRESS + "/checktest.php", name));
            }
        });

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("stringAAAAAA", "" + A);
                //db에 저장될 이름,이메일,패스워드,전화번호
                String Namedb = Name.getText().toString();//Namedb는 Name에 입력된 값
                String Emaildb = Email.getText().toString();//Emaildb는 Email에 입력된 값
                String Passworddb = Password.getText().toString();//Passworddb는 Password에 입력된 값
                String PhoneNumberdb = PhoneNumber.getText().toString();//Numberdb는 Number에 입력된 값



                //아이디,비밀번호등 확인하는곳
                if (Name.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                    Name.requestFocus();
                    return;
                }

                if (Email.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "아이디를 입력하세요!", Toast.LENGTH_SHORT).show();
                    Email.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                    if (Password.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    Password.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if (PasswordConfrim.getText().toString().length() == 0) {
                        Toast.makeText(RegistActivity.this, "비밀번호 확인을 입력하세요!", Toast.LENGTH_SHORT).show();
                        PasswordConfrim.requestFocus();
                    return;
                }
                //비밀번호 일치 하지 않을때 확인하는 코드
                if (!Password.getText().toString().equals(PasswordConfrim.getText().toString())) {
                    Toast.makeText(RegistActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    PasswordConfrim.requestFocus();
                    return;
                }
                //전화번호 입력 확인
                if (PhoneNumber.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "전화번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    PhoneNumber.requestFocus();
                    return;
                }
                if ((PhoneNumber.getText().toString().length() != 11)){
                    Toast.makeText(RegistActivity.this, "전화번호를 정확히 입력하세요!", Toast.LENGTH_SHORT).show();
                    PhoneNumber.requestFocus();
                    return;
                }
                else
                {
                    AA = 1;//AA값이 1이 될때 데이터를 보냄(이름,아이디,비밀번호,비밀번호 확인, 전화번호 등 위의 if문을 모두 통과했을때 AA값은 1로 변경됨
                    Log.d("qqqqqqqqqqqqqqq",""+AA);
                    if(AA==1){//AA값이 1이 되었을때 데이터를 저장시키기 위해서 php서버로 데이터를 보냄
                        InsertData task12 = new InsertData();
                        task12.execute("http://" + IP_ADDRESS + "/insert00.php", Namedb, Emaildb, Passworddb, PhoneNumberdb);
                      uploadImage();
                        //Toast.makeText(RegistActivity.this,"11111111111",Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(RegistActivity.this, AAA, Toast.LENGTH_SHORT).show();
                Log.d("qwerqwerqwerqwerqwer", "" + AA);
            }
        });


        // 비밀번호 일치 확인
        PasswordConfrim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Password.getText().toString().equals(PasswordConfrim.getText().toString())) {
                    setImage.setImageResource(R.drawable.sign_up_password_right);
                } else {
                    setImage.setImageResource(R.drawable.sign_up_password_currect);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //취소버튼을 눌렀을때 현재 엑티비티를 finish(종료)시킴
        Button buttoncancel = (Button) findViewById(R.id.btnCancel);
        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //php 회원가입 연동하는 부분
    public class InsertData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegistActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            //php서버에서 추가라는 값을 받을때 현재 엑티비티의 Email값이 메인 엑티비티로 보내지게 만드는 코드
            if(result.equals("추가")) {
                Intent intent = new Intent();
                intent.putExtra("email", Email.getText().toString());
                // 자신을 호출한 Activity로 데이터를 보낸다.
                setResult(RESULT_OK, intent);
                //Toast.makeText(RegistActivity.this, "회원가입에 성공하셨습니다!", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                //중복된아이디일 경우 버튼을 다시 비활성화 시킴
                Toast.makeText(RegistActivity.this, "중복된 아이디입니다. 중복확인을 해주세요", Toast.LENGTH_SHORT).show();
                buttonInsert.setEnabled(false);
                AA=0;
            }
        }

        //이부분은 다시 공부해야한다.
        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[1];
            String email = (String) params[2];
            String password = (String) params[3];
            String phonenumber = (String) params[4];
            String serverURL = (String) params[0];
            String postParameters = "name=" + name + "&email=" + email + "&password=" + password + "&phonenumber=" + phonenumber;

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

    //중복확인하는 php와 연결하는 부분

    class InsertData1 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegistActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //mTextViewResult.setText(result);
            AAA = result;
            Log.d(TAG, "POST response  - " + result);
            Log.d(TAG, "AAAAAAAAAAA  - " + result);
            Log.d(TAG, "BBBBBBBBBBB  - " + AAAA);
            if (AAAA == 1) {
                if (result.equals("가능")) {
                    buttonInsert.setEnabled(true);
                    Toast.makeText(RegistActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                }
                if (result.equals("중복")) {
                    buttonInsert.setEnabled(false);
                    Toast.makeText(RegistActivity.this, "사용 불가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    AA = 0;
                }
            }else {
                Toast.makeText(RegistActivity.this, "사진을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "name=" + name;
            Log.d("doingbackground" + name, "doingbackground" + name);

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
    //사진 선택해서 화면에 보여지게 해주는 부분
    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
        Log.d("qqweq","qqweq"+IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null)
        {
            Uri path = data.getData();
            AAAA=1;
            Log.d("qqweq","qqweq"+path);
            try{
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                profileImg.setImageBitmap(bitmap);
                profileImg.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //서버로 이미지 전송하는 부분
    private void uploadImage(){
        String Image = imageToString();
        String Title = Email.getText().toString();


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ImageClass> call = apiInterface.uploadImage(Title,Image);

        Log.d("Image",""+Image);
        Log.d("Title",""+Title);

        call.enqueue(new Callback<ImageClass>() {
            @Override
            public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {

                ImageClass imageClass = response.body();
                Toast.makeText(RegistActivity.this,"Server Response:"+imageClass.getResponse(),Toast.LENGTH_LONG).show();

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

}
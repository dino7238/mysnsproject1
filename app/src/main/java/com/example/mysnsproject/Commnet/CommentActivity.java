package com.example.mysnsproject.Commnet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysnsproject.FriendList.FriendListActivity;
import com.example.mysnsproject.FriendList.FriendListData;
import com.example.mysnsproject.FriendList.FriendprofileActivity;
import com.example.mysnsproject.PosteditActivity;
import com.example.mysnsproject.PostwriteActivity;
import com.example.mysnsproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private Button commententer;
    private EditText comment_edit;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "Commentaddphp";
    private String commentjsonstring;
    private ArrayList<CommentData> commentlistArraylist;
    private CommentAdapter commentAdapter;
    private RecyclerView commentRecyclerview;
    private String commentuser;
    private String postidx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commententer = (Button) findViewById(R.id.CommnetActivity_BTN_enter);
        comment_edit = (EditText) findViewById(R.id.CommnetActivity_ET_write);

        //인텐트로 값 가져오는 부분
        Intent intent = getIntent();
        commentuser = intent.getStringExtra("startactvity_login_id");//로그인한 유저의 아이디 받아오기
        Log.d("startactvity_login_id", commentuser);

        postidx = intent.getStringExtra("startactvity_post_idx");//게시글 번호 받아오기
        Log.d("startactvity_post_idx", postidx);

        //friendlistRecyclerView가 무엇인지 설정하기
        commentRecyclerview = (RecyclerView) findViewById(R.id.commnent_list_recyclerview);

        //friendlistRecyclerView의 레이아웃 매니저 설정하기
        commentRecyclerview.setLayoutManager(new LinearLayoutManager(this));


        //friendlistArraylist의 배열 선언
        commentlistArraylist = new ArrayList<>();

        commentAdapter = new CommentAdapter(this, commentlistArraylist);
        commentRecyclerview.setAdapter(commentAdapter);


        //friendlistArraylist데이터 초기화 해주기
        commentlistArraylist.clear();
        //friendlistArraylist 데이터 호출
        commentAdapter.notifyDataSetChanged();

        //Getcommentdata 실행해서 코멘트 목록 가져오기
        Getcommentdata getcommentdata = new Getcommentdata();
        getcommentdata.execute("http://"+IP_ADDRESS+"/commentdata.php", postidx);

        //코멘트작성 버튼을 누를때 작성한 내용이 mysql에 저장되게 만듬
        commententer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_edit.getText().toString().length() == 0) {
                    Toast.makeText(CommentActivity.this, "내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    commentadd commentadd = new commentadd();
                    commentadd.execute("http://" + IP_ADDRESS + "/commentadd.php", postidx, commentuser, comment_edit.getText().toString());
                    Toast.makeText(CommentActivity.this, "댓글 작성 완료!", Toast.LENGTH_SHORT).show();
                    //friendlistArraylist데이터 초기화 해주기
                    commentlistArraylist.clear();
                    //friendlistArraylist 데이터 호출
                    commentAdapter.notifyDataSetChanged();
                    //Getcommentdata 실행해서 코멘트 목록 가져오기
                    Getcommentdata getcommentdata = new Getcommentdata();
                    getcommentdata.execute("http://"+IP_ADDRESS+"/commentdata.php", postidx);
                    comment_edit.setText("");
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //friendlistArraylist데이터 초기화 해주기
        commentlistArraylist.clear();
        //friendlistArraylist 데이터 호출
        commentAdapter.notifyDataSetChanged();

        //Getcommentdata 실행해서 코멘트 목록 가져오기
        Getcommentdata getcommentdata = new Getcommentdata();
        getcommentdata.execute("http://"+IP_ADDRESS+"/commentdata.php", postidx);
    }

    ///////////코멘트 추가하는 부분
    public class commentadd extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CommentActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            //로그에 result 출력시키기
            Log.d("CommentActivityAdd", result);
        }

        @Override
        protected String doInBackground(String... params) {
            String postidx = (String) params[1];
            String commentid = (String) params[2];
            String comment = (String) params[3];
            String serverURL = (String) params[0];
            String postParameters = "post_idx=" + postidx + "&comment=" + comment + "&comment_id=" + commentid;
            //String postParameters = "post_idx=" + postidx + "&comment_id=" + commentid ;

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
                Log.d("좋아요 버튼 한번눌름", "POST response code - " + responseStatusCode);

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

                Log.d("좋아요 버튼 한번눌름", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }
    ///////////코멘트 삭제하는 부분
    public class commentdelete extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CommentActivity.this,
                    "Please Wait", null, true, true);
        }

        //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            //ChangeName.setText(result);
            Log.d("CommentActivityDelete", result);
        }

        //백그라운드 실행 시키기, 포스트값으로 데이터 전달하기
        @Override
        protected String doInBackground(String... params) {
            String postidx = (String) params[1];//게시판 번호
            String commentidx = (String) params[2];//작성된 코멘트 번호
            String serverURL = (String) params[0];
            String postParameters = "post_idx=" + postidx + "&commentidx=" + commentidx;
            //String postParameters = "post_idx=" + postidx + "&comment_id=" + commentid ;

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
                Log.d("좋아요 버튼 한번눌름", "POST response code - " + responseStatusCode);

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

                Log.d("좋아요 버튼 한번눌름", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }

    //코멘트 데이터를 불러오기
    private class Getcommentdata extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CommentActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {
                Toast.makeText(CommentActivity.this, result, Toast.LENGTH_SHORT).show();
                //mTextViewResult.setText(errorString);
            } else {
                commentjsonstring = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String postidx = (String) params[1];
            String postParameters = "postidx=" + postidx;
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
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
    private void showResult() {

        String TAG_JSON = "webnautes";
        String TAG_Comment = "comment";
        String TAG_Commentid = "commentid";
        String TAG_Postidx = "postidx";
        String TAG_Commentidx = "commentidx";
       //제이슨오브젝트로 값 받아오기
        try {
            JSONObject jsonObject = new JSONObject(commentjsonstring);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                //php에서 받아온 제이슨 데이터를 풀어주는 부분
                JSONObject item = jsonArray.getJSONObject(i);

                String Comment = item.getString(TAG_Comment);
                String Commentid = item.getString(TAG_Commentid);
                String Postidx = item.getString(TAG_Postidx);
                String Commentidx = item.getString(TAG_Commentidx);

                //CommentData에 데이터 값을 생성해주는 부분
                CommentData commentData = new CommentData();
                //데이터들을 저장하는 부분
                commentData.setComment(Comment);
                commentData.setPost_idx(Postidx);
                commentData.setCommentuser_id(Commentid);
                commentData.setComment_idx(Commentidx);

                commentlistArraylist.add(commentData);
                commentAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    //이미지 불러오는 부분
    public static class ImageRoader {
        private final String serverUrl = "http://54.180.24.94/";

        public ImageRoader() {
            new FriendListActivity.ThreadPolicy();
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
    public static class ThreadPolicy {

        //쓰레드를 사용해서 네트워크를 원할하게 해줌
        public ThreadPolicy() {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //코멘트 어댑터 클래스
    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {
        private ArrayList<String> commenteditarraylist;
        private ArrayList<CommentData> commentListDataArrayList = null;
        private Activity context = null;
        private Object LoginID;

        public CommentAdapter(Activity context, ArrayList<CommentData> list) {
            this.context = context;
            this.commentListDataArrayList = list;
        }

        //뷰홀더 클래스 변수선언 및 변수가 무엇을 뜻하는지 정함
        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView comment_id;
            protected TextView comment;
            protected ImageView comment_image;
            protected TextView delete;
            protected TextView edit;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                this.comment_id = (TextView) itemView.findViewById(R.id.comment_item_list_Textview_userid);
                this.comment = (TextView) itemView.findViewById(R.id.comment_item_list_Textview_comment);
                this.comment_image = (ImageView) itemView.findViewById(R.id.comment_item_list_imageview_user);
                this.delete = (TextView) itemView.findViewById(R.id.comment_item_list_Textview_delete);
                this.edit = (TextView) itemView.findViewById(R.id.comment_item_list_Textview_edit);
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_list, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        //데이터들이 보여지는 부분
        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, final int i) {
            //작성자 아이디가 보여지는 부분
            customViewHolder.comment_id.setText(commentListDataArrayList.get(i).getCommentuser_id());
            //작성자 이름이 보여지는 부분
            customViewHolder.comment.setText(commentListDataArrayList.get(i).getComment());
            //작성자 사진이 보여지는 부분
            customViewHolder.comment_image.setImageBitmap(new FriendListActivity.ImageRoader().getBitmapImg(commentListDataArrayList.get(i).getCommentuser_id() + ".jpg"));

            Log.d("",""+commentuser);
            if (commentListDataArrayList.get(i).getCommentuser_id().equals(commentuser)){
                //customViewHolder.delete.setEnabled(true);
                customViewHolder.delete.setVisibility(View.VISIBLE);
                customViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CommentActivity.this,"댓글을 삭제했습니다.",Toast.LENGTH_SHORT).show();
                       // Toast.makeText(CommentActivity.this,"해당글 mysql 번호는"+commentListDataArrayList.get(i).getComment_idx(),Toast.LENGTH_SHORT).show();
                        commentdelete commentdelete = new commentdelete();
                        commentdelete.execute("http://"+IP_ADDRESS+"/commentdelete.php", postidx, commentListDataArrayList.get(i).getComment_idx());
                        commentListDataArrayList.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, commentListDataArrayList.size());
                        notifyDataSetChanged();
                    }
                });
            }else{
                customViewHolder.delete.setVisibility(View.GONE);
            }

            if (commentListDataArrayList.get(i).getCommentuser_id().equals(commentuser)){
                //customViewHolder.delete.setEnabled(true);
                customViewHolder.edit.setVisibility(View.VISIBLE);
                customViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CommentEditActivity.class);
                        intent.putExtra("commenteditidx", commentListDataArrayList.get(i).getComment_idx());//코멘트 인덱스 보내주기
                        intent.putExtra("commentcomment", commentListDataArrayList.get(i).getComment());//코멘트 내용 보내주기
                        //intent.putExtra("4", friend_idx.getText().toString());
                       // intent.putExtra("commentedit", commenteditarraylist);
                        context.startActivity(intent);
                    }
                });
            }else{
                customViewHolder.edit.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return (null != commentListDataArrayList ? commentListDataArrayList.size() : 0);
        }
    }
}

package com.example.mysnsproject.PostListActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysnsproject.Commnet.CommentActivity;
import com.example.mysnsproject.FriendList.FriendListActivity;
import com.example.mysnsproject.FriendList.FriendListData;
import com.example.mysnsproject.FriendList.FriendprofileActivity;
import com.example.mysnsproject.PosteditActivity;
import com.example.mysnsproject.PostwriteActivity;
import com.example.mysnsproject.ProfileActivity;
import com.example.mysnsproject.R;
import com.example.mysnsproject.SearchFriend;

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
import java.text.BreakIterator;
import java.util.ArrayList;

public class startActivity extends AppCompatActivity {

    private ImageView post_write;
    private ImageView profile;
    private ImageView imageview;
    private ImageView searchfriend;
    private ImageView friendlist;
    private static String IP_ADDRESS = "54.180.24.94";
    private static String TAG = "StartActivityPhpTest";
    private ArrayList<PostData> postArraylist;
    private PostAdapter postAdapter;
    private RecyclerView postRecyclerview;
    private String postjsonString;
    public String bbb;
    public ArrayList<String> Check = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Intent startactivitymove = getIntent();
        //final Intent profilename = getIntent();
//        Log.d("222222222",startactivitymove.getExtras().getString("name"));
        bbb = startactivitymove.getExtras().getString("name");
        Log.d("333333333333333", bbb);

        postRecyclerview = (RecyclerView) findViewById(R.id.post_item_list_recyclerview);
        postRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        postArraylist = new ArrayList<>();

        postAdapter = new PostAdapter(this, postArraylist);
        postRecyclerview.setAdapter(postAdapter);

        post_write = (ImageView) findViewById(R.id.start_post_btn);

        post_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PostwriteActivity.class);
                intent2.putExtra("postwrite", bbb);
                postArraylist.clear();
                postAdapter.notifyDataSetChanged();
                startActivity(intent2);
            }
        });

        //profilemovebtn = (Button) findViewById(R.id.profile_move_btn);
        profile = (ImageView) findViewById(R.id.start_profile_btn);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), ProfileActivity.class);
                intent2.putExtra("profile", bbb);
                startActivity(intent2);
                postArraylist.clear();
                postAdapter.notifyDataSetChanged();
            }
        });

        searchfriend = (ImageView) findViewById(R.id.start_search_btn);
        searchfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), SearchFriend.class);
                intent2.putExtra("searchfriend", bbb);
                startActivity(intent2);
            }
        });

        friendlist = (ImageView) findViewById(R.id.start_hart_btn);
        friendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), FriendListActivity.class);
                intent2.putExtra("friendlistmyid", bbb);
                startActivity(intent2);
            }
        });
        postRecyclerview.setHasFixedSize(true);

        GetPostData getFriendListData1 = new GetPostData();
        getFriendListData1.execute("http://" + IP_ADDRESS + "/post.php", bbb);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        postRecyclerview.removeAllViewsInLayout();
        postRecyclerview.setAdapter(postAdapter);
        postArraylist.clear();
        postAdapter.notifyDataSetChanged();
        Check = new ArrayList<>();
        //이전에 데이터를 삭제하고 재실행 시키기
        GetPostData getFriendListData1 = new GetPostData();
        getFriendListData1.execute("http://" + IP_ADDRESS + "/post.php", bbb);
    }

    //게시글 데이터를 불러오기
    private class GetPostData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(startActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response체크 - " + result);

            if (result == null) {
                Toast.makeText(startActivity.this, result, Toast.LENGTH_SHORT).show();
                //mTextViewResult.setText(errorString);
            } else {
                postjsonString = result;
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
        String TAG_ID = "writeid";
        String TAG_content = "content";
        String TAG_idx = "idx";
        String TAG_time = "time";
        String TAG_commentcount = "commentcount";
        String TAG_postidx = "postidx";
        String TAG_likeid = "likeid";
        String TAG_JSON1 = "asdf";


        try {
            JSONObject jsonObject = new JSONObject(postjsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                //php에서 받아온 제이슨 데이터를 풀어주는 부분
                JSONObject item = jsonArray.getJSONObject(i);

                String postidx = item.getString(TAG_postidx);
                String likeid = item.getString(TAG_likeid);


                Check.add(postidx);

                //데이터들을 저장하는 부분
                //   postData.setLikepostnumber(likepostnumber);
                Log.d("friendidx", "" + postidx);


            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

        try {
            JSONObject jsonObject = new JSONObject(postjsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON1);

            for (int i = 0; i < jsonArray.length(); i++) {
                //php에서 받아온 제이슨 데이터를 풀어주는 부분
                JSONObject item = jsonArray.getJSONObject(i);

                String wirite_id = item.getString(TAG_ID);
                String postcontent = item.getString(TAG_content);
                String posttime = item.getString(TAG_time);
                String idx = item.getString(TAG_idx);
                String commentcount = item.getString(TAG_commentcount);

                //  String likepostnumber = item.getString(TAG_likepostnumber);

                //Friendlistdata에 데이터 값을 생성해주는 부분
                PostData postData = new PostData();
                //데이터들을 저장하는 부분
                postData.setWriter_id(wirite_id);
                postData.setPost_contents(postcontent);
                postData.setPost_time(posttime);
                postData.setPost_idx(idx);
                postData.setCommentcount(commentcount);

                postData.setPostlikeid(false);

                Log.d("asdfasdfasdf", "" + Check);
                for (int j = 0; j < Check.size(); j++) {
                    if (idx.equals(Check.get(j))) {
                        postData.setPostlikeid(true);
                        break;
                    }
                }
                postArraylist.add(postData);
                postAdapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    //작성자의 프로필 이미지 불러오는 부분
    public static class startactivity_imageload {
        private final String serverUrl = "http://54.180.24.94/";

        public startactivity_imageload() {
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

    //어댑터 클래스!!!!!!!!!!
    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.CustomViewHolder> {
        public ArrayList<String> CommentArrayListString;
        public ArrayList<String> postArrayListString;
        public ArrayList<PostData> postDataArrayList = null;
        public Activity context = null;
        private String AAA = "B";
        public ImageView like_imageview;
        private Boolean likeimagecheck;

        public PostAdapter(Activity context, ArrayList<PostData> list) {
            this.context = context;
            this.postDataArrayList = list;

        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            //public ImageView like_imageview;
            protected TextView writer_id;
            protected TextView post_contents;
            protected ImageView writer_image;
            protected ImageView post_imageviews;
            protected ImageView post_commnet;
            protected ImageView setting;
            protected TextView post_number;
            protected TextView numberofcomment;
            protected ImageView like_imageviews;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                this.post_commnet = (ImageView) itemView.findViewById(R.id.post_list_item_commetn_imageview);
                like_imageview = (ImageView) itemView.findViewById(R.id.post_list_item_hart_imageview);
                this.writer_id = (TextView) itemView.findViewById(R.id.post_item_list_textview_writerid);
                this.post_contents = (TextView) itemView.findViewById(R.id.post_item_list_textview_contents);
                this.writer_image = (ImageView) itemView.findViewById(R.id.post_itemt_list_imageview_writerimage);
                this.post_imageviews = (ImageView) itemView.findViewById(R.id.post_item_list_imageview_imageview);
                this.like_imageviews = (ImageView) itemView.findViewById(R.id.post_list_item_hart_imageview);
                this.post_number = (TextView) itemView.findViewById(R.id.post_list_item_post_number);
                this.setting = (ImageView) itemView.findViewById(R.id.post_item_list_ImageView_mysetting);
                this.numberofcomment = (TextView) itemView.findViewById(R.id.post_list_item_post_comment_numberofcomment);
                // this.like_imageviews.setOnClickListener(this);
                //이곳에 데이터를 바꾸려고하면 제일 처음 데이터만 설정 할 수 있네?
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item_list, null);
            return new CustomViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        //데이터들이 보여지는 부분
        @Override
        public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, final int i) {
            // likeimagecheck=true;
            customViewHolder.numberofcomment.setText("댓글이 " + postDataArrayList.get(i).getCommentcount() + "개 있습니다");
            //친구 아이디가 보여지는 부분
            customViewHolder.writer_id.setText(postDataArrayList.get(i).getWriter_id());
            //친구 이름이 보여지는 부분
            customViewHolder.post_contents.setText(postDataArrayList.get(i).getPost_contents());
            //친구 사진이 보여지는 부분
            customViewHolder.writer_image.setImageBitmap(new startActivity.startactivity_imageload().getBitmapImg(postDataArrayList.get(i).getWriter_id() + ".jpg"));
            customViewHolder.post_imageviews.setImageBitmap(new startActivity.startactivity_imageload().getBitmapImg(postDataArrayList.get(i).getPost_idx() + ".jpg"));
            customViewHolder.post_number.setText(postDataArrayList.get(i).getPost_idx());

            final int position1 = customViewHolder.getAdapterPosition();


            if (postDataArrayList.get(i).getPostlikeid() == true) {
                //일치하는 번호(좋아요를 한 게시글)가 있어서 빨간하트로 출력될때
                customViewHolder.like_imageviews.setImageResource(R.drawable.hart);
            } else {
                customViewHolder.like_imageviews.setImageResource(R.drawable.blackhart);
            }
            Log.d("111111111111111111111", postDataArrayList.get(i).getPost_idx() + "" + likeimagecheck);

            customViewHolder.like_imageviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postDataArrayList.get(i).getPostlikeid()==false) {
                        Log.d("likeimagecheck가 false일때", "" + likeimagecheck);
                        checklike addlike = new checklike();
                        addlike.execute("http://" + IP_ADDRESS + "/addlike.php", postDataArrayList.get(i).getPost_idx(), bbb);
                        postDataArrayList.get(i).setPostlikeid(true);
                        Log.d("하트확인", "검정하트에서 빨간하트로 되야해"+postDataArrayList.get(i).getPost_idx()+likeimagecheck);
                    } else if (postDataArrayList.get(i).getPostlikeid() == true) {
                        Log.d("likeimagecheck가 true일때", "" + likeimagecheck);
                        checklike cancellike = new checklike();
                        cancellike.execute("http://" + IP_ADDRESS + "/addlikecancel.php", postDataArrayList.get(i).getPost_idx(), bbb);
                        postDataArrayList.get(i).setPostlikeid(false);
                        Log.d("하트확인", "빨간하트에서 검정하트로 되야해"+postDataArrayList.get(i).getPost_idx()+likeimagecheck);
                    }
                    notifyDataSetChanged();
                }
            });

            //댓글달기기능
            customViewHolder.post_commnet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("startactvity_login_id", bbb);//로그인한 유저의 아이디 보내주기
                    intent.putExtra("startactvity_post_idx", postDataArrayList.get(i).getPost_idx());//게시글 번호 보내기
                    //intent.putExtra("4", friend_idx.getText().toString());
                    intent.putExtra("startactvity_String", CommentArrayListString);
                    context.startActivity(intent);
                }
            });

            //게시글 수정,삭제 하는 부분
            if (customViewHolder.writer_id.getText().toString().equals(bbb)) {
                // setting.setImageResource(R.drawable.setting);
                customViewHolder.setting.setVisibility(View.VISIBLE);
                customViewHolder.setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("삭제후아이디확인", "" + customViewHolder.writer_id.getText().toString());
                        Log.d("삭제후로그인아이디확인", "" + bbb);
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                        getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.m1:
                                        //Toast.makeText(getApplication(), "수정", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, PosteditActivity.class);
                                        intent.putExtra("writeid", bbb);//친구아이디를 보내주기
                                        intent.putExtra("postidx", postDataArrayList.get(i).getPost_idx());
                                        intent.putExtra("postcontent", postDataArrayList.get(i).getPost_contents());
                                        //intent.putExtra("4", friend_idx.getText().toString());
                                        intent.putExtra("게시글", postArrayListString);
                                        context.startActivity(intent);
                                        break;
                                    case R.id.m2:
                                        postdelete postdelete = new postdelete();
                                        postdelete.execute("http://" + IP_ADDRESS + "/postdelete.php", postDataArrayList.get(i).getPost_idx(), bbb);
                                        postDataArrayList.remove(position1);
                                        notifyItemRemoved(position1);
                                        notifyItemRangeChanged(position1, postDataArrayList.size());
                                        notifyDataSetChanged();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            } else {
                customViewHolder.setting.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return postDataArrayList.size();
        }

        ///////////게시판 삭제 눌렀을때 발생하는 부분
        public class postdelete extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(context,
                        "Please Wait", null, true, true);
            }

            //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
            @Override
            public void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();

                Log.d("goodbuttonactivity", result);
                if (result.equals("삭제 완료")) {
                    Toast.makeText(getApplication(), "게시글을 삭제 했습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplication(), "게시글을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();

                }
            }

            //이부분은 다시 공부해야한다.
            @Override
            protected String doInBackground(String... params) {
                String idx = (String) params[1];
                String likeid = (String) params[2];
                //  String phonenumber = (String) params[4];
                String serverURL = (String) params[0];
                String postParameters = "postidx=" + idx + "&like_id=" + likeid;

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

        ///////////좋아요 확인하는 부분 눌렀을때 발생하는 부분
        public class checklike extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(context,
                        "Please Wait", null, true, true);
            }

            //데이터를 확인 하는 코드 (result값에 데이터를 받아옴)
            @Override
            public void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();

                Log.d("checkid", result);
                if (result.equals("좋아요추가")) {
                    Toast.makeText(context, "좋아요 추가", Toast.LENGTH_SHORT).show();
                    likeimagecheck = true;
                } else if (result.equals("좋아요취소")) {
                    Toast.makeText(context, "좋아요 취소", Toast.LENGTH_SHORT).show();
                    likeimagecheck = false;
                }

            }

            //이부분은 다시 공부해야한다.
            @Override
            protected String doInBackground(String... params) {
                String idx = (String) params[1];
                String likeid = (String) params[2];
                //  String phonenumber = (String) params[4];
                String serverURL = (String) params[0];
                String postParameters = "postidx=" + idx + "&like_id=" + likeid;

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
    }
}
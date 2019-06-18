package com.example.mysnsproject.PostListActivity;

public class PostData {
    private String writer_id;
    private String post_contents;
    private String post_idx;
    private String post_time;
    private String commentcount;
    private int viewType;
    private String likepostnumber;
    private Boolean postlikeid;

    public Boolean getPostlikeid() {
        return postlikeid;
    }

    public void setPostlikeid(Boolean postlikeid) {
        this.postlikeid = postlikeid;
    }

    public String getLikepostnumber() {
        return likepostnumber;
    }

    public void setLikepostnumber(String likepostnumber) {
        this.likepostnumber = likepostnumber;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public String getPost_contents() {
        return post_contents;
    }

    public void setPost_contents(String post_contents) {
        this.post_contents = post_contents;
    }

    public String getPost_idx() {
        return post_idx;
    }

    public void setPost_idx(String post_idx) {
        this.post_idx = post_idx;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(String commentcount) {
        this.commentcount = commentcount;
    }
}

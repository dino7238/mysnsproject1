package com.example.mysnsproject.Commnet;


import android.os.Parcelable;

public class CommentData  {
    private String commentuser_id;
    private String comment;
    private String comment_idx;
    private String post_idx;

    public String getCommentuser_id() {
        return commentuser_id;
    }

    public void setCommentuser_id(String commentuser_id) {
        this.commentuser_id = commentuser_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_idx() {
        return comment_idx;
    }

    public void setComment_idx(String comment_idx) {
        this.comment_idx = comment_idx;
    }

    public String getPost_idx() {
        return post_idx;
    }

    public void setPost_idx(String post_idx) {
        this.post_idx = post_idx;
    }
}


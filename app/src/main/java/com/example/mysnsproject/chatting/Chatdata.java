package com.example.mysnsproject.chatting;

public class Chatdata {
    private String Chatuser_id;
    private String Chat;
    private Boolean chatboolean;

    public Boolean getChatboolean() {
        return chatboolean;
    }

    public void setChatboolean(Boolean chatboolean) {
        this.chatboolean = chatboolean;
    }

    public String getChatuser_id() {
        return Chatuser_id;
    }

    public void setChatuser_id(String chatuser_id) {
        Chatuser_id = chatuser_id;
    }

    public String getChat() {
        return Chat;
    }

    public void setChat(String chat) {
        Chat = chat;
    }
}

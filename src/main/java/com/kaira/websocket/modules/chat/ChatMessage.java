package com.kaira.websocket.modules.chat;


public class ChatMessage {
    private String userName;
    private String content;

    public ChatMessage() {
    }
    public ChatMessage(String userName) {
        super();

        this.userName = userName;
    }
    public ChatMessage(String userName, String content) {
        super();

        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

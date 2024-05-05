package com.example.quizapp;

public interface UserDataCallback {
    void onUserDataReceived(User user);
    void onUserNotFound();
    void onFailedToReadData();
}

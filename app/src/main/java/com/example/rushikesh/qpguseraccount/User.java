package com.example.rushikesh.qpguseraccount;

/**
 * Created by Rushikesh on 05/04/2018.
 */

public class User {
    public String userId;
    public String userName;
    public String userUid;

    public User(){

    }

    public User(String userId, String userName,String userUid) {
        this.userId = userId;
        this.userName = userName;
        this.userUid = userUid;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUid() {
        return userUid;
    }
}

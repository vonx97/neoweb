package com.neosoft.neoweb.data;

public class AccessData {

    private int id;
    private int userId;
    private int appId;

    public AccessData(int id, int userId, int appId) {
        this.id = id;
        this.userId = userId;
        this.appId = appId;
    }

    public AccessData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}

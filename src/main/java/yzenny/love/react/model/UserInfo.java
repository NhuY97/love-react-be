package yzenny.love.react.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1981953460068735073L;

    private String username;
    private String roomId;

    public UserInfo(String username) {
        this.username = username;
    }

    public UserInfo(String username, String roomId) {
        this.username = username;
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

package yzenny.love.react.service;

import yzenny.love.react.model.UserInfo;

public interface UserService {
    boolean checkIfUserExisted(String username);

    UserInfo getUser(String username);

    UserInfo saveUser(String username, String roomId);

    void deleteUser(String username);
}

package yzenny.love.react.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yzenny.love.react.model.BaseResponse;
import yzenny.love.react.model.UserInfo;
import yzenny.love.react.model.UserInfoResponse;
import yzenny.love.react.service.UserService;

@RestController
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public ResponseEntity<UserInfoResponse> index(HttpServletRequest request) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        String username = (String) request.getSession().getAttribute("username");

        if (username == null || username.isEmpty()) {
            userInfoResponse.setStatus(404);
            userInfoResponse.setMessage("Not found!");
            return ResponseEntity.ok(userInfoResponse);
        }

        UserInfo userInfo = userService.getUser(username);
        if (userInfo == null) {
            request.getSession(true).invalidate();
            userInfoResponse.setStatus(404);
            userInfoResponse.setMessage("Not found!");
            return ResponseEntity.ok(userInfoResponse);
        }

        userInfoResponse.setStatus(0);
        userInfoResponse.setMessage("OK");
        userInfoResponse.setUserInfo(userInfo);

        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping(path = "/create-user")
    public ResponseEntity<UserInfoResponse> doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        username = username.trim();

        if (username.isEmpty()) {
            userInfoResponse.setStatus(400);
            userInfoResponse.setMessage("Vui lòng nhập lại tên!");
            return ResponseEntity.ok(userInfoResponse);
        }

        if (userService.checkIfUserExisted(username)) {
            userInfoResponse.setStatus(409);
            userInfoResponse.setMessage("Tiếc quá, tên này đã có người đặt rồi!");
            return ResponseEntity.ok(userInfoResponse);
        }

        UserInfo userInfo = userService.saveUser(username, "");
        request.getSession().setAttribute("username", userInfo.getUsername());

        userInfoResponse.setStatus(0);
        userInfoResponse.setMessage("OK");
        userInfoResponse.setUserInfo(userInfo);
        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping(path = "/remove-user")
    public ResponseEntity<BaseResponse> logout(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username != null && !username.isEmpty()) {
            userService.deleteUser(username);
        }

        request.getSession(true).invalidate();

        BaseResponse response = new BaseResponse();
        response.setStatus(0);
        response.setMessage("OK");
        return ResponseEntity.ok(response);
    }

}
package yzenny.love.react.controller;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yzenny.love.react.model.*;
import yzenny.love.react.service.GameService;
import yzenny.love.react.service.UserService;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @GetMapping("/health-check")
    public ResponseEntity<BaseResponse> healthCheck() {
        BaseResponse response = new BaseResponse();
        response.setStatus(0);
        response.setMessage("OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/verify")
    public ResponseEntity<UserInfoResponse> verify(@RequestParam(defaultValue = "") String username) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();

        if (username == null || username.isEmpty()) {
            userInfoResponse.setStatus(404);
            userInfoResponse.setMessage("Not found!");
            return ResponseEntity.ok(userInfoResponse);
        }

        UserInfo userInfo = userService.getUser(username);
        if (userInfo == null) {
            userInfoResponse.setStatus(404);
            userInfoResponse.setMessage("Not found!");
            return ResponseEntity.ok(userInfoResponse);
        }

        userInfoResponse.setStatus(0);
        userInfoResponse.setMessage("OK");
        userInfoResponse.setUserInfo(userInfo);

        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping(path = "/user/create")
    public ResponseEntity<UserInfoResponse> doLogin(@RequestParam(defaultValue = "") String username) {
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

        userInfoResponse.setStatus(0);
        userInfoResponse.setMessage("OK");
        userInfoResponse.setUserInfo(userInfo);
        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping(path = "/user/remove")
    public ResponseEntity<BaseResponse> logout(@RequestParam(defaultValue = "") String username) {
        if (username != null && !username.isEmpty()) {
            userService.deleteUser(username);
        }

        BaseResponse response = new BaseResponse();
        response.setStatus(0);
        response.setMessage("OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/game/create")
    public ResponseEntity<GameIdResponse> createNewGame(@RequestParam(defaultValue = "") String username) {
        GameIdResponse gameIdResponse = new GameIdResponse();
        if (verify(username).getBody().getStatus() != 0) {
            gameIdResponse.setMessage("User not found!");
            gameIdResponse.setStatus(404);
            return ResponseEntity.ok().body(gameIdResponse);
        }

        String roomId;
        try {
            roomId = gameService.createNewGame(username);
        } catch (JsonProcessingException e) {
            gameIdResponse.setMessage("Create new room occurs error!");
            gameIdResponse.setStatus(500);
            return ResponseEntity.ok().body(gameIdResponse);
        }
        userService.saveUser(username, roomId);

        gameIdResponse.setRoomId(roomId);
        gameIdResponse.setMessage("OK");
        gameIdResponse.setStatus(0);
        return ResponseEntity.ok().body(gameIdResponse);
    }

    @GetMapping(path = "/game/quit")
    public ResponseEntity<BaseResponse> quitGame(@RequestParam(defaultValue = "") String username) {
        BaseResponse baseResponse = new BaseResponse();

        UserInfo userInfo = userService.getUser(username);
        if (userInfo == null) {
            baseResponse.setStatus(404);
            baseResponse.setMessage("Not found!");
            return ResponseEntity.ok(baseResponse);
        }

        userService.saveUser(userInfo.getUsername(), "");

        BaseResponse response = new BaseResponse();
        response.setStatus(0);
        response.setMessage("OK");
        return ResponseEntity.ok(response);
    }
}
package yzenny.love.react.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import yzenny.love.react.model.*;
import yzenny.love.react.service.GameService;
import yzenny.love.react.service.UserService;

import static yzenny.love.react.model.GameStatus.*;

@Controller
public class WebSocketController {
    Logger log = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/game/join/{roomId}/{player}")
    public void joinGame(@DestinationVariable String roomId, @DestinationVariable String player) {
        log.info("Request join room - id {} player {}", roomId, player);
        GameResponse gameResponse = new GameResponse();
        GameInfo gameInfo = gameService.getGameInfo(roomId);
        if (gameInfo == null) {
            gameResponse.setStatus(404);
            gameResponse.setMessage("Không tìm thấy phòng!");
            simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
            return;
        }
        GameStatus gameStatus = getByStatus(gameInfo.getStatus());
        switch (gameStatus) {
            case CREATED:
                if(!player.equals(gameInfo.getPlayer1())) {
                    UserInfo userInfo = userService.getUser(player);
                    if (userInfo == null) {
                        gameResponse.setStatus(404);
                        gameResponse.setMessage("Không tìm thấy người chơi!");
                        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                        return;
                    }
                    if (userInfo.getRoomId() != null && !userInfo.getRoomId().isEmpty()) {
                        gameResponse.setStatus(999);
                        gameResponse.setMessage("Bạn đang ở một phòng khác!");
                        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                        return;
                    }
                    userService.saveUser(player, roomId);
                    gameInfo.player2Joined(player);
                    boolean rs = gameService.saveGameInfo(gameInfo);
                    if (!rs) {
                        gameResponse.setStatus(500);
                        gameResponse.setMessage("Không thể vào phòng, vui lòng thử lại sau!");
                        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                        return;
                    }
                }
                break;
            default:
                if (!player.equals(gameInfo.getPlayer1()) && !player.equals(gameInfo.getPlayer2())) {
                    gameResponse.setStatus(1001);
                    gameResponse.setMessage("Phòng đã đủ người!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }
                break;
        }

        gameResponse.setStatus(0);
        gameResponse.setMessage("OK");
        gameResponse.setGameInfo(new GameInfoResponse(gameInfo));
        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
    }

    @MessageMapping("/game/ready/{roomId}/{player}")
    public void ready(@DestinationVariable String roomId, @DestinationVariable String player) {
        log.info("Request ready play room - id {} player {}", roomId, player);
        GameResponse gameResponse = new GameResponse();
        GameInfo gameInfo = gameService.getGameInfo(roomId);
        if (gameInfo == null) {
            gameResponse.setStatus(404);
            gameResponse.setMessage("Không tìm thấy phòng!");
            simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
            return;
        }
        GameStatus gameStatus = getByStatus(gameInfo.getStatus());
        switch (gameStatus) {
            case ALLOW_READY:
            case WAITING_READY:
                if(!player.equals(gameInfo.getPlayer1()) && !player.equals(gameInfo.getPlayer2())) {
                    gameResponse.setStatus(404);
                    gameResponse.setMessage("Không thể sẵn sàng!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }
                UserInfo userInfo = userService.getUser(player);
                if (userInfo == null) {
                    gameResponse.setStatus(404);
                    gameResponse.setMessage("Không tìm thấy người chơi!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }
                if (!userInfo.getRoomId().equals(roomId)) {
                    gameResponse.setStatus(999);
                    gameResponse.setMessage("Bạn đang ở một phòng khác!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }

                boolean isPlayer1 = player.equals(gameInfo.getPlayer1());
                boolean isReady;
                if (isPlayer1) {
                    gameInfo.setPlayer1Ready(!gameInfo.isPlayer1Ready());
                    isReady = gameInfo.isPlayer1Ready();
                } else {
                    gameInfo.setPlayer2Ready(!gameInfo.isPlayer2Ready());
                    isReady = gameInfo.isPlayer2Ready();
                }

                gameInfo.setStatus(gameStatus.getStatus() + (isReady ? 1 : -1));
                boolean rs = gameService.saveGameInfo(gameInfo);
                if (!rs) {
                    gameResponse.setStatus(500);
                    gameResponse.setMessage("Không thể sẵn sàng, vui lòng thử lại sau!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }
                break;
            default:
                gameResponse.setStatus(9969);
                gameResponse.setMessage("Không thể thực hiện chức năng này!");
                simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                return;
        }

        gameResponse.setStatus(0);
        gameResponse.setMessage("OK");
        gameResponse.setGameInfo(new GameInfoResponse(gameInfo));
        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
    }

    @MessageMapping("/game/play/{roomId}/{player}/{number}")
    public void clickNumberButton(@DestinationVariable String roomId, @DestinationVariable String player, @DestinationVariable Integer number) {
        log.info("Room {} | player {} pick number {}", roomId, player, number);
        GameResponse gameResponse = new GameResponse();
        GameInfo gameInfo = gameService.getGameInfo(roomId);
        if (gameInfo == null) {
            gameResponse.setStatus(404);
            gameResponse.setMessage("Không tìm thấy phòng!");
            simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
            return;
        }

        GameStatus gameStatus = getByStatus(gameInfo.getStatus());
        switch (gameStatus) {
            case PLAYING:
                if(!player.equals(gameInfo.getPlayer1()) && !player.equals(gameInfo.getPlayer2())) {
                    gameResponse.setStatus(404);
                    gameResponse.setMessage("Không thể chơi!");
                    simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                    return;
                }
                int statusNumber = gameInfo.getMetric().get(number);
                if (statusNumber == NumberStatus.AVAILABLE.getStatus()) {
                    boolean correctNumber = number == (gameInfo.getCurrentNumber() + 1);
                    boolean isPlayer1 = player.equals(gameInfo.getPlayer1());
                    if (correctNumber) {
                        gameInfo.setCurrentNumber(number);
                        gameInfo.getMetric().put(number, (isPlayer1 ? NumberStatus.MARK_BY_PLAYER1.getStatus() : NumberStatus.MARK_BY_PLAYER2.getStatus()));
                        if (number == 100) {
                            gameInfo.setStatus(GAME_OVER.getStatus());
                        }
                        boolean rs = gameService.saveGameInfo(gameInfo);
                        if (!rs) {
                            gameResponse.setStatus(500);
                            gameResponse.setMessage("Handle pick number event failure!");
                            simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                            return;
                        }
                    }
                }
                break;
            default:
                gameResponse.setStatus(69);
                gameResponse.setMessage("Chưa sẵn sàng để chơi!");
                simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
                return;
        }

        gameResponse.setStatus(0);
        gameResponse.setMessage("OK");
        gameResponse.setGameInfo(new GameInfoResponse(gameInfo));
        simpMessagingTemplate.convertAndSend("/topic/" + roomId, gameResponse);
    }

}
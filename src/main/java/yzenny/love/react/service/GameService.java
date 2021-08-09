package yzenny.love.react.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import yzenny.love.react.model.GameInfo;

public interface GameService {
    String createNewGame(String username) throws JsonProcessingException;

    GameInfo getGameInfo(String roomId);

    boolean saveGameInfo(GameInfo gameInfo);

    boolean deleteGameInfo(String roomId);
}

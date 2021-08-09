package yzenny.love.react.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponse extends BaseResponse {
    private GameInfoResponse gameInfo;

    public GameInfoResponse getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfoResponse gameInfo) {
        this.gameInfo = gameInfo;
    }
}

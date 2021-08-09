package yzenny.love.react.model;

public enum GameStatus {
    CREATED(0), // vừa tạo
    ALLOW_READY(1), // đủ 2 ng
    WAITING_READY(2), // 1 ng ready
    PLAYING(3), // 2 ng ready
    GAME_OVER(4);

    private int status;
    GameStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static GameStatus getByStatus(int status) {
        for(GameStatus e : values()) {
            if(e.status == status) return e;
        }
        return GAME_OVER;
    }
}

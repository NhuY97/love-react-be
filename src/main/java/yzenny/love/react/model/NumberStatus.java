package yzenny.love.react.model;

public enum NumberStatus {
    AVAILABLE(0),
    MARK_BY_PLAYER1(1),
    MARK_BY_PLAYER2(2);

    private int status;
    NumberStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

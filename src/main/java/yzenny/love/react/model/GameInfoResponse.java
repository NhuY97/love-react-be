package yzenny.love.react.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameInfoResponse implements Serializable {
    private static final long serialVersionUID = 2506067078798065561L;

    private String roomId;
    private String player1;
    private String player2;
    private boolean player1Ready;
    private boolean player2Ready;
    private int status;
    private Set<Integer> metricNumber;
    private List<Integer> metricStatus;
    private int currentNumber;

    public GameInfoResponse(GameInfo gameInfo) {
        this.roomId = gameInfo.getRoomId();
        this.player1 = gameInfo.getPlayer1();
        this.player2 = gameInfo.getPlayer2();
        this.player1Ready = gameInfo.isPlayer1Ready();
        this.player2Ready = gameInfo.isPlayer2Ready();
        this.status = gameInfo.getStatus();
        this.currentNumber = gameInfo.getCurrentNumber();
        this.metricNumber = gameInfo.getMetric().keySet();
        this.metricStatus = gameInfo.getMetric().values().stream().collect(Collectors.toList());
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Set<Integer> getMetricNumber() {
        return metricNumber;
    }

    public void setMetricNumber(Set<Integer> metricNumber) {
        this.metricNumber = metricNumber;
    }

    public List<Integer> getMetricStatus() {
        return metricStatus;
    }

    public void setMetricStatus(List<Integer> metricStatus) {
        this.metricStatus = metricStatus;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    public boolean isPlayer1Ready() {
        return player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }

    public long getPlayer1Point() {
        if (metricStatus == null || metricStatus.isEmpty())
            return 0L;
        return metricStatus.stream().filter(n -> n != null && n.intValue() == 1).count();
    }

    public long getPlayer2Point() {
        if (metricStatus == null || metricStatus.isEmpty())
            return 0L;
        return metricStatus.stream().filter(n -> n != null && n.intValue() == 2).count();
    }
}

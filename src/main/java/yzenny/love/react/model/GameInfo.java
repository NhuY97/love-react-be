package yzenny.love.react.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameInfo implements Serializable {
    private static final long serialVersionUID = 5535085277413083748L;

    private String roomId;
    private String player1;
    private String player2;
    private boolean player1Ready;
    private boolean player2Ready;
    private int status;
    private Map<Integer, Integer> metric;
    private int currentNumber;

    public GameInfo() {
    }

    public GameInfo(String player1) {
        this.player1 = player1;
        this.roomId = Base64.getEncoder().encodeToString((UUID.randomUUID().toString()).getBytes(StandardCharsets.UTF_8)) + "bhZGTS7hNY";
        this.roomId = this.roomId.substring(0, 15);
        this.status = GameStatus.CREATED.getStatus();
        this.currentNumber = 0;
        this.metric = generateMetric();
    }

    public void player2Joined(String player2) {
        this.player2 = player2;
        this.status = GameStatus.ALLOW_READY.getStatus();
    }

    private static Map<Integer, Integer> generateMetric() {
        List<Integer> keys = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        Collections.shuffle(keys);
        Map<Integer, Integer> map = new LinkedHashMap<>();
        keys.forEach(number -> map.put(number, NumberStatus.AVAILABLE.getStatus()));
        return map;
    }

    public static void main(String[] args) {
        generateMetric();
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

    public Map<Integer, Integer> getMetric() {
        return metric;
    }

    public void setMetric(Map<Integer, Integer> metric) {
        this.metric = metric;
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

    @Override
    public String toString() {
        return "GameInfo{" +
                "roomId='" + roomId + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", status=" + status +
                ", metric=" + metric +
                ", currentNumber=" + currentNumber +
                '}';
    }
}

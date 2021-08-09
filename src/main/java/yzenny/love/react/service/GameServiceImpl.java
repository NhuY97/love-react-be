package yzenny.love.react.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import yzenny.love.react.model.GameInfo;

import java.util.concurrent.TimeUnit;

@Service
public class GameServiceImpl implements GameService {
    Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    private static final String ROOM_KEY_PREFIX = "ROOM_";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String createNewGame(String username) throws JsonProcessingException {
        GameInfo gameInfo = new GameInfo(username);
        redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + gameInfo.getRoomId(), objectMapper.writeValueAsString(gameInfo), 1, TimeUnit.HOURS);
        log.info("Redis save new room with data {}", gameInfo);
        return gameInfo.getRoomId();
    }

    @Override
    public GameInfo getGameInfo(String roomId) {
        try {
            Object rawData = redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + roomId);
            log.info("Redis get roomId {} with data {}", roomId, rawData);
            return objectMapper.readValue(rawData.toString(), GameInfo.class);
        } catch (Exception ex) {
            log.error("Redis get roomId {}, exception {}", roomId, ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean saveGameInfo(GameInfo gameInfo) {
        try {
            redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + gameInfo.getRoomId(), objectMapper.writeValueAsString(gameInfo), 1, TimeUnit.HOURS);
            return true;
        } catch (JsonProcessingException e) {
            log.error("Redis save gameInfo {} parse fail!", gameInfo);
        }
        return false;
    }

    @Override
    public boolean deleteGameInfo(String roomId) {
        Boolean rs = redisTemplate.delete(ROOM_KEY_PREFIX + roomId);
        return rs != null && rs;
    }
}

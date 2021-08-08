package yzenny.love.react.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import yzenny.love.react.model.UserInfo;

import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

@Service
public class UserServiceImpl implements UserService {
    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_KEY_PREFIX = "USER_";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean checkIfUserExisted(String username) {
        Boolean rs = redisTemplate.hasKey(USER_KEY_PREFIX + username);
        return ofNullable(rs).orElse(false);
    }

    @Override
    public UserInfo getUser(String username) {
        if (checkIfUserExisted(username)) {
            Object roomId = redisTemplate.opsForValue().get(USER_KEY_PREFIX + username);
            log.info("Redis get username `{}` value `{}`", username, roomId);
            return new UserInfo(username, ofNullable(roomId).map(Object::toString).orElse(""));
        }
        return null;
    }

    @Override
    public UserInfo saveUser(String username, String roomId) {
        redisTemplate.opsForValue().set(USER_KEY_PREFIX + username, ofNullable(roomId).orElse(""), 1, TimeUnit.HOURS);
        return getUser(username);
    }

    @Override
    public void deleteUser(String username) {
        redisTemplate.delete(USER_KEY_PREFIX + username);
    }
}

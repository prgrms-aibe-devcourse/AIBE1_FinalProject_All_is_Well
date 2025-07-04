package org.example.bookmarket.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 데이터를 Redis에 저장합니다. (유효 시간 설정 가능)
     *
     * @param key      저장할 키
     * @param value    저장할 값
     * @param duration 유효 시간
     */
    public void setData(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 키에 해당하는 데이터를 Redis에서 가져옵니다.
     *
     * @param key 조회할 키
     * @return 조회된 데이터 (없으면 null)
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 키에 해당하는 데이터를 Redis에서 삭제합니다.
     *
     * @param key 삭제할 키
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
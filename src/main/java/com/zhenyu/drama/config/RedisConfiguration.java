package com.zhenyu.drama.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 RedisTemplate 对象...");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置 Redis 连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置 Redis Key 的序列化器（使用字符串序列化，方便查看）
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置 Redis Value 的序列化器（使用默认的 JdkSerializationRedisSerializer）
        // 如果需要 JSON 序列化，可以改为 Jackson2JsonRedisSerializer
        return redisTemplate;
    }
}

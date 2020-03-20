/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.configuration;

import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @author nhungtt
 */
@Configuration
@EnableRedisRepositories
public class RedisConfiguration {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String redisHost = ConfigLoader.getMainConfig().getString(Constant.REDIS_HOST);
        Integer redisPort = ConfigLoader.getMainConfig().getInt(Constant.REDIS_PORT);
        String redisPassword = ConfigLoader.getMainConfig().getString(Constant.REDIS_PASSWORD);
        LettuceConnectionFactory lecttuceConFactory = new LettuceConnectionFactory(redisHost, redisPort);
        lecttuceConFactory.setPassword(redisPassword);
        lecttuceConFactory.setDatabase(3);
        return lecttuceConFactory;
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}

package net.sasakonnect.wallet.tools.security;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {
	@Autowired 
	 RedisTemplate<String, Boolean> redisTemplate;
	
	public Boolean isBlocked(String keyAndMethod) {
		return redisTemplate.hasKey(keyAndMethod);
	}
	 public void track(String keyAndMethod, Long rateLimitDurationSeconds) {
	        redisTemplate.opsForValue().set(keyAndMethod, true, rateLimitDurationSeconds, TimeUnit.SECONDS);
	    }

}

package net.sasakonnect.wallet.beans;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisBean<T extends Serializable> {
	private final RedisTemplate<String, T> redisTemplate;

	public RedisBean(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Optional<T> getRecord(String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));

		// TODO Auto-generated method stub

	}

	public void storeRecord(String key, T value) {
		ValueOperations<String, T> valueOps = redisTemplate.opsForValue();
		valueOps.set(key, value, 5, TimeUnit.MINUTES);

		// TODO Auto-generated method stub

	}
}

package net.sasakonnect.wallet.tools.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobProducer<T extends Queueable> {
	@Value("${spring.profiles.active:production}:jobQueue")
	String queueName;

	private final RedisTemplate<String, T> redisTemplate;

	public JobProducer(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void enqueueJob(T job) {
		redisTemplate.opsForList().leftPush(queueName, job);
	}

	public void enqueueJob(String name, T job) {
		redisTemplate.opsForList().leftPush(name, job);
	}
}

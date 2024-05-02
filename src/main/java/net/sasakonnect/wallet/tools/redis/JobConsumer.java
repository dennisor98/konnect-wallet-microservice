package net.sasakonnect.wallet.tools.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobConsumer<T extends Queueable<T>> {

	@Value("${spring.profiles.active:production}:jobQueue")
	String queueName;

	private final RedisTemplate<String, T> redisTemplate;

	public JobConsumer(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Scheduled(fixedRate = 1000) // Adjust the rate (in milliseconds) as needed
	public void processJobs() {
		Queueable<T> queue = redisTemplate.opsForList().rightPop(queueName);
		if (queue != null) {
			log.info("Picking a job with tag " + queueName);

			// Process the job
			queue.executeJob();
		}
		Queueable<T> firebaseQueue = redisTemplate.opsForList().rightPop("firebase");
		if (firebaseQueue != null) {
			System.out.println("Job exist");

			// Process the job
			firebaseQueue.executeJob();
		}
	}
}

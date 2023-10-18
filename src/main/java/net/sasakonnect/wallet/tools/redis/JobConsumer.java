package net.sasakonnect.wallet.tools.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobConsumer<T extends Queueable<T>> {

	private final RedisTemplate<String, T> redisTemplate;

	public JobConsumer(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Scheduled(fixedRate = 1000) // Adjust the rate (in milliseconds) as needed

	public void processJobs() {
		Queueable<T> queue = redisTemplate.opsForList().rightPop("jobQueue");
		if (queue != null) {
			System.out.println("Job exist");

			// Process the job
			queue.executeJob(queue);
		}
		Queueable<T> firebaseQueue = redisTemplate.opsForList().rightPop("firebase");
		if (firebaseQueue != null) {
			System.out.println("Job exist");

			// Process the job
			firebaseQueue.executeJob(firebaseQueue);
		}
	}
}

package net.sasakonnect.wallet.tools.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobConsumer<T extends Queueable> {

	private final RedisTemplate<String, T> redisTemplate;

	public JobConsumer(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Scheduled(fixedRate = 1000) // Adjust the rate (in milliseconds) as needed

	public void processJobs() {
		Queueable job = redisTemplate.opsForList().rightPop("jobQueue");
		if (job != null) {
			System.out.println("Job exist");

			// Process the job
			job.executeJob(job);
		}
	}
}

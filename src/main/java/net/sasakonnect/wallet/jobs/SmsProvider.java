package net.sasakonnect.wallet.jobs;

import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import net.sasakonnect.wallet.tools.redis.Queueable;

@Data
@PropertySource("classpath:application.properties")

public abstract class SmsProvider extends Queueable<String> {

	private static final long serialVersionUID = 1L;
	String template;
	String phoneNumber;
	Integer retryCount = 0;

}

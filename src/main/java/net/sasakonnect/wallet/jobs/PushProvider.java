package net.sasakonnect.wallet.jobs;

import java.util.List;

import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import net.sasakonnect.wallet.tools.redis.Queueable;

@Data
@PropertySource("classpath:application.properties")

public abstract class PushProvider<T> extends Queueable<T> {

	private static final long serialVersionUID = 1L;
	String template;
	List<String> tokens;
	Integer retryCount = 0;

}



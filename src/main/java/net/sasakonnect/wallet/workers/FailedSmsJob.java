package net.sasakonnect.wallet.workers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.jobs.SmsProvider;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedSmsJob {
	private SmsProvider failedSmsProvider;
	private String messageTemplate;
	private String phoneNumber;
	@Builder.Default
	int retryCount = 1;
}
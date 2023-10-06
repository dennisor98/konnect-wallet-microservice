package net.sasakonnect.wallet.notification;

import lombok.Data;

@Data
public class NotificationResult<T> {
	private String requestId;
	private String sender;
	private String locale;
	private long timestamp;
	private String notificationType;
	private String salt;
	private String signature;
	private T params;

}

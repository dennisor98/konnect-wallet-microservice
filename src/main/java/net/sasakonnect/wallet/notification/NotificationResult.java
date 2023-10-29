package net.sasakonnect.wallet.notification;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class NotificationResult<T> {
	private String requestId;
	private String sender;
	private String locale;
	private long timestamp;
	@SerializedName("notificationType")

	public String notificationType;
	private String salt;
	private String signature;
	private T params;

}

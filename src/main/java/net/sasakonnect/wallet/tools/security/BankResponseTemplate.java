package net.sasakonnect.wallet.tools.security;

import java.util.Map;

import lombok.Data;

@Data
class BankResponseTemplate {
	private String code;
	private String msg;
	private String sender;
	private String requestId;
	private String locale;
	private Map<String, Object> data;
	private long timestamp;
	private String salt;
	private String signature;
}
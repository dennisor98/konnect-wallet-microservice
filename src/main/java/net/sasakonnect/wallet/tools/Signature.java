package net.sasakonnect.wallet.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.tools.security.SignRequest;

@Component
public class Signature {

	private int length = 30;
	private String privateKey = "";
	private String sender = "";
	private String salt;
	private SignRequest request;
	private boolean isBuild = false;
	private String requestId;

	public String getRandomSalt() {
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[length / 2];
		random.nextBytes(saltBytes);
		salt = Hex.encodeHexString(saltBytes).substring(0, length);
		return salt;
	}

	public String getRandomId() {
		SecureRandom random = new SecureRandom();
		byte[] idBytes = new byte[length / 2];
		random.nextBytes(idBytes);
		requestId = Hex.encodeHexString(idBytes).substring(0, length);
		return requestId;
	}

	public Signature addPrivateKey(String privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public Signature addSender(String sender) {
		this.sender = sender;
		return this;
	}

	public String flattenJson(SignRequest request) {
		List<String> keyValuePairs = new ArrayList<>();
		for (Map.Entry<String, Object> entry : request.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			keyValuePairs.add(key + "=" + value);
		}
		Collections.sort(keyValuePairs);
		return String.join("&", keyValuePairs);
	}

	public String hash256() {
		if (!isBuild) {
			throw new IllegalStateException("Request has not been built.");
		}
		String flattened = flattenJson(request);
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(flattened.getBytes());
			return Hex.encodeHexString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not available.", e);
		}
	}

	public SignRequest build(SignRequest request) {
		getRandomSalt();
		getRandomId();
		long timestamp = System.currentTimeMillis();
		request.setSalt(salt);
		request.setPrivateSenderKey(privateKey);
		request.setSender(sender);
		request.setTimestamp(timestamp);
		request.setRequestId(requestId);
		this.request = request;
		isBuild = true;

		String signature = hash256();
		request.setSignature(signature);

		return request;
	}

	public boolean verify(SignRequest request) {
		addPrivateKey(privateKey);
		addSender(sender);
		request.setSender(sender);
		request.setPrivateSenderKey(privateKey);
		request.setSalt(salt);

		String signature = request.getSignature();
		request.setSignature(null); // Remove signature for hashing

		String computedHash = hash256();

		return Objects.equals(signature, computedHash);
	}

	public static String createHashFrom(String message) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(message.getBytes());

			// Convert the hash to a hex string
			StringBuilder hexString = new StringBuilder();
			for (byte hashByte : hashBytes) {
				String hex = Integer.toHexString(0xff & hashByte);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not available", e);
		}
	}

	public static String createBase64StringFromHash(String hash) {
		byte[] hashBytes = hexStringToByteArray(hash);
		return Base64.getEncoder().encodeToString(hashBytes);
	}

	private static byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

}

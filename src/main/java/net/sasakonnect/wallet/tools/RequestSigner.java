package net.sasakonnect.wallet.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RequestSigner {
	@Value("${privateKey}")
	private String privateKey;
	@Value("${CHOICE_SENDER}")
	private String choiceSender;

	public String signRequest(Map<String, Object> data) {
		try {
			Map<String, Object> request = new HashMap<>();

			request.put("requestId", "APPREQ00990320fed02000");
			request.put("sender", choiceSender);
			request.put("locale", "en_KE");
			request.put("timestamp", System.currentTimeMillis());
			request.put("params", data);

			// Step 1: Generate Salt
			request.put("salt", "QcEwsZ123da");

			// Step 2: Add Private Key
			request.put("senderKey", privateKey);

			// Step 3: Flatten and Sort JSON
			String flattened = flattenAndSortJSON(request);
			System.out.println(flattened);
			// Step 4: Hash the String with SHA-256
			String signature;

			signature = calculateSHA256Hash(flattened);
			request.put("signature", signature);
			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
			request.remove("senderKey");
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	private String flattenAndSortJSON(Map<String, Object> obj) {
		List<String> sortedKeys = obj.keySet().stream().sorted().collect(Collectors.toList());

		List<String> keyValuePairs = new ArrayList<>();
		for (String key : sortedKeys) {
			Object value = obj.get(key);
			if (value instanceof Map) {
				Map<String, Object> nestedMap = (Map<String, Object>) value;
				String nestedQueryString = flattenAndSortJSON(nestedMap);
				List<String> nestedKeyValues = Arrays.stream(nestedQueryString.split("&"))
						.map(nestedKey -> key + "." + nestedKey).collect(Collectors.toList());
				keyValuePairs.addAll(nestedKeyValues);
			} else if (value instanceof List) {
				List<?> listValue = (List<?>) value;
				for (int i = 0; i < listValue.size(); i++) {
					Object listItem = listValue.get(i);
					if (listItem instanceof Number) {
						keyValuePairs.add(key + "[" + i + "]=" + listItem.toString());
					} else {
						keyValuePairs.add(key + "[" + i + "]=" + listItem.toString());
					}
				}
			}

			else {
				keyValuePairs.add(key + "=" + value.toString());
			}
		}

		return String.join("&", keyValuePairs);
	}

	private static String calculateSHA256Hash(String input) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(input.getBytes());
		StringBuilder hexString = new StringBuilder();
		for (byte hashByte : hashBytes) {
			String hex = String.format("%02x", hashByte);
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String createHashFrom(String message) {
		try {
			// Create a MessageDigest instance for SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(message.getBytes(StandardCharsets.UTF_8));

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

}

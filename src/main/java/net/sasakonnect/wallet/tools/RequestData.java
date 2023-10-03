package net.sasakonnect.wallet.tools;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class RequestData {
	Gson gson = new Gson();

	private Map<String, String> params = new HashMap<>();

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public String getSenderKey() {
		return params.get("senderKey");
	}

	public void setSenderKey(String senderKey) {
		params.put("senderKey", senderKey);
	}

	public String getSalt() {
		return params.get("salt");
	}

	public void setSalt(String salt) {
		params.put("salt", salt);
	}

	public String getSignature() {
		return params.get("signature");
	}

	public void setSignature(String signature) {
		params.put("signature", signature);
	}

	public void removeSenderKey() {
		params.remove("senderKey");
	}

	// Implement toJSON() method to convert the object to a JSON string
	public String toJSON() {
		// Convert 'params' map to JSON representation
		// You can use a JSON library like Gson to do this
		// Example:
		Gson gson = new Gson();
		return gson.toJson(params);

	}
}

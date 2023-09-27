package net.sasakonnect.wallet.tools.security;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor

public class SignRequest implements Map<String, Object> {
	private String requestId;
	private String locale;
	private Map<String, Object> params;
	private long timestamp;
	private String salt;
	private String sender;
	private String privateSenderKey;
	private String signature;

//	public void setTimestamp(long timestamp) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void setSalt(String salt) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void setSenderKey(String privateKey) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void setSender(String sender) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public int size() {
		return params.size();
	}

	@Override
	public boolean isEmpty() {
		return params.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return params.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return params.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		if (key.equals("requestId")) {
			return requestId;
		} else if (key.equals("locale")) {
			return locale;
		} else {
			return params.get(key);
		}
	}

	@Override
	public Object put(String key, Object value) {
		if (key.equals("requestId")) {
			String oldValue = requestId;
			requestId = (String) value;
			return oldValue;
		} else if (key.equals("locale")) {
			String oldValue = locale;
			locale = (String) value;
			return oldValue;
		} else {
			return params.put(key, value);
		}
	}

	@Override
	public Object remove(Object key) {
		if (key.equals("requestId")) {
			String oldValue = requestId;
			requestId = null;
			return oldValue;
		} else if (key.equals("locale")) {
			String oldValue = locale;
			locale = null;
			return oldValue;
		} else {
			return params.remove(key);
		}
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		requestId = null;
		locale = null;
		params.clear();
	}

	@Override
	public Set<String> keySet() {
		Set<String> keys = new HashSet<>();
		keys.add("requestId");
		keys.add("locale");
		keys.addAll(params.keySet());
		return keys;
	}

	@Override
	public Collection<Object> values() {
		Collection<Object> values = new ArrayList<>();
		values.add(requestId);
		values.add(locale);
		values.addAll(params.values());
		return values;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		Set<Entry<String, Object>> entries = new HashSet<>();
		entries.add(new AbstractMap.SimpleEntry<>("requestId", requestId));
		entries.add(new AbstractMap.SimpleEntry<>("locale", locale));
		entries.addAll(params.entrySet());
		return entries;
	}

}
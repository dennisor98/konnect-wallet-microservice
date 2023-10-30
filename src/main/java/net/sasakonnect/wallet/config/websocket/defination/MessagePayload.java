package net.sasakonnect.wallet.config.websocket.defination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class MessagePayload {
	public String senderId;
	public String receiverId;
	public MessageType messageType;
	public String message;

	public MessagePayload buildFromJson(ObjectNode jsonObject) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(jsonObject);
			this.messageType = MessageType.BIN_JSON;
			message = jsonString;
			return this;

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public void messageType(MessageType messageType) throws Exception {
		if (this.messageType != null) {
			throw new Exception("Message Typel aready set");
		} else {
			this.messageType = messageType;
		}
	}

	public byte[] toBytes() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode jsonObject = objectMapper.createObjectNode();
		jsonObject.put("senderId", senderId);
		jsonObject.put("receiverId", receiverId);
		jsonObject.put("messageType", messageType.toString());

		// Encode the message field (byte array) as Base64 and include it in the JSON
		jsonObject.put("message", message);

		// Convert the entire object to JSON
		try {
			String jsonString = objectMapper.writeValueAsString(jsonObject);

			// Encode the JSON as bytes
			return jsonString.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public MessagePayload buildFromJson(String jsonString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ObjectNode jsonObject = objectMapper.readValue(jsonString, ObjectNode.class);

			this.senderId = jsonObject.get("senderId").asText();
			this.receiverId = jsonObject.get("receiverId").asText();
			this.messageType = MessageType.valueOf(jsonObject.get("messageType").asText());

			// Decode the Base64 message field to a byte array
			this.message = jsonObject.get("message").asText();

			return this;
		} catch (JsonProcessingException e) {
			throw e;

		} catch (Exception e) {
			throw e;
		}
	}
}

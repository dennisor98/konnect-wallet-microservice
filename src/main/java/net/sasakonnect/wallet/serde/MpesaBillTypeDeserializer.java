package net.sasakonnect.wallet.serde;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import net.sasakonnect.wallet.RequestDto.MpesaBillType;

public class MpesaBillTypeDeserializer extends JsonDeserializer<MpesaBillType> {
	@Override
	public MpesaBillType deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		String value = node.asText();

		// Customize this logic to handle unknown values as needed
		if ("PAYBILL".equals(value)) {
			return MpesaBillType.PAY_BILL;
		} else if ("TILL".equals(value)) {
			return MpesaBillType.TILL;
		} else {
			// Handle unknown values here, e.g., return a default value or throw an
			// exception
			return MpesaBillType.PAY_BILL;
		}
	}
}
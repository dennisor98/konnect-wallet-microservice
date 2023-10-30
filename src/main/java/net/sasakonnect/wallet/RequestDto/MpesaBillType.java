package net.sasakonnect.wallet.RequestDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sasakonnect.wallet.serde.MpesaBillTypeDeserializer;

@JsonDeserialize(using = MpesaBillTypeDeserializer.class)

public enum MpesaBillType {
	TILL(1), PAY_BILL(0);

	int code;

	MpesaBillType(int i) {
		this.code = i;

		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return this.code;
	}
}

package net.sasakonnect.wallet.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {
	private String code;
	private String msg;
	private String sender;
	private String requestId;
	private String locale;
	private TxtData data;
	private double timestamp;
	private String salt;
	private String signature;

	public static class TxtData {
		public String txId;

	}
}

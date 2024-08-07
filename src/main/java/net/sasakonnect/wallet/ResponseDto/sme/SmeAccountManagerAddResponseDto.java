package net.sasakonnect.wallet.ResponseDto.sme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto.TxtData;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmeAccountManagerAddResponseDto {
	private String code;
	private String msg;
	private String sender;
	private String requestId;
	private String locale;
	private ResponseData data;
	private double timestamp;
	private String salt;
	private String signature;
	
	
	public class ResponseData  {
		public String applicationId;
	}
}

package net.sasakonnect.wallet.ResponseDto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementDTO {
	private String jobId;
    private String statementUrl;
}

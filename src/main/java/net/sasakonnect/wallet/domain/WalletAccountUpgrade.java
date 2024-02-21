package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletAccountUpgrade extends BaseWalletDomain implements Serializable {
	private String userId;
	private String onboardingRequestId;
	private Integer status;
	private String accountId;
	private String accountType;
	private Long completeTime;
	private List<String> rejectionReasonIds;
	private List<String> rejectionReasonMsgs;
}

package net.sasakonnect.wallet.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.FinancialContactType;
@Entity

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialContact extends BaseWalletDomain implements Serializable  {

	private static final long serialVersionUID = -8524060173356940591L;
	@Column
	String txType;
	
	@Column()
	String oppoChannelId;
	
	@Column()
	String oppoBankCode;
	
	
	@Column
	String accountId;
	
	@Column(nullable=false)
	String oppoAccountId;
	
	@Column(nullable=true)
	String oppoSubAccountId;
	
	@Column
    String oppoAccountName;
    
}
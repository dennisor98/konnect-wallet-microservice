package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UtilityDetails extends BaseWalletDomain{
	@Column()
	String txId;

	@Column()
	String provider;
	
	@Column()
	String  accountId;

	@Column()
	String oppoAccountId;
	
	@Column()
	String oppoSubAccountId;

}

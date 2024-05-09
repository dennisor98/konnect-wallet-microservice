package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.PinResetType;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinresetIssues extends BaseWalletDomain{
	@ManyToOne()
	@JoinColumn(name = "requester_user_id", nullable = true)
    User requesterId;
	
	@ManyToOne()
	@JoinColumn(name="account_owner_id")
	User accountOwner;
	
	
	@Column()
	String accountId;
	
	@Column()
	float validationScore;
	
	@Column()
	String approverLarkOpenId;
	
	@Column()
	PinResetType resetReason;
	
}

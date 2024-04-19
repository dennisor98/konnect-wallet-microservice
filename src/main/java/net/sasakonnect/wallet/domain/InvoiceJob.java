package net.sasakonnect.wallet.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceJob extends BaseWalletDomain{

	@Column()
	Date startDate;
	
	@Column()
	Date endDate;
		
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requester_user_id", nullable = true)
	private User jobOwner;
	
	@Column(nullable = true, columnDefinition = "TEXT")
	private String description;
	
	@Column(nullable=true)
	String downloadLink;

}


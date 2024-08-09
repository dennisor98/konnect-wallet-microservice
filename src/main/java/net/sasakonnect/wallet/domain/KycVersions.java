package net.sasakonnect.wallet.domain;

import java.util.Date;

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
public class KycVersions extends BaseWalletDomain{
	@Column
   Integer version;
	
	@Column
	Date startDate;
   
	@Column
   Date dateLine;
}

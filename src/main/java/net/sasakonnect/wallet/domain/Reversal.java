package net.sasakonnect.wallet.domain;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="reversal_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reversal extends BaseWalletDomain{
	   String accountNumber;
	   
	   String mobileNumber;
	   
	   String reference;
	   
	   String description;
}

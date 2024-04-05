package net.sasakonnect.wallet.domain.invoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceItem {
	int count;
	String description;
	String noOfTransaction;
	double amount;
	double tax;
	

}

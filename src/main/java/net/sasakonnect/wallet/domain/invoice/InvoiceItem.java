package net.sasakonnect.wallet.domain.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.Transaction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {
	@Builder.Default
	Tariff tariff = new Tariff();
	@Builder.Default
	Transaction transaction = new Transaction();
	int count;
	String description;
	String noOfTransaction;
	double amount;
	double tax;
	

}

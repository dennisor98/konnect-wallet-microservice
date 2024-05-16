package net.sasakonnect.wallet.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class MerchantStkPush extends SdkSearchCustomer {
	int amount;

}

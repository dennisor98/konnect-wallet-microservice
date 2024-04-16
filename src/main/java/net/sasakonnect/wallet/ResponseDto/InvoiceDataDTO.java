package net.sasakonnect.wallet.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.constant.ChannelType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDataDTO {
	Double amount;
    Double tax;
    Integer count;
    Integer noOfTransaction;
    String description;
}

package net.sasakonnect.wallet.RequestDto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class WalletRank {
	String account_id;
	String name;
	Integer transaction_count;
	Integer totalAmount;
}


package net.sasakonnect.wallet.domain;

import lombok.Data;

@Data
public class ExtInfo {
	public String externalTxId;
    public String oppoPhoneNumber;
    public String counterpartyName;
    public String thirdPartyTxType;
    public String transactionNarrative;
}

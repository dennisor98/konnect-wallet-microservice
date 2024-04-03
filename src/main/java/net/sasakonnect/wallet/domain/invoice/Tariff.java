package net.sasakonnect.wallet.domain.invoice;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.BaseWalletDomain;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tariff extends BaseWalletDomain implements Serializable {
    private static final long serialVersionUID = 10000007L;
	private String tier;
    private double channelCharge;
    private double cbCharge;
    private double totalCostExclToPartner;
    private double totalPartnerProfit;
    private double totalMarginTaxable;
    private double exciseDutyTax;
    private double totalCostToUserIncl;
    private double  savedValue;
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;
    @ElementCollection
    private List<String> opponentAccount;
    private String tierLabel;
    private int min;
    private int max;
   
}

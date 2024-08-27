package net.sasakonnect.wallet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientAppScopes extends BaseWalletDomain{
  @ManyToOne
  @JoinColumn(name="client_id")
  WalletClient client;
  
  String appKey;
  
}

package net.sasakonnect.wallet.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorporatePin extends BaseWalletDomain {
  @Column()
  String pin;
  
  Integer wrongAttempts;
  
  @OneToOne()
  @JoinColumn(name="user_id")
  CorporateDetails user;
  
}

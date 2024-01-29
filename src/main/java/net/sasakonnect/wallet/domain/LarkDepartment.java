package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LarkDepartment extends BaseWalletDomain{
  @Column()
  String departmentId;
  
  @Column(unique=true,nullable=false)
  String departmentName;
  
}

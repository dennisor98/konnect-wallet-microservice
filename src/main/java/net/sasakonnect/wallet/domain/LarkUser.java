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
public class LarkUser extends BaseWalletDomain {
  @Column()
  String name;
  
  @Column(unique=true)
  String openId;
  
  @Column()
  String departmentId;
  
  @Column()
  String avartarUrl;
  
  @Column()
  String mobileNumber;
  
  @Column()
  String email;
}
package net.sasakonnect.wallet.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsRead extends BaseWalletDomain{
  @ManyToOne()
  @JoinColumn(name="user_id")
  User user;
  
  @ManyToOne()
  @JoinColumn(name="message_id")
  Notifications message;
}

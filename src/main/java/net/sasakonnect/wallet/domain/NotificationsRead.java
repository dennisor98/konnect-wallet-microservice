package net.sasakonnect.wallet.domain;


import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
@IdClass(NotificationReadId.class)
public class NotificationsRead extends BaseWalletDomain {
  @ManyToOne()
  @JoinColumn(name="user_id")
  User user;
  
  @ManyToOne()
  @JoinColumn(name="message_id")
  Notifications message;
  

}

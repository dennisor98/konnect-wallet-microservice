package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.NotificationTargetType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notifications extends BaseWalletDomain {
	@Column()
    String title;
	
	@Column()
	String message;
	
	@ManyToOne()
	@JoinColumn(name = "user_id")
	User targetUser;
	
	@Column()
	String targetType;
	
	@Column()
	Boolean isRead;
	
	
  
}

package net.sasakonnect.wallet.domain;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	
	@Column(columnDefinition = "LONGTEXT")
	String message;
	
	@ManyToOne()
	@JoinColumn(name = "user_id")
	User targetUser;
	
	@Column()
	String targetType;
	
	@OneToMany(mappedBy="message")
	List<NotificationsRead> messageRead;
	
	@Column()
	Date expiryDate;
   
}

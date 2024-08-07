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
import jakarta.persistence.PrePersist;
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
	@JoinColumn(name = "user_id",nullable=true)
	User targetUser;
	
	@Column()
	String targetType;
	
	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'text'")
    private String contentType;
	
	@OneToMany(mappedBy="message")
	List<NotificationsRead> messageRead;
	
	@Column()
	Date expiryDate;
	
	@Column()
	String caption;
	
	 @PrePersist
	    protected void onPrePersist() {
	        if (this.contentType == null) {
	            this.contentType = "text";
	        }
	    }
	 

	    public String getContentType() {
	        return contentType;
	    }

	    public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
   
}

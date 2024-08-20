package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
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
public class UserKycDoc extends BaseWalletDomain {
	@ManyToOne
	@JoinColumn(name="user_id",nullable=false)
	User user;
	
	@Column
	String parentFolder;
	
	@Column
	String idFrontUrl;
	
	@Column
	String idBackUrl;
	
	@Column
	String selfieUrl;
	
}
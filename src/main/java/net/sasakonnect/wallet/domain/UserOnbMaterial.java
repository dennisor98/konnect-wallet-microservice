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
public class UserOnbMaterial extends BaseWalletDomain {
	@ManyToOne
	@JoinColumn(name="user_id",nullable=true)
	User user;
	
	@Column
	String idNumber;
	
	@Column
	String idFrontUrl;
		
	@Column
	String selfieUrl;
}

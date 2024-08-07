package net.sasakonnect.wallet.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppVersions  extends BaseWalletDomain {
	@Column
	String version;

	@Column(columnDefinition = "LONGTEXT")
	String description;
	
	@Column
	Date updateDateline;
	
	
}

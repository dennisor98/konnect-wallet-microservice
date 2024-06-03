package net.sasakonnect.wallet.domain.sme;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;

@Entity
@Data
@Builder
@AllArgsConstructor()
@NoArgsConstructor()
public class Enterprise extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = -4463864574321745687L;
	@OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
	private List<Sme> smeAccounts;

	@OneToOne
	@JoinColumn(name = "creator_id")
	private User creator;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)

	private String ownership;
	@Column(nullable = false)

	private String industry;
	@Column(nullable = false)

	private String mission;
	@Column(nullable = false)

	private String vision;
}

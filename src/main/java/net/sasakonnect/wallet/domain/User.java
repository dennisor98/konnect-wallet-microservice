package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.EmploymentStatus;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.IdType;
import net.sasakonnect.wallet.enums.MonthlyIncome;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseWalletDomain implements Serializable, UserDetails {
	Set<SimpleGrantedAuthority> simple = new HashSet<SimpleGrantedAuthority>();
	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false, unique = true)
	private String openId;

	@PrePersist
	private void generateOpenId() {
		if (this.openId == null) {
			// Generate your formatted openId here
			this.openId = generateFormattedOpenId();
		}
	}

	@Column
	private String middleName;

	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = true, length = 50)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false)
	private int countryCode;

	@Column(nullable = false, unique = true, length = 50)
	private String mobile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IdType idType;

	@Column(nullable = false, unique = true, length = 50)
	private String idNumber;

	@OneToOne
	@JoinColumn(name = "corporate_id", referencedColumnName = "id", nullable = true)
	private CorporateDetails corporate;

	@Column(length = 50, unique = true)
	private String onboardingRequestId;

	@Column(nullable = false)
	private Date birthday;

	@Column(nullable = true, length = 50)
	private String kraPin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private EmploymentStatus employmentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private MonthlyIncome monthlyIncome;

	@OneToOne
	@JoinColumn(name = "meta_id")
	private MetaMask metaMask;

	@OneToMany(mappedBy = "user") // This 'user' should match the field name in UserWallet
	private List<UserWallet> userWallets;

	@OneToMany(mappedBy = "user")
	private List<UserPin> pins;

	@OneToMany(mappedBy = "user")
	private List<FirebaseToken> firebaseTokens;

	@ManyToMany(mappedBy = "user_notified")
	private List<Notification> notifications;

	@OneToOne(mappedBy = "user")
	private UserRole userRole;

	@OneToMany(mappedBy = "user")
	private List<UserDevice> userDevices;
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<UserPin> userPins;
	
	@Column(nullable=true)
	private String status;

	public Map<String, Object> toBankPayload() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("userId", this.id);
		payload.put("firstName", this.firstName);
		payload.put("middleName", this.middleName);
		payload.put("lastName", this.lastName);
		payload.put("address", this.address);
		payload.put("gender", this.gender.toString());
		payload.put("countryCode", this.countryCode);
		payload.put("mobile", this.mobile);
		payload.put("idNumber", this.idNumber);
		payload.put("idType", this.idType.toString());
		payload.put("birthday", this.birthday);
		payload.put("kraPin", this.kraPin);
		payload.put("employmentStatus", this.employmentStatus.toString());
		payload.put("monthlyIncome", this.monthlyIncome.toString());
		return payload;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return simple;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.countryCode + this.mobile;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "User{" + "openId=" + openId + "," + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='"
				+ lastName + '\'' + ", address='" + address + '\'' +
				// Include other non-lazy attributes here
				'}';
	}

	private String generateFormattedOpenId() {
		// Implement your logic to generate the formatted openId
		// For example, using UUID.randomUUID() and formatting it
		return "w_oid" + UUID.randomUUID().toString();
	}

}
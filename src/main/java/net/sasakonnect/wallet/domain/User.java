package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import net.sasakonnect.wallet.enums.EmploymentStatus;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.IdType;
import net.sasakonnect.wallet.enums.MonthlyIncome;

@Entity
@Data
public class User extends BaseWalletDomain implements Serializable, UserDetails {
	Set<SimpleGrantedAuthority> simple = new HashSet<SimpleGrantedAuthority>();
	@Column(nullable = false)
	private String firstName;

	@Column
	private String middleName;

	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = false, length = 50)
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

	@Column(length = 50, unique = true)
	private String onboardingRequestId;

	@Column(nullable = false)
	private Date birthday;

	@Column(nullable = false, length = 50)
	private String kraPin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EmploymentStatus employmentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MonthlyIncome monthlyIncome;

	@OneToOne
	@JoinColumn(name = "meta_id")
	private MetaMask metaMask;

	@OneToMany(mappedBy = "user")
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

}
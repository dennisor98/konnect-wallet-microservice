package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.LogTypes;
import net.sasakonnect.wallet.enums.MonthlyIncome;

@Entity()
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logs extends BaseWalletDomain implements Serializable{

	private static final long serialVersionUID = -1466182051674693633L; 
   @Column(nullable = true, columnDefinition = "TEXT")
	private String description;
   
   @Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private LogTypes activity;
}

package net.sasakonnect.wallet.domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity()
public class ProfileImage extends BaseWalletDomain {
	@Column()
	private String name;
	@Column()
    private String type;
	@Column()
    private String filePath;
	
}

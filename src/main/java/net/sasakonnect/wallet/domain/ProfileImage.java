package net.sasakonnect.wallet.domain;
import jakarta.persistence.Entity;
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
	private String name;
    private String type;
    private String filePath;
}

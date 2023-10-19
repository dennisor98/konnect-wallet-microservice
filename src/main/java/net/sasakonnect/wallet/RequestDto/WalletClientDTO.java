package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WalletClientDTO {
	@NotNull(message = "appName is required")
	private String appName;

	@NotNull(message = "appDescription is required")
	private String appDescription;
	@Schema(hidden = true)
	private String appKey;
	@Schema(hidden = true)

	private String appSecret;
	private Boolean enabled;

	@Size(max = 255)
	private String callBackUrl;

	private Long walletClientAccountId;
}

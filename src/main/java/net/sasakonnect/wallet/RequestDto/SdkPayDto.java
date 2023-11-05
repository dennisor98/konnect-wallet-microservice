package net.sasakonnect.wallet.RequestDto;

import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SdkPayDto {
	String amount;
	@Builder.Default
	private Map<String, Object> payload = Collections.emptyMap();
}

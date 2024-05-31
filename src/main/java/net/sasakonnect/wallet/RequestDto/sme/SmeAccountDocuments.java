package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.sasakonnect.wallet.enums.sme.SmeDocumentContentType;
import net.sasakonnect.wallet.enums.sme.SmeDocumentMediaType;

@Data
public class SmeAccountDocuments {
	@NotBlank(message = "Onboarding request ID must not be blank")
	private String onboardingRequestId;

	@NotBlank(message = "Base64 document must not be blank")
	private String base64Document;

	@NotNull(message = "Media type must not be null")
	private SmeDocumentMediaType mediaType;

	@NotNull(message = "Content type must not be null")
	private SmeDocumentContentType contentType;

}
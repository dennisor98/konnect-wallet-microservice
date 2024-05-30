package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.sme.SmeDocumentContentType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LlcSmeMemberDto {
	@NotNull(message = "user_id must not be null")

	String user_id;
	@NotNull(message = "sme_id must not be null")

	String sme_id;
	@NotNull(message = "kraPin must not be null")

	String kraPin;
	@NotNull(message = "idFrontSideFileType must not be null")

	String idFrontSideFile;
	@NotNull(message = "idFrontSideFileType must not be null")
	private SmeDocumentContentType idFrontSideFileType;
	String idBackSideFile;
	@NotNull(message = "idBackSideFileType must not be null")
	private SmeDocumentContentType idBackSideFileType;
	String selfieFile;
	@NotNull(message = "selfieFileType must not be null")
	private SmeDocumentContentType selfieFileType;
	String kraPinFile;
	@NotNull(message = "kraPinFileType must not be null")
	private SmeDocumentContentType kraPinFileType;
}

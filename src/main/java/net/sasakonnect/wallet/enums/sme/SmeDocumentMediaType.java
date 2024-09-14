package net.sasakonnect.wallet.enums.sme;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum SmeDocumentMediaType {
	KYCF00001("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "101"),
	KYCF00002("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "101"),
	KYCF00006("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "101"),
	KYCF00009("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "KEC001"),
	KYCF00024("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "Certificate of Incorporation"),
	KYCF00011("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "CR12"),
	KYCF00010("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "KEB001"),
	KYCF00012("Optional", Arrays.asList("PDF", "JPEG", "JPG"), "CR2, CR1, CR8 - Memorandum of the Company"),
	KYCF00013("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "KRA Pin certificate"),
	KYCF00014("Mandatory", Arrays.asList("PDF", "JPEG", "JPG"), "Original Board resolution"),
	KYCF00023("Optional (unless one shareholder is a company)", Arrays.asList("PDF", "JPEG", "JPG"),
			"Ultimate Beneficiary Owner"),
	KYCF00029("Optional", Arrays.asList("PDF", "JPEG", "JPG"), "Other documents");

	private final String requirement;
	private final List<String> supportedFileTypes;
	private final String documentName;

	public String getValue() {
		return this.name();
	}

}

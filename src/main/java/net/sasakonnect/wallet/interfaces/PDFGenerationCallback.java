package net.sasakonnect.wallet.interfaces;

public interface PDFGenerationCallback {
	void onPDFGenerated(String jobId, String pdfFilePath);

	void onPDFGenerationFailed(String jobId, Exception e);

}

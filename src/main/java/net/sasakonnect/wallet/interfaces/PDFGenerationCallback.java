package net.sasakonnect.wallet.interfaces;

public interface PDFGenerationCallback {
	void onPDFGenerated(String jobId, String pdfFilePath);
	
	void sendNotification(String jobId);

	void onPDFGenerationFailed(String jobId, Exception e);

}

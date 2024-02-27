package net.sasakonnect.wallet.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.interfaces.PDFGenerationCallback;
import net.sasakonnect.wallet.repository.UserJobRepository;

@Component
public class StatementGenerationEvent implements PDFGenerationCallback {

	@Value("${statement.download}")
	private String statamentDownloadPath;

	@Autowired
	UserJobRepository userJobRepository;

	@Override
	public void onPDFGenerated(String jobId, String pdfFilePath) {
		System.out.println(pdfFilePath);
		// TODO Auto-generated method stub
		this.userJobRepository.updateDownloadLinkAndIsCompleteByJobId(jobId,
				statamentDownloadPath.toString() + "/" + jobId + ".pdf");

	}

	@Override
	public void onPDFGenerationFailed(String jobId, Exception e) {
		// TODO Auto-generated method stub

	}

}

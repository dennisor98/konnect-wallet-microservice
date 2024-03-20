package net.sasakonnect.wallet.events;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.UserJob;
import net.sasakonnect.wallet.interfaces.PDFGenerationCallback;
import net.sasakonnect.wallet.repository.UserJobRepository;
import net.sasakonnect.wallet.services.WalletService;
import net.sasakonnect.wallet.services.extensions.LarkUtilityService;

@Component
public class StatementGenerationEvent implements PDFGenerationCallback {

	@Value("${statement.download}")
	private String statamentDownloadPath;

	@Autowired
	UserJobRepository userJobRepository;
	
	@Autowired
	WalletService walletService;

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

	@Override
	public void sendNotification(String jobId) {
		this.walletService.notifyRequestedAdmin(jobId,"Statement Processing completed");
		
	}
	
	



}

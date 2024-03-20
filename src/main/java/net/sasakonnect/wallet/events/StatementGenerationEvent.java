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
import net.sasakonnect.wallet.services.extensions.LarkUtilityService;

@Component
public class StatementGenerationEvent implements PDFGenerationCallback {

	@Value("${statement.download}")
	private String statamentDownloadPath;

	@Autowired
	UserJobRepository userJobRepository;
	
	@Autowired
	LarkUtilityService larkUtilityService;

	@Override
	public void onPDFGenerated(String jobId, String pdfFilePath) {
		System.out.println(pdfFilePath);
		// TODO Auto-generated method stub
		this.userJobRepository.updateDownloadLinkAndIsCompleteByJobId(jobId,
				statamentDownloadPath.toString() + "/" + jobId + ".pdf");
		
		this.notifyRequestedAdmin(jobId,"Statement Processing completed");
	}

	@Override
	public void onPDFGenerationFailed(String jobId, Exception e) {
		this.notifyRequestedAdmin(jobId,"Statement Processing failed");
		// TODO Auto-generated method stub

	}
	
	public void notifyRequestedAdmin(String jobId,String message) {
		Optional<UserJob> userJob = this.userJobRepository.findUserJobByJobId(jobId);
		if(userJob.isPresent()) {
			var job = userJob.get();
			if(job.getIsAdmin()) {
				User user =  job.getJobOwner();
				CorporateDetails corp = user.getCorporate();
				String alertMessage = message+
						"\n**Job Id**:"+jobId
						+"\n**Download Link**:"+job.getDownloadLink();
				this.larkUtilityService.walletStatementAlert("Statement Request Alert", alertMessage,"open_id",corp.getLarkOpenId());
			}
			
		}
	}



}

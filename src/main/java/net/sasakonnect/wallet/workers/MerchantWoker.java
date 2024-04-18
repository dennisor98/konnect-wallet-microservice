package net.sasakonnect.wallet.workers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.domain.FinancialInstituation;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.repository.WalletClientAccountRepository;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;

@Component
public class MerchantWoker {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	WalletClientAccountRepository walletClientAccountRepo;
	@Autowired
	TransactionService transactionService;
	@Autowired
	private ClientAppsBean clientAppsBean;
	
	@Autowired
	UserService userService;
	
	WebClient webClient = WebClient.builder().build();

	@Async
	@Transactional
	public void notifyMerchant(Transaction transaction) {
		if (transaction.getTxType().equalsIgnoreCase("TTID0005")) {
			 Transaction newTransaction = Transaction.builder()
	                    .txId(transaction.getTxId())
	                    
	                    .externalTxId(transaction.getExternalTxId())
	                    .accountId("***********")
	                    .accountName(transaction.getAccountName()) // Assuming you want to set the currency as the account name
	                    .txType(transaction.getTxType())
	                    .oppoBankCode(transaction.getOppoBankCode())
	                    .oppoAccountId(transaction.getOppoAccountId())
	                    .oppoSubAccount(transaction.getOppoSubAccount())
	                    .txStatus(transaction.getTxStatus())
	                    .mpesaBusinessPayType(transaction.getMpesaBusinessPayType())
	                    .oppoAccountName(transaction.getOppoAccountName())
	                    .counterpartyName(transaction.getCounterpartyName())
	                    .extInfo(transaction.getExtInfo())
	                    .oppoChannelId(transaction.getOppoChannelId())
	                    .thirdPartyTxType(transaction.getThirdPartyTxType())
	                    .currency(transaction.getCurrency())
	                    .amount(transaction.getAmount())
	                    
	                    .completeTime(transaction.getCompleteTime())
	                    .notificationType(transaction.getNotificationType())
	                    .requestId(transaction.getRequestId())
	                    .build();
			var walletClient = this.walletClientAccountRepo.findWalletClientByTillNumberAndAccountType(
					transaction.getOppoAccountId(), FinancialInstituation.MPESA);
                 if(!walletClient.isEmpty()) {
                	 newTransaction.id=this.userService.findUserByAccountd(transaction.getAccountId()).isPresent()? this.userService.findUserByAccountd(transaction.getAccountId()).get().toString():null;
                	 
                
			WebClient.ResponseSpec responseSpec = webClient.post().uri(walletClient.get(0).getCallBackUrl())
					.contentType(MediaType.APPLICATION_JSON).bodyValue(newTransaction).retrieve();

			// Perform the request and handle the response
			responseSpec.toEntity(String.class).subscribe(responseEntity -> {
				// Handle the response here
			});
                 }
		}
	}

	@Async
	@Transactional
	public void notifyMerchantIncomingPayment(Object walletClientService, SdkPayDto pay) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			var data = (net.sasakonnect.wallet.ResponseDto.TransactionResponseDto) walletClientService;
			System.out.println(walletClientService);

			var clientApp = clientAppsBean.getWalletClient();

			pay.getPayload().put("txtId",data.getData().txId);

			String jsonRequestBody;

			jsonRequestBody = objectMapper.writeValueAsString(pay);

			WebClient.ResponseSpec responseSpec = webClient.post().uri(clientApp.getCallBackUrl())
					.contentType(MediaType.APPLICATION_JSON).bodyValue(jsonRequestBody).retrieve();

			// Perform the request and handle the response
			responseSpec.toEntity(String.class).subscribe(responseEntity -> {
				// Handle the response here
			});
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

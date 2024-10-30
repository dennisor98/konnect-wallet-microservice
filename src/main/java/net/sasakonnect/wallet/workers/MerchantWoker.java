package net.sasakonnect.wallet.workers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.enums.NotificationType;
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

	@Transactional
	public void notifyMerchant(Transaction transaction) {
		if (transaction.getTxType().equalsIgnoreCase("TTID0005") || (transaction.getTxType().equalsIgnoreCase(NotificationType.TRANSACTION.getCode()) && 
				transaction.getOppoAccountId().equalsIgnoreCase("46012000048133")) ) {
			Transaction newTransaction = Transaction.builder().txId(transaction.getTxId())

					.externalTxId(transaction.getExternalTxId()).accountId("***********")
					.accountName(transaction.getAccountName()) // Assuming you want to set the currency as the account name
					.txType(transaction.getTxType()).oppoBankCode(transaction.getOppoBankCode())
					.oppoAccountId(transaction.getOppoAccountId()).oppoSubAccount(transaction.getOppoSubAccount())
					.txStatus(transaction.getTxStatus()).mpesaBusinessPayType(transaction.getMpesaBusinessPayType())
					.oppoAccountName(transaction.getOppoAccountName())
					.counterpartyName(transaction.getCounterpartyName()).extInfo(transaction.getExtInfo())
					.oppoChannelId(transaction.getOppoChannelId()).thirdPartyTxType(transaction.getThirdPartyTxType())
					.currency(transaction.getCurrency()).amount(transaction.getAmount())
					.completeTime(transaction.getCompleteTime()).notificationType(transaction.getNotificationType())
					.requestId(transaction.getRequestId()).build();
//			var walletClient = this.walletClientAccountRepo.findWalletClientByTillNumberAndAccountType(
//					transaction.getOppoAccountId(), FinancialInstituation.MPESA);
			var walletClient = this.walletClientAccountRepo
					.findWalletClientBySmeAccount(transaction.getOppoAccountId());
			if (!walletClient.isEmpty()) {

				newTransaction.id = this.userService.findUserByAccountd(transaction.getAccountId()).isPresent()
						? this.userService.findUserByAccountd(transaction.getAccountId()).get().getOpenId()
						: null;

				WebClient.ResponseSpec responseSpec = webClient.post().uri(walletClient.get(0).getCallBackUrl())
						.contentType(MediaType.APPLICATION_JSON).bodyValue(newTransaction).retrieve();

				// Perform the request and handle the response
				responseSpec.toEntity(String.class).subscribe(responseEntity -> {
					// Handle the response here
				});

			}
		}
		
//		

	}

	@Transactional
	public void notifyMerchantIncomingPayment(TransactionResponseDto data, SdkPayDto pay) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			System.out.println(data);

			var clientApp = clientAppsBean.getWalletClient();

			pay.getPayload().put("txtId", data.getData().txId);

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

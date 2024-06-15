package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ChoiceTransferDto;
import net.sasakonnect.wallet.RequestDto.MpesaBillType;
import net.sasakonnect.wallet.RequestDto.MpesaBilling;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.WalletBilling;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.WalletClientAccount;
import net.sasakonnect.wallet.repository.CurrencyRepository;
import net.sasakonnect.wallet.repository.LogsRepository;
import net.sasakonnect.wallet.repository.RejectedAccountRepository;
import net.sasakonnect.wallet.repository.UserJobRepository;
import net.sasakonnect.wallet.repository.UserWalletRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.services.extensions.LarkUtilityService;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SdkWalletService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	ChoiceBankSmsService choiceBankSmsService;
	@Autowired
	UserService userService;

	@Autowired
	AccountStatementService accountStatementService;

	@Autowired
	LarkService larkService;

	@Autowired
	LarkUtilityService larkUtilityService;

	@Autowired
	WalletRepository walletRepository;
	@Autowired
	CurrencyRepository currencyRepository;

	@Autowired
	UserWalletRepository userWalletRepository;

	@Autowired
	RejectedAccountRepository rejectedAccountRepository;

	@Autowired
	UserJobRepository userJobRepository;
	@Autowired
	LogsRepository logsRepository;
	@Value("${KONNECT_BANK}")
	private String konnectBank;

	public TransactionResponseDto applyForTransfer(@Valid ChoiceTransferDto choiceTransfer) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);
			reqId.put("payerAccountId", userwallet.getAccountId());

		}
		var receivingUser = this.userService.findUserByAccountd(choiceTransfer.getReceiverAccount().trim());
		if (receivingUser.isPresent()) {
			reqId.put("payeeMobileForNotification", receivingUser.get().getMobile());

		}

		reqId.put("payeeBankCode", choiceTransfer.getBankCode().trim());

		reqId.put("payeeAccountId", choiceTransfer.getReceiverAccount().trim());
		reqId.put("payeeAccountName", choiceTransfer.getReceiverName());

		reqId.put("currency", choiceTransfer.getCurrencyCode());
		reqId.put("amount", choiceTransfer.getAmount());
		reqId.put("otpMobile", user.getMobile());
		reqId.put("otpType", choiceTransfer.getOtpType());
		reqId.put("remark", choiceTransfer.getRemarks());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();
		log.info(responseJson);
		if (responseJson != null) {
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
			log.info(resp.getData().txId);
			return resp;
		}

		// TODO Auto-generated method stub
		return null;
	}

	// overload
	public TransactionResponseDto requestWalletDeduction(@Valid SdkPayDto sdkpayDto, WalletClient clientApp) {
		var account = clientApp.getWalletClientAccount();
		var accounts = account.stream().filter(acc -> acc.getDeletedAt() == null && acc.getIsPrimary() == true)
				.collect(Collectors.toList());

		if (!accounts.isEmpty()) {
			var activeAccount = accounts.get(0);

			return performBilling(activeAccount, Integer.parseInt(sdkpayDto.getAmount()), clientApp);

		} else {
			var activeAccount = account.stream().filter(acc -> acc.getDeletedAt() == null).collect(Collectors.toList());
			if (!activeAccount.isEmpty()) {

				var anaccount = activeAccount.get(0);
				return performBilling(anaccount, Integer.parseInt(sdkpayDto.getAmount()), clientApp);

			}

		}
		return null;
		// TODO Auto-generated method stub

	}

	private TransactionResponseDto performBilling(WalletClientAccount activeAccount, int amount,
			WalletClient clientApp) {
		log.info("transferring " + amount + " to merchant(" + clientApp.getAppName() + ")'s account type "
				+ activeAccount.getAccountType());
		switch (activeAccount.getAccountType()) {
		case BANK:
			var choiceTransaferDto = ChoiceTransferDto.builder().amount(String.valueOf(amount))
					.bankCode(activeAccount.getBankCode()).receiverAccount(activeAccount.getBankAccount())
					.receiverName(clientApp.getAppName()).remarks(clientApp.getAppName()).build();

			return this.applyForTransfer(choiceTransaferDto);
		case MPESA:
			var mpesaBill = new MpesaBilling();
			mpesaBill.amount = amount;
			if (activeAccount.getTillNumber() != null) {
				mpesaBill.shortCode = activeAccount.getTillNumber();
				mpesaBill.setBillType(MpesaBillType.TILL);
				return this.mpesaTillAndByGoodsSdk(mpesaBill);

			} else if (activeAccount.getPayBillAccountNo() != null && activeAccount.getPaybillNumber() != null) {
				mpesaBill.shortCode = activeAccount.getPaybillNumber();
				mpesaBill.setBillType(MpesaBillType.PAY_BILL);
				mpesaBill.setReceivingAccount(activeAccount.getPayBillAccountNo());
				return this.mpesaTillAndByGoodsSdk(mpesaBill);
			}

			break;
		case WALLET:
			var walletBilling = WalletBilling.builder();

			walletBilling.receiverAccount(activeAccount.getWalletAccountNo()).receiverName(clientApp.getAppName())
					.remarks(clientApp.getAppName()).amount(String.valueOf(amount));

			return this.applyFoWalletToWalletMerchant(walletBilling.build());
		default:
			break;

		}
		return null;

	}

	public Object loadWalletFromMpesa(String merchantAccount, String targetNo, int amount) {

		var reqId = new HashMap<String, Object>();
		reqId.put("accountId", merchantAccount);
		reqId.put("amount", amount);
		reqId.put("mobile", targetNo);
		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.DEPOSIT_FROM_MPESA).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();
		log.info(responseJson);
		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public TransactionResponseDto applyFoWalletToWalletMerchant(ChoiceTransferDto walletTransfer) {
		User userLoggedIn = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var userop = this.userService
				.findUserByPhoneNumber(userLoggedIn.getMobile().substring(userLoggedIn.getMobile().length() - 9));
		if (userop.isPresent()) {
			// var user = userop.get();
			var reqId = new HashMap<String, Object>();

			var userWallets = this.walletRepository.findByUserWalletsUser(userLoggedIn);
			if (!userWallets.isEmpty()) {
				var userwallet = userWallets.get(0);
				reqId.put("payerAccountId", userwallet.getAccountId());
			}
			reqId.put("payeeAccountId", walletTransfer.getReceiverAccount());
			reqId.put("payeeBankCode", konnectBank);
			reqId.put("payeeAccountName", walletTransfer.getReceiverName());
			reqId.put("currency", walletTransfer.getCurrencyCode());
			reqId.put("amount", walletTransfer.getAmount());
			reqId.put("otpMobile", userLoggedIn.getMobile());
			reqId.put("otpType", walletTransfer.getOtpType());
			var reqs = this.requestSigner.signRequest(reqId);
			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);
			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {

				return new Gson().fromJson(responseJson, TransactionResponseDto.class);

			}

			// TODO Auto-generated method stub
			return null;
		} else {
			return null;
		}
		// return null;
	}

	public TransactionResponseDto mpesaTillAndByGoodsSdk(@Valid MpesaBilling tillAndBuyGoods) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);
			reqId.put("payerAccountId", userwallet.getAccountId());

		}
		reqId.put("payType", tillAndBuyGoods.getBillType().getCode());

		switch (tillAndBuyGoods.billType) {
		case PAY_BILL:
			reqId.put("payeeReferenNumber", tillAndBuyGoods.getReceivingAccount().trim());

			break;
		case TILL:

			break;
		default:
			break;

		}
		reqId.put("payeeShortCode", tillAndBuyGoods.getShortCode().trim());

		reqId.put("amount", tillAndBuyGoods.getAmount());
		reqId.put("description", tillAndBuyGoods.getShortNote());

		reqId.put("otpType", tillAndBuyGoods.getOtpType());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.MPESA_TILL_AND_PAYBILL).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);
		String responseJson = responseMono.block();
		log.info(responseJson);
		if (responseJson != null) {
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
			return resp;
			// return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

}

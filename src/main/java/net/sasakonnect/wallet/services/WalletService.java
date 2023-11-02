package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.BuyAirtime;
import net.sasakonnect.wallet.RequestDto.ChoiceTransferDto;
import net.sasakonnect.wallet.RequestDto.EasyOnboardingRequestParams;
import net.sasakonnect.wallet.RequestDto.Mpesa;
import net.sasakonnect.wallet.RequestDto.MpesaBillType;
import net.sasakonnect.wallet.RequestDto.MpesaBilling;
import net.sasakonnect.wallet.RequestDto.OnboardingOtp;
import net.sasakonnect.wallet.RequestDto.OnboardingStatus;
import net.sasakonnect.wallet.RequestDto.OtpTransfer;
import net.sasakonnect.wallet.RequestDto.PayUtility;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.TransactionPeriod;
import net.sasakonnect.wallet.RequestDto.TransferToMpesa;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.enums.NotificationBody;
import net.sasakonnect.wallet.enums.NotificationType;
import net.sasakonnect.wallet.enums.TransactionStatus;
import net.sasakonnect.wallet.enums.WalletTransactionType;
import net.sasakonnect.wallet.events.TransactionEvent;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;
import net.sasakonnect.wallet.repository.CurrencyRepository;
import net.sasakonnect.wallet.repository.UserWalletRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WalletService extends JwtService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	UserService userService;
	@Autowired
	ChatService chatService;
	@Autowired
	WalletRepository walletRepository;
	@Autowired
	CurrencyRepository currencyRepository;

	@Autowired
	UserWalletRepository userWalletRepository;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ApplicationEventPublisher publisher;

	public Object getWalletInfo() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var reqId = new HashMap<String, Object>();
		reqId.put("userId", user.getId());
		var reqs = requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.GET_WALLET_INFO)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		return null;
	}

	public Object getBankCode() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var reqId = new HashMap<String, Object>();
		reqId.put("userId", user.getId());
		var reqs = requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.BANK_CODES)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		return null;
	}

	public Object getWalletBalance() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user2 = this.userService.findUserWallet(user);

		if (user2.isPresent()) {
			var wallets = user2.get().getUserWallets();

			if (!wallets.isEmpty()) {
				var oneWallet = wallets.get(0);
				var reqId = new HashMap<String, Object>();
				reqId.put("accountId", oneWallet.getWallet().getAccountId());
				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.CHECK_BALANCE).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					return new Gson().fromJson(responseJson, Object.class);

				}
			}
		}

		return null;
	}

	public Object getTransactionHistory() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user2 = this.userService.findUserWallet(user);
		List<WalletTransactionType> transactionTypes = new ArrayList<>(Arrays.asList(WalletTransactionType.TTID0001,
				WalletTransactionType.TTID0002, WalletTransactionType.TTID0003, WalletTransactionType.TTID0004,
				WalletTransactionType.TTID0005, WalletTransactionType.TTID0006, WalletTransactionType.TTID0007));

		// Create an ArrayList for TransactionStatus
		List<TransactionStatus> transactionStatuses = new ArrayList<>(
				Arrays.asList(TransactionStatus.TIMEOUT, TransactionStatus.PENDING, TransactionStatus.PROCESSING,
						TransactionStatus.FAILED, TransactionStatus.SUCCESS));

		if (user2.isPresent()) {
			var wallets = user2.get().getUserWallets();

			if (!wallets.isEmpty()) {
				var oneWallet = wallets.get(0);
				var reqId = new HashMap<String, Object>();
				reqId.put("userId", user.getId());
				reqId.put("accountId", oneWallet.getWallet().getAccountId());
				reqId.put("txType",
						transactionTypes.stream().map(WalletTransactionType::getValue).collect(Collectors.toList()));

				reqId.put("txStatus",
						transactionStatuses.stream().map(TransactionStatus::getValue).collect(Collectors.toList()));

				reqId.put("startTime", TransactionPeriod.getThisMonthStart());
				reqId.put("endTime", TransactionPeriod.getTimeNow());
				reqId.put("pageSize", 20);
				reqId.put("pageNo", 1);
				reqId.put("orderByDesc", 1);

				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.GET_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					return new Gson().fromJson(responseJson, Object.class);

				}
			}
		}

		return null;
	}

	public Object getTransactionHistoryAsOf(@Valid TransactionPeriod transactionPeriod) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user2 = this.userService.findUserWallet(user);
		List<WalletTransactionType> transactionTypes = new ArrayList<>(Arrays.asList(WalletTransactionType.TTID0001,
				WalletTransactionType.TTID0002, WalletTransactionType.TTID0003, WalletTransactionType.TTID0004,
				WalletTransactionType.TTID0005, WalletTransactionType.TTID0006, WalletTransactionType.TTID0007));

		// Create an ArrayList for TransactionStatus
		List<TransactionStatus> transactionStatuses = new ArrayList<>(
				Arrays.asList(TransactionStatus.TIMEOUT, TransactionStatus.PENDING, TransactionStatus.PROCESSING,
						TransactionStatus.FAILED, TransactionStatus.SUCCESS));

		if (user2.isPresent()) {
			var wallets = user2.get().getUserWallets();

			if (!wallets.isEmpty()) {
				System.out.println("Start time is " + transactionPeriod.getStartDate());
				var oneWallet = wallets.get(0);
				var reqId = new HashMap<String, Object>();
				reqId.put("userId", user.getId());
				reqId.put("accountId", oneWallet.getWallet().getAccountId());
				reqId.put("txType",
						transactionTypes.stream().map(WalletTransactionType::getValue).collect(Collectors.toList()));

				reqId.put("txStatus",
						transactionStatuses.stream().map(TransactionStatus::getValue).collect(Collectors.toList()));

				reqId.put("startTime", transactionPeriod.getStartDate());
				reqId.put("endTime", transactionPeriod.getEndDate());
				reqId.put("pageSize", 20);
				reqId.put("pageNo", 1);
				reqId.put("orderByDesc", 1);

				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.GET_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					return new Gson().fromJson(responseJson, Object.class);

				}
			}
		}

		return null;
	}

	public Object getOnBoardingStatus(OnboardingStatus onboardingStatus) {
		var user = this.userService.getUserById(onboardingStatus.getUser_id());
		if (user.isPresent()) {

			var reqId = new HashMap<String, Object>();
			reqId.put("onboardingRequestId", user.get().getOnboardingRequestId());
			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.POLL_ONBOARDING).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}

		}

		return null;
	}

	public Object loadWalletFromMpesa(Mpesa mpesa) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);

			var reqId = new HashMap<String, Object>();
			reqId.put("accountId", userwallet.getAccountId());
			reqId.put("amount", mpesa.getAmount());

			reqId.put("mobile", mpesa.getMpesaNumber());

			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.DEPOSIT_FROM_MPESA).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Object> createNewOnBoardingUser(@Valid EasyOnboardingRequestParams easyOnboarding) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("firstName", easyOnboarding.getFirstName());
		userMap.put("middleName", easyOnboarding.getMiddleName());
		userMap.put("lastName", easyOnboarding.getLastName());
		userMap.put("birthday", easyOnboarding.getBirthday());
		userMap.put("gender", easyOnboarding.getGenderVerbal().getValue());
		userMap.put("countryCode", easyOnboarding.getCountryCode());
		userMap.put("mobile", easyOnboarding.getMobile());
		userMap.put("idType", easyOnboarding.getIdTypeVerbal());
		userMap.put("idNumber", easyOnboarding.getIdNumber());
		userMap.put("kraPin", easyOnboarding.getKraPin());
		userMap.put("address", easyOnboarding.getMobile());
		userMap.put("employmentStatus", easyOnboarding.getEmploymentStatusType().getCode());
		userMap.put("monthlyIncome", easyOnboarding.monthlyIncomeType().getCode());
		try {
			var user = User.builder().firstName(easyOnboarding.getFirstName()).lastName(easyOnboarding.getLastName())
					.middleName(easyOnboarding.getMiddleName()).lastName(easyOnboarding.getLastName())
					.birthday(easyOnboarding.parseBithDay()).address(easyOnboarding.getAddress())
					.gender(easyOnboarding.getGenderVerbal())
					.countryCode(Integer.parseInt(easyOnboarding.getCountryCode()))
					.mobile(easyOnboarding.getSerchablePhone()).idType(easyOnboarding.getIdTypeEnum())
					.monthlyIncome(easyOnboarding.monthlyIncomeType()).kraPin(easyOnboarding.getKraPin())
					.employmentStatus(easyOnboarding.getEmploymentStatusType()).idNumber(easyOnboarding.getIdNumber())
					.build();

			final User savedUser = this.userService.createUser(user);
			savedUser.setPins(null);
			savedUser.setFirebaseTokens(null);
			savedUser.setUserDevices(null);
			savedUser.setUserWallets(null);
			savedUser.setUserPins(null);
			savedUser.setUserRole(null);
			savedUser.setNotifications(null);

			userMap.put("userId", savedUser.getId());

			var reqs = this.requestSigner.signRequest(userMap);
			Mono<JsonNode> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.OPEN_WALLET_ACCOUNT).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class) // Deserialize the response as a String
					.map(response -> {
						ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.registerModule(new JavaTimeModule());

						try {
							JsonNode jsonNode = objectMapper.readTree(response);
							return jsonNode;
						} catch (Exception e) {
							// Handle any potential exception here
							e.printStackTrace();
							return objectMapper.createObjectNode(); // Return an empty JsonObject or handle the error
																	// appropriately
						}
					});
			var jsonNode = responseMono.block();
			var onboardingRequestId = jsonNode.path("data").path("onboardingRequestId");
			if (onboardingRequestId.isNull()) {
				this.userService.deleteUserById(savedUser.getId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("payload", jsonNode);
				map.put("success", false);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
			} else {
				savedUser.setOnboardingRequestId(onboardingRequestId.asText());
				var updateduser = this.userService.updateUser(savedUser);
				return this.userService.createJwtFor(savedUser);

			}

		} catch (DataIntegrityViolationException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Account already exist");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		} catch (Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", e.getMessage());
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		}
	}

	public Object onCallBackInvocation(JsonObject body) {
		// TODO Auto-generated method stub
		return this.callBackContentResolver(body);

	}

	private Object callBackContentResolver(JsonObject body) {
		try {
			var notification_Type = body.get("notificationType").getAsString();
			var params = body.getAsJsonObject("params");

			NotificationBody notificationBody = new Gson().fromJson(params, NotificationBody.class);

			if (notification_Type.equalsIgnoreCase(NotificationType.ONBOARD.getCode())) {
				var user = this.userService.getUserById(params.get("userId").getAsString());
				if (notificationBody.getStatus() == 7 && user.isPresent()) {
					Optional<Wallet> existingWallet = walletRepository.findByAccountId(notificationBody.getAccountId());
					if (existingWallet.isEmpty()) {
						var wallet = new Wallet();
						wallet.setAccountId(notificationBody.getAccountId());
						wallet.setAccountType(notificationBody.getAccountType());
						var savedwallet = this.walletRepository.save(wallet);
						var userWallet = new UserWallet();
						userWallet.setUser(user.get());
						userWallet.setWallet(savedwallet);
						this.chatService.registerUserToOpenFire(user.get());

						this.userWalletRepository.save(userWallet);
					}

				} else {
					var onboardingRequestId = params.get("onboardingRequestId").getAsString();
					System.out.println(onboardingRequestId);
					this.userService.deletUserByOnboardingRequestId(onboardingRequestId);

				}
			} else if (notification_Type == NotificationType.ACCOUNT_STATEMENT.getCode()) {

			} else if (notification_Type.equalsIgnoreCase(NotificationType.TRANSACTION.getCode())) {
				log.info("payload {}", body.toString());
//				TypeToken<NotificationResult<TransactionResultNotification>> typeToken = new TypeToken<NotificationResult<TransactionResultNotification>>() {
//				};
				NotificationResult<TransactionResultNotification> results = new Gson().fromJson(body.toString(),
						new TypeToken<NotificationResult<TransactionResultNotification>>() {
						}.getType());

				log.info("transacttion {}", results);
//				System.out.println(typeToken.getType().getTypeName());
//				System.out.println(results.getNotificationType());
				var createdTransaction = this.transactionService.saveTransaction(results);
				if (createdTransaction != null) {
					log.info("publish transaction to socket {}", createdTransaction);

					this.publisher.publishEvent(TransactionEvent.builder().userService(userService)
							.transaction(createdTransaction).build());
				}

			} else if (notification_Type == NotificationType.BALANCE.getCode()) {

			} else if (notification_Type == NotificationType.INTERNAL_BATCH_TRANSACTION.getCode()) {

			} else if (notification_Type == NotificationType.WALLET_ACCOUNT_UPGRADE.getCode()) {

			} else if (notification_Type == NotificationType.SME_ACCOUNT_OPEN.getCode()) {

			} else if (notification_Type == NotificationType.UTILITY.getCode()) {

			} else if (notification_Type == NotificationType.BULK_PAYMENT.getCode()) {

			} else if (notification_Type == NotificationType.FOREIGN_CURRENCY_DEPOSIT.getCode()) {

			} else if (notification_Type == NotificationType.FOREIGN_CURRENCY_OUTBOUND_TRANSACTION.getCode()) {

			} else if (notification_Type == NotificationType.MULTIPLE_ACCOUNT_OPENING.getCode()) {

			} else if (notification_Type == NotificationType.FOREIGN_CURRENCY_EXCHANGE.getCode()) {

			} else if (notification_Type == NotificationType.BULK_UTILITY_PAYMENT.getCode()) {

			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "Got You!");
		map.put("success", true);
		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}

	public Object confirmOnboardingOtp(@Valid OnboardingOtp otp) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		reqId.put("onboardingRequestId", user.getOnboardingRequestId());
		reqId.put("onboardType", "personal");
		reqId.put("code", otp.getOtp());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.CONFIRM_OTP)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object resendOnboardingOtp() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		reqId.put("onboardingRequestId", user.getOnboardingRequestId());
		reqId.put("onboardType", "personal");

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.REQUEST_OTP_RESEND).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object sendToOtherWallet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object sendToMpesa(@Valid TransferToMpesa mpesa) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);

			var reqId = new HashMap<String, Object>();
			reqId.put("payerAccountId", userwallet.getAccountId());
			reqId.put("amount", mpesa.getAmount());
			reqId.put("payeeBankCode", "M-PESA");
			reqId.put("payeeAccountId", mpesa.getReceiverMobileNumber());
			reqId.put("currency", mpesa.getCurrencyCode());
			reqId.put("remark", mpesa.getRemarks());
			reqId.put("otpType", "SMS");
			reqId.put("payeeMobileForNotification", mpesa.getPayeeMobileForNotification());

			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Object sendToOtherBankingInstitution(@Valid ChoiceTransferDto mpesa) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);

			var reqId = new HashMap<String, Object>();
			reqId.put("payerAccountId", userwallet.getAccountId());
			reqId.put("amount", mpesa.getAmount());

			reqId.put("payeeBankCode", mpesa.getBankCode());
			reqId.put("payeeAccountId", mpesa.getReceiverAccount());
			reqId.put("currency", mpesa.getCurrencyCode());
			reqId.put("remark", mpesa.getRemarks());
			reqId.put("otpType", "SMS");
			reqId.put("payeeMobileForNotification", mpesa.getPayeeMobileForNotification());

			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Object getOnboardingStatus() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		reqId.put("onboardingRequestId", user.getOnboardingRequestId());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.GET_ONBOARDING_STATUS).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object getAccountStatus() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var res = this.userService.isPinSet();
		List<Wallet> wallets = this.walletRepository.findByUserWalletsUser(user);
		if (wallets != null && wallets.isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Wallet Not Confirmed");
			map.put("code", "KWEC001");
			map.put("success", "false");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}

		else if (res.getStatusCode() == HttpStatus.OK) {
			return res;
		} else {
			return res;
		}

	}

	public Object currencyIso() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", currencyRepository.findAll());
		map.put("success", true);
		return ResponseEntity.status(HttpStatus.OK).body(map);

	}

	public Object airtimePayment(@Valid BuyAirtime buyAirtime) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		if (buyAirtime.getAccountId() == null) {
			var userWallets = this.walletRepository.findByUserWalletsUser(user);
			if (!userWallets.isEmpty()) {
				var userwallet = userWallets.get(0);
				reqId.put("accountId", userwallet.getAccountId());

			}
		} else {
			reqId.put("accountId", buyAirtime.getAccountId());

		}
		reqId.put("mobileNumber", buyAirtime.getMobileNumber());

		reqId.put("networkProvider", buyAirtime.getNetworkProviderId());

		reqId.put("amount", Integer.parseInt(buyAirtime.getAmount()));

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.BUY_AIRTIME)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object payUtility(@Valid PayUtility payUtiltiy) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		if (payUtiltiy.getAccountId() == null) {
			var userWallets = this.walletRepository.findByUserWalletsUser(user);
			if (!userWallets.isEmpty()) {
				var userwallet = userWallets.get(0);
				reqId.put("accountId", userwallet.getAccountId());

			}
		} else {
			reqId.put("accountId", payUtiltiy.getAccountId());

		}
		reqId.put("billOrderNumber", payUtiltiy.getBillOrderNumber());

		reqId.put("billType", payUtiltiy.getBillType());

		reqId.put("amount", Integer.parseInt(payUtiltiy.getAmount()));

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.PAY_UTILITY)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object applyForTransfer(@Valid ChoiceTransferDto choiceTransfer) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();

		var userWallets = this.walletRepository.findByUserWalletsUser(user);
		if (!userWallets.isEmpty()) {
			var userwallet = userWallets.get(0);
			reqId.put("payerAccountId", userwallet.getAccountId());

		}

		reqId.put("payeeBankCode", choiceTransfer.getBankCode());

		reqId.put("payeeAccountId", choiceTransfer.getReceiverAccount());
		reqId.put("payeeAccountName", choiceTransfer.getReceiverName());

		reqId.put("currency", choiceTransfer.getCurrencyCode());
		reqId.put("amount", choiceTransfer.getAmount());
		reqId.put("otpMobile", user.getMobile());
		reqId.put("otpType", choiceTransfer.getOtpType());
		reqId.put("payeeMobileForNotification", choiceTransfer.getPayeeMobileForNotification());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object confirmOtpTransfer(OtpTransfer otpTransfer) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		reqId.put("txId", otpTransfer.getTxId());
		reqId.put("otpCode", otpTransfer.getOtp());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.CONFIRM_OTP_TRANSFER).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object mpesaTillAndByGoods(@Valid MpesaBilling tillAndBuyGoods) {
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
			reqId.put("payeeReferenNumber", tillAndBuyGoods.getReceivingAccount());

			break;
		case TILL:

			break;
		default:
			break;

		}
		reqId.put("payeeShortCode", tillAndBuyGoods.getShortCode());

		reqId.put("amount", tillAndBuyGoods.getAmount());
		reqId.put("description", tillAndBuyGoods.getShortNote());

		reqId.put("otpType", tillAndBuyGoods.getOtpType());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.MPESA_TILL_AND_PAYBILL).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object requestWalletDeduction(@Valid SdkPayDto sdkpayDto, WalletClient clientApp) {
		var account = clientApp.getWalletClientAccount();
		switch (account.getAccountType()) {
		case BANK:
			break;
		case MPESA:
			var mpesaBill = new MpesaBilling();
			mpesaBill.amount = Integer.parseInt(sdkpayDto.getAmount());
			if (account.getTillNumber() != null) {
				mpesaBill.shortCode = account.getTillNumber();
				mpesaBill.setBillType(MpesaBillType.TILL);
				return this.mpesaTillAndByGoods(mpesaBill);

			} else if (account.getPayBillAccountNo() != null && account.getPaybillNumber() != null) {
				mpesaBill.shortCode = account.getPaybillNumber();
				mpesaBill.setBillType(MpesaBillType.PAY_BILL);
				mpesaBill.setReceivingAccount(account.getPayBillAccountNo());
				return this.mpesaTillAndByGoods(mpesaBill);
			}

			break;
		case WALLET:

			break;
		default:
			break;

		}
		return clientApp.getWalletClientAccount().getAccountType().name();

		// TODO Auto-generated method stub

	}
}

package net.sasakonnect.wallet.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import net.sasakonnect.wallet.RequestDto.PhoneCheckDto;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.TransactionPeriod;
import net.sasakonnect.wallet.RequestDto.TransferToMpesa;
import net.sasakonnect.wallet.RequestDto.UpgradeWalletAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.RequestDto.admin.CheckUserAccount;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserJob;
import net.sasakonnect.wallet.domain.UserPin;
import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.enums.NotificationBody;
import net.sasakonnect.wallet.enums.NotificationType;
import net.sasakonnect.wallet.enums.TransactionStatus;
import net.sasakonnect.wallet.enums.WalletTransactionType;
import net.sasakonnect.wallet.events.StatementGenerationEvent;
import net.sasakonnect.wallet.events.TransactionEvent;
import net.sasakonnect.wallet.notification.AccountStatementReportNotification;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;
import net.sasakonnect.wallet.notification.WalletAccountUpgradeResultNotification;
import net.sasakonnect.wallet.repository.CurrencyRepository;
import net.sasakonnect.wallet.repository.UserJobRepository;
import net.sasakonnect.wallet.repository.UserWalletRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WalletService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	ChoiceBankSmsService choiceBankSmsService;
	@Autowired
	UserService userService;
	@Autowired
	ChatService chatService;

	@Autowired
	AccountStatementService accountStatementService;

	@Autowired
	LarkService larkService;

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
	@Value("${email.statements}")
	private String emailStatement;
	@Autowired
	UserJobRepository userJobRepository;
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	TransactionEventService transactionEventService;

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

	// overloaded function to get acc.balance by accountId
	public Object getWalletAccountBalance(String accountId) {

		var user = this.userService.findUserByWalletAccountId(accountId);

		if (user.isPresent()) {
			var wallets = user.get().getUserWallets();

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
					return (new Gson().fromJson(responseJson, Object.class));

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
		if (easyOnboarding.getKraPin() != null) {
			userMap.put("kraPin", easyOnboarding.getKraPin());

		}
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

			User su = this.userService.createUser(user);
			var savedUser = this.userService.findUserAndWallets(su).get();
			savedUser.setPins(null);
			savedUser.setFirebaseTokens(null);
			savedUser.setUserDevices(null);
			// savedUser.setUserWallets(null);
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
				this.choiceBankSmsService.invokeSms(onboardingRequestId.asText());
				return this.userService.createJwtFor(savedUser);

			}

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Account already exist");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		} catch (Exception e) {
			e.printStackTrace();
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
						this.larkService.sendOnBoardingMessage("SUCCESSFUL ONBOARDING", "orange", notificationBody);
						// log creation of new wallet
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

				} else if (notificationBody.getStatus() == 3 && user.isPresent()) {
					this.larkService.sendOnBoardingMessage("ACCOUNT OPENING PASSED", "green", notificationBody);
				} else if (notificationBody.getStatus() == 4 && user.isPresent()) {
					// log rejected account deletion
					this.larkService.sendOnBoardingMessage("REJECTED ONBOARDING", "red", notificationBody);
					var onboardingRequestId = params.get("onboardingRequestId").getAsString();
					System.out.println(onboardingRequestId);
					this.userService.deletUserByOnboardingRequestId(onboardingRequestId);

				} else if (notificationBody.getStatus() == 5 && user.isPresent()) {
					// log closed account
					this.larkService.sendOnBoardingMessage("ACCOUNT CLOSED", "red", notificationBody);
					Optional<Wallet> existingWallet = walletRepository.findByAccountId(notificationBody.getAccountId());
					if (existingWallet.isPresent()) {
					}

				} else if (notificationBody.getStatus() == 8 && user.isPresent()) {
					// log failed account opening
					this.larkService.sendOnBoardingMessage("ACCOUNT OPENING FAILED", "red", notificationBody);
					var onboardingRequestId = params.get("onboardingRequestId").getAsString();
					System.out.println(onboardingRequestId);
					// delete user from the system
					this.userService.deletUserByOnboardingRequestId(onboardingRequestId);
				} else if (notificationBody.getStatus() == 9 && user.isPresent()) {
					// account under manual review
					this.larkService.sendOnBoardingMessage("ACCOUNT UNDER MANUAL REVIEW", "green", notificationBody);

				} else {

					// log any other onboarding account status
					if (user.isPresent()) {
						user.get().setStatus(params.get("status").getAsString());
						this.userService.save(user.get());

					}
				}

			} else if (notification_Type == NotificationType.ACCOUNT_STATEMENT.getCode()) {

			} else if (notification_Type.equalsIgnoreCase(NotificationType.TRANSACTION.getCode())) {

				log.info("payload {}", body.toString());

				NotificationResult<TransactionResultNotification> results = new Gson().fromJson(body.toString(),
						new TypeToken<NotificationResult<TransactionResultNotification>>() {
						}.getType());
				var transaction = this.transactionService.getTransactionById(results.getParams().getTxId());
				if (transaction.isPresent()) {
					transaction.get().setTxStatus(results.getParams().getTxStatus());
					var createdTransaction = this.transactionService.transactionRepository.save(transaction.get());

				} else {
					var createdTransaction = this.transactionService.saveTransaction(results);
					if (createdTransaction != null) {
						log.info("publish transaction to socket {}", createdTransaction);

						this.publisher.publishEvent(TransactionEvent.builder().userService(userService)
								.transaction(createdTransaction).build());
					}
				}
				log.info("transacttion {}", results);

			} else if (notification_Type.equalsIgnoreCase(NotificationType.BALANCE.getCode())) {

				NotificationResult<TransactionResultNotification> results = new Gson().fromJson(body.toString(),
						new TypeToken<NotificationResult<TransactionResultNotification>>() {
						}.getType());
				log.info("balance update {}", results);

				var transaction = this.transactionService.getTransactionById(results.getParams().getTxId());
				if (transaction.isPresent() && this.transactionService.isUpdatableTransaction(results)) {
					transaction.get().setTxStatus(8);
					transaction.get().setBalance(new BigDecimal(results.getParams().getBalance()));
					this.transactionService.transactionRepository.save(transaction.get());

					this.publisher.publishEvent(
							TransactionEvent.builder().userService(userService).transaction(transaction.get()).build());

				} else {
					if (results.getParams().getTxStatus() == 0) {
						results.getParams().setTxStatus(8);

					}
					var createdTransaction = this.transactionService.saveTransaction(results);
					if (createdTransaction != null) {
						log.info("publish transaction to socket {}", createdTransaction);

						this.publisher.publishEvent(TransactionEvent.builder().userService(userService)
								.transaction(createdTransaction).build());
					}

				}

			} else if (notification_Type == NotificationType.INTERNAL_BATCH_TRANSACTION.getCode()) {

			} else if (notification_Type == NotificationType.WALLET_ACCOUNT_UPGRADE.getCode()) {

				//
				NotificationResult<WalletAccountUpgradeResultNotification> results = new Gson().fromJson(
						body.toString(), new TypeToken<NotificationResult<WalletAccountUpgradeResultNotification>>() {
						}.getType());
				log.info("balance update {}", results);
				Optional<Wallet> wallet = this.walletRepository.findByAccountId(results.getParams().getAccountId());

				// update wallet type
				if (wallet.isPresent()) {
					wallet.get().setAccountType(results.getParams().getAccountType());
					this.walletRepository.save(wallet.get());
				}
				this.userService.pushUpgradeNotification(results.getParams());

			} else if (notification_Type == NotificationType.SME_ACCOUNT_OPEN.getCode()) {

			} else if (notification_Type == NotificationType.UTILITY.getCode()) {

			} else if (notification_Type == NotificationType.BULK_PAYMENT.getCode()) {

			} else if (notification_Type.equalsIgnoreCase(NotificationType.ACCOUNT_STATEMENT.getCode())) {
				NotificationResult<AccountStatementReportNotification> results = new Gson().fromJson(body.toString(),
						new TypeToken<NotificationResult<AccountStatementReportNotification>>() {
						}.getType());
				this.accountStatementService.readFileAndGeneratePDF(results.getParams().getJobId(),
						results.getParams().getStatementUrl(),
						applicationContext.getBean(StatementGenerationEvent.class));

				/// this.userJobRepository.updateByJobId()

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
			reqId.put("payeeMobileForNotification", mpesa.getReceiverMobileNumber());

			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			// call wallet invitation thread
			transactionEventService.notifyNewCustomer(userwallet.getAccountId(), mpesa.getReceiverMobileNumber());

			if (responseJson != null) {
				var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
				choiceBankSmsService.invokeSms(resp.getData().txId);
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
				// return new Gson().fromJson(responseJson, Object.class);
				var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
				choiceBankSmsService.invokeSms(resp.getData().txId);
				return resp;

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

	public Object getOnboardingStatus(String onBoardingId) {

		var reqId = new HashMap<String, Object>();
		reqId.put("onboardingRequestId", onBoardingId);

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
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			log.info("begun sending sms");
			choiceBankSmsService.invokeSms(resp.getData().txId);
			log.info("now returning");

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
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
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
		var receivingUser = this.userService.findUserByAccountd(choiceTransfer.getReceiverAccount());
		if (receivingUser.isPresent()) {
			reqId.put("payeeMobileForNotification", receivingUser.get().getMobile());

		}

		reqId.put("payeeBankCode", choiceTransfer.getBankCode());

		reqId.put("payeeAccountId", choiceTransfer.getReceiverAccount());
		reqId.put("payeeAccountName", choiceTransfer.getReceiverName());

		reqId.put("currency", choiceTransfer.getCurrencyCode());
		reqId.put("amount", choiceTransfer.getAmount());
		reqId.put("otpMobile", user.getMobile());
		reqId.put("otpType", choiceTransfer.getOtpType());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
			log.info(resp.getData().txId);
			return resp;
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object applyFoWalletToWallet(@Valid WalletTransferDto walletTransfer) {
		User userLoggedIn = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var phoneNumber = walletTransfer.getPhoneNumber().substring(walletTransfer.getPhoneNumber().length() - 9);
		// return phoneNumber;
		var userop = this.userService.findUserByPhoneNumber(phoneNumber);
		if (userop.isPresent()) {
			var user = userop.get();
			var reqId = new HashMap<String, Object>();

			var userWallets = this.walletRepository.findByUserWalletsUser(userLoggedIn);
			if (!userWallets.isEmpty()) {
				var userwallet = userWallets.get(0);
				reqId.put("payerAccountId", userwallet.getAccountId());
			}
			if (user.getUserWallets().size() > 0) {
				reqId.put("payeeAccountId", user.getUserWallets().get(0).getWallet().getAccountId());
			} else {
				Map<String, String> map = new HashMap<String, String>();
				map.put("message", "user with phone " + walletTransfer.getPhoneNumber() + " not found");
				map.put("success", "false");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
			}
			reqId.put("payeeBankCode", "CIC0018");
			reqId.put("payeeAccountName", user.getFirstName());
			reqId.put("currency", walletTransfer.getCurrencyCode());
			reqId.put("amount", walletTransfer.getAmount());
			reqId.put("otpMobile", userLoggedIn.getMobile());
			reqId.put("otpType", walletTransfer.getOtpType());
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
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "user with phone " + walletTransfer.getPhoneNumber() + " not found");

			map.put("success", "false");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}
		// return null;
	}

	public Object confirmOtpTransfer(OtpTransfer otpTransfer) {
		// User user = (User)
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return this.choiceBankSmsService.confirmOperation(otpTransfer.getTxId(), otpTransfer.getOtp());
//		var reqId = new HashMap<String, Object>();
//		reqId.put("txId", otpTransfer.getTxId());
//		reqId.put("otpCode", otpTransfer.getOtp());
//
//		var reqs = this.requestSigner.signRequest(reqId);
//
//		Mono<String> responseMono = this.bankClientBean.webClient.post()
//				.uri(ChoiceEndpointsConstants.CONFIRM_OTP_TRANSFER).contentType(MediaType.APPLICATION_JSON)
//				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
//				.bodyToMono(String.class);
//
//		String responseJson = responseMono.block();
//
//		if (responseJson != null) {
//			return new Gson().fromJson(responseJson, Object.class);
//
//		}
//
//		// TODO Auto-generated method stub
//		return null;
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
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
			return resp;
			// return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object requestWalletDeduction(@Valid SdkPayDto sdkpayDto, WalletClient clientApp) {
		var account = clientApp.getWalletClientAccount();
		System.out.println(account.get(0).getId());
		var activeAccount = account.stream().takeWhile(acc -> acc.getDeletedAt() == null).findFirst().get();
		switch (activeAccount.getAccountType()) {
		case BANK:
			break;
		case MPESA:
			var mpesaBill = new MpesaBilling();
			mpesaBill.amount = Integer.parseInt(sdkpayDto.getAmount());
			if (activeAccount.getTillNumber() != null) {
				mpesaBill.shortCode = activeAccount.getTillNumber();
				mpesaBill.setBillType(MpesaBillType.TILL);
				return this.mpesaTillAndByGoods(mpesaBill);

			} else if (activeAccount.getPayBillAccountNo() != null && activeAccount.getPaybillNumber() != null) {
				mpesaBill.shortCode = activeAccount.getPaybillNumber();
				mpesaBill.setBillType(MpesaBillType.PAY_BILL);
				mpesaBill.setReceivingAccount(activeAccount.getPayBillAccountNo());
				return this.mpesaTillAndByGoods(mpesaBill);
			}

			break;
		case WALLET:

			break;
		default:
			break;

		}
		return activeAccount.getAccountType().name();

		// TODO Auto-generated method stub

	}

	public Object checkUserAccountStatus(@Valid CheckUserAccount checkUserAccount) {
		var results = this.userService.findUserByPhoneNumber(checkUserAccount.getPhoneNumber());
		if (results.isPresent()) {
			var reqId = new HashMap<String, Object>();
			reqId.put("userId", results.get().getId());
			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.GET_WALLET_INFO).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "user with phone " + checkUserAccount.getPhoneNumber() + "not found");

			map.put("success", "false");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Object checkUserPublicAccount(@Valid PhoneCheckDto phoneCheck) {
		var phoneNumber = phoneCheck.getPhoneNumber().substring(phoneCheck.getPhoneNumber().length() - 9);
		// return phoneNumber;
		var userop = this.userService.findUserByPhoneNumber(phoneNumber);
		if (userop.isPresent()) {
			var user = userop.get();
			var reqId = new HashMap<String, Object>();

			var userWallets = this.walletRepository.findByUserWalletsUser(user);
			if (!userWallets.isEmpty()) {
				var userwallet = userWallets.get(0);
				reqId.put("payerAccountId", userwallet.getAccountId());
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("account", userwallet.getAccountId());
				payload.put("name", user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
				map.put("payload", payload);
				map.put("success", "true");
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			if (user.getUserWallets().size() > 0) {
				reqId.put("payeeAccountId", user.getUserWallets().get(0).getWallet().getAccountId());
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "user with phone " + phoneCheck.getPhoneNumber() + " not found");
				map.put("success", "false");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);

			}
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "user with phone " + phoneCheck.getPhoneNumber() + " not found");
			map.put("success", "false");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Object> getWalletPinAttempts(String accountId) {
		Optional<User> user = this.userService.findUserByAccountd(accountId);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> resMap = new HashMap<>();
		if (user.isPresent()) {
			var pins = user.get().getPins();
			if (pins != null && !pins.isEmpty()) {
				UserPin userPin = user.get().getPins().get(0);
				Integer attempts = userPin.getPinAttempts();
				map.put("attempts", attempts);
				map.put("isBlocked", attempts >= 5 ? true : false);
				resMap.put("success", true);
				resMap.put("message", "Request successfull");
				resMap.put("payload", map);
				return ResponseEntity.status(HttpStatus.OK).body(resMap);
			}
		}
		resMap.put("success", false);
		resMap.put("message", "Account not found");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMap);
	}

	public Object upgradeFromWalletToAccount(@Valid UpgradeWalletAccountDto upgradeWalletAccount) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var reqId = new HashMap<String, Object>();
		reqId.put("userId", user.getId());

		reqId.put("kraPin", upgradeWalletAccount.getKraPin());

		reqId.put("frontSidePhoto", upgradeWalletAccount.getFrontSidePhoto());
		reqId.put("backSidePhoto", upgradeWalletAccount.getBackSidePhoto());

		reqId.put("selfiePhoto", upgradeWalletAccount.getSelfiePhoto());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.UPGRADE_ACCOUNT)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object getAccountStatement(LocalDate startdate, LocalDate endDate) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var userWallets = this.walletRepository.findByUserWalletsUser(user);

		var reqId = new HashMap<String, Object>();

		reqId.put("startTime", startdate.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond());
		reqId.put("endTime", endDate.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond());
		reqId.put("accountId", userWallets.get(0).getAccountId());
		reqId.put("email", emailStatement);

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.REQUEST_BANK_STATEMENT).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
		// TODO Auto-generated method stub
	}

	public Object getUserStatement(LocalDate startDate, LocalDate endDate) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var wallets = this.walletRepository.findByUserWalletsUser(loggedInUser);
		if (!wallets.isEmpty()) {
			var currentWallet = wallets.get(0);

			var reqId = new HashMap<String, Object>();
			reqId.put("accountId", currentWallet.getAccountId());
			reqId.put("startTime", startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());

			reqId.put("endTime", endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());

			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.REQUEST_BANK_STATEMENT_CSV).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				var jsonObject = new Gson().fromJson(responseJson, JsonObject.class);
				System.out.println(responseJson);
				String jobId = jsonObject.getAsJsonObject("data").get("jobId").getAsString();

				var job = UserJob.builder().user(loggedInUser).jobOwner(loggedInUser).jobId(jobId).isAdmin(false)
						.build();
				this.userJobRepository.save(job);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "Please wait as we process your statement");
				map.put("success", true);
				return ResponseEntity.status(HttpStatus.OK).body(map);

			}
		}
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "Unable to request statement at this time");
		map.put("success", false);
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(map);
	}

	// Overloaded method
	// get account statement for admin based on accountId
	public Object getUserStatement(String accountId, LocalDate startDate, LocalDate endDate) {
		Optional<User> user = this.userService.findUserByWalletAccountId(accountId);
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!user.isEmpty()) {
			var wallets = this.walletRepository.findByAccountId(accountId);
			if (!wallets.isEmpty()) {
				var currentWallet = wallets.get();

				var reqId = new HashMap<String, Object>();
				reqId.put("accountId", currentWallet.getAccountId());
				reqId.put("startTime", startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());

				reqId.put("endTime", endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());

				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.REQUEST_BANK_STATEMENT_CSV)
						.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
						.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					var jsonObject = new Gson().fromJson(responseJson, JsonObject.class);
					System.out.println(responseJson);
					String jobId = jsonObject.getAsJsonObject("data").get("jobId").getAsString();

					var job = UserJob.builder().description(startDate.toString() + "-" + endDate.toString())
							.user(user.get()).jobId(jobId).jobOwner(loggedInUser).isAdmin(true).build();
					this.userJobRepository.save(job);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("message", "Please wait as we process your statement");
					map.put("success", true);
					return ResponseEntity.status(HttpStatus.OK).body(map);

				}
			}
			// TODO Auto-generated method stub
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Unable to request statement at this time");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(map);
		} else {
			return null;
		}
	}

	public ResponseEntity<Object> getUserRequestedstatements() {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<UserJob> statements = this.userJobRepository.findUserRequestedStatements(loggedInUser);
		Map<String, Object> map = new HashMap<>();
		map.put("success", true);
		map.put("message", "Request complete");
		if (!statements.isEmpty()) {
			var st = statements.stream().map(s -> {
				Map<String, Object> sMap = new HashMap<>();
				sMap.put("userId", s.getUser().getId());
				sMap.put("jobId", s.getJobId());
				sMap.put("owner", s.getJobOwner().getId());
				sMap.put("isRead", s.getIsRead());
				sMap.put("createdAt", s.getCreatedAt());
				sMap.put("description", s.getDescription());
				sMap.put("downloadLink", s.getDownloadLink());
				return sMap;
			}).collect(Collectors.toList());
			map.put("statements", st);
		} else {
			map.put("statements", new ArrayList<>());
		}
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	// overload

	public ResponseEntity<Object> getUserRequestedstatements(String userId) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = this.userService.getUserById(userId);
		if (user.isPresent()) {
			List<UserJob> statements = this.userJobRepository.findAdminStatementsByUser(loggedInUser, user.get());
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request complete");
			if (!statements.isEmpty()) {
				var st = statements.stream().map(s -> {
					Map<String, Object> sMap = new HashMap<>();
					sMap.put("createdAt", s.getCreatedAt());
					sMap.put("userId", s.getUser().getId());
					sMap.put("jobId", s.getJobId());
					sMap.put("owner", s.getJobOwner().getId());
					sMap.put("isRead", s.getIsRead());
					sMap.put("downloadLink", s.getDownloadLink());
					return sMap;
				}).collect(Collectors.toList());
				map.put("statements", st);
			} else {
				map.put("statements", new ArrayList<>());
			}
			return ResponseEntity.status(HttpStatus.OK).body(map);
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Uknown user");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	public ResponseEntity<Object> updateStamentRead(String jobId) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<UserJob> userJob = this.userJobRepository.findUserJobByJobId(jobId);
		if (userJob.isPresent()) {
			if (loggedInUser.getId().equalsIgnoreCase(userJob.get().getJobOwner().getId())) {
				userJob.get().setIsRead(true);
				Map<String, Object> map = new HashMap<>();
				map.put("success", true);
				map.put("message", "Request complete");
				this.userJobRepository.save(userJob.get());
				return ResponseEntity.status(HttpStatus.OK).body(map);
			} else {
				Map<String, Object> map = new HashMap<>();
				map.put("success", false);
				map.put("message", "No job ownership");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
			}
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Job does not exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

}

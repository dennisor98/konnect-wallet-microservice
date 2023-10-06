package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.EasyOnboardingRequestParams;
import net.sasakonnect.wallet.RequestDto.Mpesa;
import net.sasakonnect.wallet.RequestDto.OnboardingStatus;
import net.sasakonnect.wallet.RequestDto.TransactionPeriod;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.enums.NotificationBody;
import net.sasakonnect.wallet.enums.NotificationType;
import net.sasakonnect.wallet.enums.TransactionStatus;
import net.sasakonnect.wallet.enums.WalletTransactionType;
import net.sasakonnect.wallet.repository.UserWalletRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
public class WalletService extends JwtService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	UserService userService;
	@Autowired
	WalletRepository walletRepository;

	@Autowired
	UserWalletRepository userWalletRepository;

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

				reqId.put("startTime", 1680419930226L);
				reqId.put("endTime", 1687245530226L);
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

		var reqId = new HashMap<String, Object>();
		// reqId.put("onboardingRequestId", user.get().getOnboardingRequestId());
		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.DEPOSIT_FROM_MPESA).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object createNewOnBoardingUser(@Valid EasyOnboardingRequestParams easyOnboarding) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("firstName", easyOnboarding.getFirstName());
		userMap.put("middleName", easyOnboarding.getMiddleName());
		userMap.put("lastName", easyOnboarding.getLastName());
		userMap.put("birthday", easyOnboarding.getBirthday());
		userMap.put("gender", easyOnboarding.getGender());
		userMap.put("countryCode", easyOnboarding.getCountryCode());
		userMap.put("mobile", easyOnboarding.getMobile());
		userMap.put("idType", easyOnboarding.getIdTypeVerbal());
		userMap.put("idNumber", easyOnboarding.getIdNumber());
		userMap.put("kraPin", easyOnboarding.getKraPin());
		userMap.put("address", easyOnboarding.getMobile());
		userMap.put("employmentStatus", easyOnboarding.getEmploymentStatusType().getCode());
		userMap.put("monthlyIncome", easyOnboarding.monthlyIncomeType().getCode());
		var user = User.builder().firstName(easyOnboarding.getFirstName()).lastName(easyOnboarding.getLastName())
				.middleName(easyOnboarding.getMiddleName()).lastName(easyOnboarding.getLastName())
				.birthday(easyOnboarding.parseBithDay()).address(easyOnboarding.getAddress())
				.gender(easyOnboarding.getGenderVerbal()).countryCode(Integer.parseInt(easyOnboarding.getCountryCode()))
				.mobile(easyOnboarding.getMobile()).idType(easyOnboarding.getIdTypeEnum())
				.monthlyIncome(easyOnboarding.monthlyIncomeType()).kraPin(easyOnboarding.getKraPin())
				.employmentStatus(easyOnboarding.getEmploymentStatusType()).idNumber(easyOnboarding.getIdNumber())
				.build();
		try {
			final User savedUser = this.userService.createUser(user);
			userMap.put("userId", savedUser.getId());

			var reqs = this.requestSigner.signRequest(userMap);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.OPEN_WALLET_ACCOUNT).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);
			responseMono = responseMono.flatMap((String response) -> {
				// Check if "onboardingRequestId" is null in the response JSON
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					JsonNode responseJson = objectMapper.readTree(response);
					JsonNode onboardingRequestId = responseJson.path("data").path("onboardingRequestId");

					if (onboardingRequestId.isNull()) {
						// The "onboardingRequestId" is null, delete the user here
						this.userService.deleteUserById(savedUser.getId());
					} else {
						savedUser.setOnboardingRequestId(onboardingRequestId.asText());
						this.userService.updateUser(savedUser);
					}

					// Return the response as-is
					return Mono.just(response);
				} catch (JsonProcessingException e) {
					return Mono.just("Error response: " + e.getMessage());
				}
			}).onErrorResume(throwable -> {
				// Handle other errors here
				this.userService.deleteUserById(savedUser.getId());
				return Mono.just("Error response: " + throwable.getMessage());
			});
			String responseJson = responseMono.block();

			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}

			// TODO Auto-generated method stub
			return null;

		} catch (DataIntegrityViolationException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Account already exist");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		}
	}

	public Object onCallBackInvocation(JsonObject body) {
		// TODO Auto-generated method stub
		return this.callBackContentResolver(body);

	}

	private Object callBackContentResolver(JsonObject body) {
		var notification_Type = body.get("notificationType").getAsString();
		var params = body.getAsJsonObject("params");
		try {
			NotificationBody notificationBody = new Gson().fromJson(params, NotificationBody.class);

			if (notification_Type == NotificationType.ONBOARD.getCode()) {
				var user = this.userService.getUserById(params.get("userId").getAsString());
				if (notificationBody.getStatus() == 7) {
					var wallet = new Wallet();
					wallet.setAccountId(notificationBody.getAccountId());
					wallet.setAccountType(notificationBody.getAccountType());
					var savedwallet = this.walletRepository.save(wallet);
					var userWallet = new UserWallet();
					userWallet.setUser(user.get());
					userWallet.setWallet(savedwallet);
					this.userWalletRepository.save(userWallet);

				}
			} else if (notification_Type == NotificationType.ACCOUNT_STATEMENT.getCode()) {

			} else if (notification_Type == NotificationType.TRANSACTION.getCode()) {

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
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

}

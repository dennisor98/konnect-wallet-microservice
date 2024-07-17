package net.sasakonnect.wallet.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.BuyAirtime;
import net.sasakonnect.wallet.RequestDto.ChangePin;
import net.sasakonnect.wallet.RequestDto.ChoiceTransferDto;
import net.sasakonnect.wallet.RequestDto.EasyOnboardingRequestParams;
import net.sasakonnect.wallet.RequestDto.MobileVerifyDto;
import net.sasakonnect.wallet.RequestDto.Mpesa;
import net.sasakonnect.wallet.RequestDto.MpesaBilling;
import net.sasakonnect.wallet.RequestDto.OnBoardingStatusById;
import net.sasakonnect.wallet.RequestDto.OnboardingOtp;
import net.sasakonnect.wallet.RequestDto.OtpTransfer;
import net.sasakonnect.wallet.RequestDto.PayUtility;
import net.sasakonnect.wallet.RequestDto.PhoneCheckDto;
import net.sasakonnect.wallet.RequestDto.PinDto;
import net.sasakonnect.wallet.RequestDto.ResendTxOtpDto;
import net.sasakonnect.wallet.RequestDto.TransactionPeriod;
import net.sasakonnect.wallet.RequestDto.TransferToMpesa;
import net.sasakonnect.wallet.RequestDto.UpgradeWalletAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.RequestDto.account.UpdateEmailDto;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.TransactionMiddleware;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.NotificationService;
import net.sasakonnect.wallet.services.TarrifService;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletService;

@CustomController()

@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "Wallet routes")

public class WalletController {
	private final UserService userService;
	private final WalletService walletService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	TarrifService tarrifService;

	@Autowired
	NotificationService notificationService;

	public WalletController(UserService userService, WalletService walletService) {
		this.userService = userService;
		this.walletService = walletService;
	}

	@PostMapping("")
	public Object onBoarding(@Valid @RequestBody EasyOnboardingRequestParams easyOnboarding,
			@RequestHeader("app-version-number") String konnectHeader) {

		return this.walletService.createNewOnBoardingUser(easyOnboarding, konnectHeader);
	}

	@GetMapping("pin/set")
	public Object isPinSet() {
		return userService.isPinSet();
	}

	@PostMapping("confirm/onboarding/otp")
	public Object confirmOnboardingOtp(@Valid @RequestBody OnboardingOtp easyOnboarding) {
		return this.walletService.confirmOnboardingOtp(easyOnboarding);
	}

	@PostMapping("account/upgrade")
	public Object upgradeFromWalletToAccount(@Valid @RequestBody UpgradeWalletAccountDto upgradeWalletAccount) {
		return this.walletService.upgradeFromWalletToAccount(upgradeWalletAccount);
	}

	@GetMapping("resend/onboarding/otp")
	public Object resendOnboardingOtp() {
		return this.walletService.resendOnboardingOtp();
	}

	@GetMapping("/info")
	public ResponseEntity<Object> info() {
		return ResponseEntity.ok(this.walletService.getWalletInfo());
	}

	@PostMapping("/statement")
	@TransactionMiddleware()
	public ResponseEntity<Optional<User>> statement() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("/from/mpesa/deposit")
	public Object depositFromMpesa(@Valid @RequestBody Mpesa mpesa) {
		return this.walletService.loadWalletFromMpesa(mpesa);
	}

	@PostMapping("/registerEmail")
	public Object registerUserEmail(@Valid @RequestBody UpdateEmailDto email) {
		return this.walletService.updateUserEmail(email.getEmail());
	}

	@PostMapping("/to/mpesa")
	@TransactionMiddleware()
	public Object toMpesa(@Valid @RequestBody TransferToMpesa mpesa) {
		return this.walletService.sendToMpesa(mpesa);
	}

	@PostMapping("buy/airtime")
	@TransactionMiddleware()
	public Object payUtility(@RequestBody() @Valid BuyAirtime buyAirtime) {
		return this.walletService.airtimePayment(buyAirtime);
	}

	@PostMapping("mpesa/payments")
	@TransactionMiddleware()
	public Object mpesaPayments(@RequestBody() @Valid MpesaBilling tillAndBuyGoods) {
		return this.walletService.mpesaTillAndByGoods(tillAndBuyGoods);
	}

	@GetMapping("balance")
	public Object balance() {

		return this.walletService.getWalletBalance();
	}

	@GetMapping("account/state")
	public Object accountState() {
		return this.walletService.getAccountStatus();
	}

	@PostMapping("window/period")
	public Object windowPeriod(@Valid @RequestBody PinDto setPin) {
		return this.userService.createWindowPeriod(setPin);
	}

	@PostMapping("confirm/otp/transfer")

	public Object confirmTransfer(@Valid @RequestBody OtpTransfer otpTransfer) {
		return this.walletService.confirmOtpTransfer(otpTransfer);
	}

	@PostMapping("resendOtp")
	public ResponseEntity resendOtp(@Valid @RequestBody() ResendTxOtpDto resendDto) {
		return this.walletService.resendTransactionOtp(resendDto);
	}

//	@GetMapping("pin/reset/otp")
//	public ResponseEntity<Optional<User>> resetPin() {
//		return ResponseEntity.ok(userService.getUserById("0"));
//	}

	@PostMapping("/confirmOtp")
	public ResponseEntity<Optional<User>> confirmOtp() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("/setPin")
	public Object setPin(@Valid @RequestBody PinDto setPin) {
		return userService.setPin(setPin);
	}

	@PostMapping("/changePin")
	public Object changePin(@Valid @RequestBody ChangePin changePin) {
		return userService.changePing(changePin);
	}

	@GetMapping("/transactionhistory")
	public Object getTransactionHistory(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		return ResponseEntity.ok(transactionService.getUserTransactionHistory(pageNumber, pageSize));
	}
//	public ResponseEntity<Object> transactionHistory() {
//		return ResponseEntity.ok(this.walletService.getTransactionHistory());
//	}

	@PostMapping("/transactionhistory/any")
	public Object transactionHistoryAny(@Valid @RequestBody TransactionPeriod changePin) {
		return walletService.getTransactionHistoryAsOf(changePin);
	}

	@GetMapping("applyForShortCode")
	public ResponseEntity<Optional<User>> applyForShortCode() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("getShortCode")
	public ResponseEntity<Optional<User>> getShortCode() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("bankCode")
	public Object bankCode() {
		return this.walletService.getBankCode();
	}

	@PostMapping("sendToOtherInstitution")
	@TransactionMiddleware()
	public Object sendToOtherWallet(@RequestBody() @Valid() ChoiceTransferDto choiceTransfer) {
		return this.walletService.applyForTransfer(choiceTransfer);
	}

	@PostMapping("sendToWallet")
	@TransactionMiddleware()
	public Object sendToWallet(@RequestBody() @Valid() WalletTransferDto choiceTransfer) {
		// return "error";
		return this.walletService.applyForWalletToWallet(choiceTransfer);
	}

	@PostMapping("checkAccount")

	public Object checkAccount(@RequestBody() @Valid() PhoneCheckDto phoneCheck) {
		// return "error";
		return this.walletService.checkUserPublicAccount(phoneCheck);
	}

	@PostMapping("pay/utility")
	@TransactionMiddleware()
	public Object payUtility(@RequestBody() @Valid() PayUtility payUtility) {
		return this.walletService.payUtility(payUtility);
	}

	@GetMapping("getOnboardingStatus")
	public Object getOnboardingStatus() {
		return this.walletService.getOnboardingStatus();
	}

	@PostMapping("getOnboardingStatusById")
	public Object getOnboardingStatus(@RequestBody() @Valid() OnBoardingStatusById onBoarding) {
		return this.walletService.getOnboardingStatus(onBoarding.getOnBoardingId());
	}

	@GetMapping("transaction/summary")
	public ResponseEntity<Object> getTransactionsSummary() {
		return this.transactionService.getWalletTransactionBreakdown();
	}

	@GetMapping("spending")
	public ResponseEntity<Object> getTransactionBehaviour(
			@RequestParam(name = "month", required = false, defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) int month,
			@RequestParam(name = "year", required = false, defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
		return this.transactionService.getWalletTransactionBehaviour(year, month);
	}

	@PostMapping("account/statement")
	@Operation(summary = "Get account statement", description = "Get account statement between start and end dates")

	public Object getAccountStatement(
			@Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-02-01") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date (YYYY-MM-DD)", example = "2024-02-29") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return walletService.getUserStatement(startDate, endDate);
	}

	@GetMapping("account/statements")
	@Operation(summary = "Get requested account statements", description = "Get statemenets requested by user")

	public Object getRequestedStatemens() {
		return walletService.getUserRequestedstatements();
	}

	@PutMapping("account/statement/updateread")
	public Object updateStatementRead(@RequestParam(name = "jobId") String jobId) {
		return walletService.updateStamentRead(jobId);
	}

	@GetMapping("/transaction/recentContact")
	public ResponseEntity<Object> getRecentTransactionContact(@RequestParam(name = "txType") String txType,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		return this.walletService.getRecentTransactionContact(txType, pageNumber, pageSize);
	}

	@GetMapping("/transaction/recentContact/search")
	public ResponseEntity<Object> searchRecentTransactionContact(@RequestParam(name = "queryString") String searchTerm,
			@RequestParam(name = "txType", required = false) String txType,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		return this.walletService.searchRecentTransactionContact(searchTerm, txType, pageNumber, pageSize);
	}

	@GetMapping("/tarrif/cost")
	public ResponseEntity<Object> getCostFor(@RequestParam(name = "amount") double amount,
			@RequestParam(name = "opponentAccount") String opponentAccount,
			@RequestParam(name = "channel") ChannelType channelType) {
		return this.tarrifService.getCostOn(channelType, amount, opponentAccount);
	}

	@GetMapping("/notification")
	public ResponseEntity<Object> getNotifications(@RequestParam(name = "isRead", required = false) Boolean isRead,
			@RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		if (isRead != null) {
			return this.notificationService.filterNotificationsByReadstatus(isRead, pageNumber, pageSize);
		}
		return this.notificationService.getUserNotifications(pageNumber, pageSize);
	}

	@PutMapping("/notification/read/update")
	public ResponseEntity<Object> updateNotificationRead(
			@RequestParam(name = "notificationId", required = false) String notificationId) {
		if (notificationId == null) {
			return this.notificationService.setAllAsRead();
		} else {
			return this.notificationService.setAsRead(notificationId);
		}

	}
	
	@GetMapping("/transaction/mobile/verify")
	public ResponseEntity<Object> verifyMobileNumber(@RequestParam(name="countryCode",defaultValue="254") String countryCode,@RequestParam("mobileNumber") String mobileNumber){
		return this.walletService.verifyTransactionContact(countryCode,mobileNumber);
	}

}

package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.RequestDto.ChangePin;
import net.sasakonnect.wallet.RequestDto.EasyOnboardingRequestParams;
import net.sasakonnect.wallet.RequestDto.Mpesa;
import net.sasakonnect.wallet.RequestDto.OnBoardingOtp;
import net.sasakonnect.wallet.RequestDto.PinDto;
import net.sasakonnect.wallet.RequestDto.TransactionPeriod;
import net.sasakonnect.wallet.RequestDto.TransferToMpesa;
import net.sasakonnect.wallet.annotations.TransactionMiddleware;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletService;

@CustomController()

@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "Wallet routes")

public class WalletController {
	private final UserService userService;
	private final WalletService walletService;

	public WalletController(UserService userService, WalletService walletService) {
		this.userService = userService;
		this.walletService = walletService;
	}

	@PostMapping("")
	public Object onBoarding(@Valid @RequestBody EasyOnboardingRequestParams easyOnboarding) {
		return this.walletService.createNewOnBoardingUser(easyOnboarding);
	}

	@GetMapping("pin/set")
	public Object isPinSet() {
		return userService.isPinSet();
	}

	@PostMapping("confirm/onboarding/otp")
	public Object confirmOnboardingOtp(@Valid @RequestBody OnBoardingOtp easyOnboarding) {
		return this.walletService.confirmOnboardingOtp(easyOnboarding);
	}

	@GetMapping("resend/onboarding/otp")
	public Object resendOnboardingOtp() {
		return this.walletService.resendOnboardingOtp();
	}

	@PostMapping("/messaging/token")
	public ResponseEntity<Optional<User>> token() {
		return ResponseEntity.ok(userService.getUserById("0"));
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

	@PostMapping("/to/mpesa")
	@TransactionMiddleware()
	public Object toMpesa(@Valid @RequestBody TransferToMpesa mpesa) {
		return this.walletService.sendToMpesa(mpesa);
	}

	@PostMapping("buy/airtime")
	@TransactionMiddleware()
	public ResponseEntity<Optional<User>> buyAirtime() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("balance")
	public Object balance() {

		return this.walletService.getWalletBalance();
	}

	@GetMapping("account/state")
	public ResponseEntity<Optional<User>> accountState() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("window/period")
	public Object windowPeriod(@Valid @RequestBody PinDto setPin) {
		return this.userService.createWindowPeriod(setPin);
	}

	@PostMapping("confirm/otp/transfer")
	public ResponseEntity<Optional<User>> confirmTransfer() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("resendOtp")
	public ResponseEntity<Optional<User>> resendOtp() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("pin/reset/otp")
	public ResponseEntity<Optional<User>> resetPin() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

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
	public ResponseEntity<Object> transactionHistory() {
		return ResponseEntity.ok(this.walletService.getTransactionHistory());
	}

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

	@GetMapping("sendToOtherWallet")
	@TransactionMiddleware()
	public Object sendToOtherWallet() {
		return this.walletService.sendToOtherWallet();
	}

}

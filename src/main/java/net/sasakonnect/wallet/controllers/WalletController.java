package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.CustomController;
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
	public ResponseEntity<Optional<User>> onBoarding() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("pin/set")
	public Object isPinSet() {
		return userService.isPinSet();
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
	public ResponseEntity<Optional<User>> statement() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("/from/mpesa/deposit")
	public ResponseEntity<Optional<User>> depositFromMpesa() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("/to/mpesa")
	public ResponseEntity<Optional<User>> toMpesa() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("buy/airtime")
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

	@GetMapping("window/period")
	public ResponseEntity<Optional<User>> windowPeriod() {
		return ResponseEntity.ok(userService.getUserById("0"));
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
	public ResponseEntity<Optional<User>> setPin() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("/changePin")
	public ResponseEntity<Optional<User>> changePin() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("/transactionhistory")
	public ResponseEntity<Object> transactionHistory() {
		return ResponseEntity.ok(this.walletService.getTransactionHistory());
	}

	@PostMapping("/transactionhistory/any")
	public ResponseEntity<Optional<User>> transactionHistoryAny() {
		return ResponseEntity.ok(userService.getUserById("0"));
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
	public ResponseEntity<Optional<User>> bankCode() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

}

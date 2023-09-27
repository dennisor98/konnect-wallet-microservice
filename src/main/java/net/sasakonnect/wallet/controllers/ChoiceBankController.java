package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;

@CustomController()
@Tag(name = "Choice-Bank", description = "Bank Routes routes")

public class ChoiceBankController {
	private final UserService userService;

	public ChoiceBankController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("choice-bank/onboarding/status")
	public ResponseEntity<Optional<User>> onBoardingStatus() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("choice-bank")
	public ResponseEntity<Optional<User>> getBanks() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("konnect/callBack")
	public ResponseEntity<Optional<User>> konnectCallBack() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

}

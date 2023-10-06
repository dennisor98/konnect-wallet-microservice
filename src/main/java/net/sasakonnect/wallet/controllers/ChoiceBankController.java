package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.RequestDto.OnboardingStatus;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletService;

@CustomController()
@Tag(name = "Choice-Bank", description = "Bank Routes routes")

public class ChoiceBankController {

	private final UserService userService;
	private final WalletService walletService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public ChoiceBankController(UserService userService, WalletService walletService) {
		this.userService = userService;
		this.walletService = walletService;
	}

	@GetMapping("choice-bank")
	public ResponseEntity<Optional<User>> getBanks() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@PostMapping("konnect/callBack")
	public Object konnectCallBack(@RequestBody JsonObject body) {
		return this.walletService.onCallBackInvocation(body);
	}

	@PostMapping("choice-bank/onboarding/status")

	public Object getOnboardingStatus(@Valid @RequestBody OnboardingStatus onboardingStatus) {
		return this.walletService.getOnBoardingStatus(onboardingStatus);

	}

}

package net.sasakonnect.wallet.controllers;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
	@Value("${BANK_ALLOWED_IPS}")
	private String allowedIps;
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
	public Object konnectCallBack(@RequestBody JsonObject body, HttpServletRequest request) {
		String remoteIpAddress = request.getRemoteAddr();
		var collectors = Stream.of(this.allowedIps.split(",")).filter(ip -> {
			return remoteIpAddress.equalsIgnoreCase(ip);

		}).collect(Collectors.toList());
		if (!collectors.isEmpty()) {
			return this.walletService.onCallBackInvocation(body);

		}
		System.err.println("Someunknow ips" + remoteIpAddress);
		return null;

	}

	@PostMapping("choice-bank/onboarding/status")

	public Object getOnboardingStatus(@Valid @RequestBody OnboardingStatus onboardingStatus) {
		return this.walletService.getOnBoardingStatus(onboardingStatus);

	}

}

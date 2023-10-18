package net.sasakonnect.wallet.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.provider.Firebase;
import net.sasakonnect.wallet.provider.FirebaseMessage;
import net.sasakonnect.wallet.provider.FirebaseWrapper;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.redis.JobProducer;
import net.sasakonnect.wallet.tools.redis.Queueable;

@RequestMapping("user")
@CustomController()
@Tag(name = "User", description = "User routes")

public class UserController {
	private final UserService userService;
	@Autowired
	private JobProducer<Queueable<List<FirebaseMessage>>> jobProducer;
	@Autowired
	FirebaseWrapper firebaseWrapper;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("userLogin")
	public ResponseEntity<ObjectNode> getAll(@Valid @RequestBody UserLogin loginDto) {
		Queueable<List<FirebaseMessage>> myBean = new Firebase(firebaseWrapper);
		myBean.params = new ArrayList<FirebaseMessage>();
		myBean.params.add(FirebaseMessage.builder().message("hello this").token("yes").build());

		this.jobProducer.enqueueJob("firebase", myBean);
		return userService.userLogin(loginDto);
	}

	@PostMapping("confirmOtp")
	public ResponseEntity confirmOtp(@Valid @RequestBody ConfirmOtp confirmOtp) {
		return userService.verifyOtp(confirmOtp);
	}

//	@PostMapping("/userLogin")
//	public Optional<UserLoginResponse> userLogin(@Valid @RequestBody UserLogin userLogin, BindingResult bindingResult)
//			throws UserInputException {
//
//		return Optional.of(userService.userLogin(userLogin));
//
//	}
//
//	@PostMapping("/register")
//	public Optional<Object> userRegister(@Valid @RequestBody UserSignUp userSignUp, BindingResult bindingResult)
//			throws UserInputException {
//
//		return Optional.ofNullable(userService.userRegister(userSignUp));
//
//	}
//
//	@GetMapping("/account")
//	@PreAuthorize("hasPermission(#apartmentId, 'view.account')")
//	@RequirePermission("view.account")
//	public String account(@Valid @RequestBody AccountRequestDto accountRequest) throws AccountNotFoundException {
//		// Logic to retrieve targetDomainObject
//		// For example: String targetDomainObject = someService.getTargetDomainObject();
//
//		return "account: ";
//	}

}

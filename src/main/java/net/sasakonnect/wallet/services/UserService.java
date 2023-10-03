package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.ResponseDto.UserResponseDTO;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserPin;
import net.sasakonnect.wallet.repository.PermissionRepository;
import net.sasakonnect.wallet.repository.RolePermissionRepository;
import net.sasakonnect.wallet.repository.RoleRepository;
import net.sasakonnect.wallet.repository.UserPinRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.UserRoleRepository;
import net.sasakonnect.wallet.tools.JwtService;

@Service
public class UserService extends RestClientService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SmsService smsService;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private UserPinRepository userPinRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private JwtService jwtService;

	public Optional<User> getUserById(String id) {
		return this.userRepository.findById(id);

	}

	public Optional<Role> getUserRoleByUserId(String id) {

		List<Role> roles = this.userRoleRepository.findRolesByUserId(id);
		return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
	}

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		List<Role> roles = this.userRoleRepository.findRolesByUserId(user.getId());
		Set<SimpleGrantedAuthority> simple = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName())).collect(Collectors.toSet());
		user.setSimple(simple);

		// TODO Auto-generated method stub
		return user;
	}

	public ResponseEntity<ObjectNode> userLogin(UserLogin userLogin) {
		var user = this.userRepository.findByMobileAndCountryCode(userLogin.getPhoneNumber(),
				Integer.valueOf(userLogin.getCountryCode()));
		if (user.isEmpty()) {
			ObjectMapper objectMapper = new ObjectMapper();

			ObjectNode json = JsonNodeFactory.instance.objectNode();
			ArrayNode arrayNode = objectMapper.createArrayNode();
			arrayNode.add("User not Found");
			json.put("statusCode", false);
			json.putIfAbsent("message", arrayNode);
			json.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json);

		} else {

			return this.smsService.sendSms(userLogin, null, user);

		}

//		{
//			  "hash": "c068e38b5bc9d237e3b0007d5eb0d485b07ddde7c77a57a13865da4ffe843943",
//			  "message": "Otp sent please wait for 299.789 seconds  before requesting",
//			  "success": true
//			}

//		if (user.isPresent()
//				&& new BCryptPasswordEncoder().matches(userLogin.getPassword(), user.get().getPassword())) {
//			User u = user.get();
//			return UserLoginResponse.builder().token(jwtService.generateToken(u))
//					.refreshToken(jwtService.generateRefreshToken(u))
//
//					.user(UserDto.builder().address(u.getAddress()).firstName(u.getFirstName())
//							.lastName(u.getLastName()).mobile(u.getMobile()).email(u.getEmail())
//							.countryCode(u.getCountryCode()).build()
//
//					).build();
//
//		}

	}

	public boolean findPermissionByRoleName(Optional<Role> role, Object permission) {
		List<Permission> permissions = this.rolePermissionRepository.findPermissionsByRoleAndPermissionName(role.get(),
				(String) permission);
		if (permissions.isEmpty()) {
			return false;
		}
		return true;
	}

	public ResponseEntity<Object> verifyOtp(@Valid ConfirmOtp confirmOtp) {
		var opt = this.smsService.verifyOtp(confirmOtp);
		if (opt.isPresent()) {
			if (opt.get().isValid()) {

				ObjectNode json = JsonNodeFactory.instance.objectNode();
				json.put("message", "otp code is Invalid");
				return ResponseEntity.badRequest().body(json);

			}
			var u = opt.get().getUser();
			if (u != null) {

				var response = UserResponseDTO.builder().token(jwtService.generateToken(u))
						.refreshToken(jwtService.generateRefreshToken(u)).middleName(u.getMiddleName())
						.gender(u.getGender().name()).idType(u.getIdType().name()).idNumber(u.getIdNumber())
						.onboardingRequestId(u.getOnboardingRequestId()).birthday(u.getBirthday().toString())
						.updatedAt(u.getUpdatedAt().toInstant()).kraPin(u.getKraPin())
						.employmentStatus(u.getEmploymentStatus().name()).monthlyIncome(u.getMonthlyIncome().toString())
						.createdAt(u.getCreatedAt().toInstant()).id(u.getId())

						.address(u.getAddress()).firstName(u.getFirstName()).lastName(u.getLastName())
						.mobile(u.getMobile()).countryCode(u.getCountryCode()).build();
				return ResponseEntity.ok(response);

			}
		} else {
			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("message", "otp code is Invaliddd");
			return ResponseEntity.badRequest().body(json);
		}
		return null;
		// TODO Auto-generated method stub
	}

	public Optional<User> findUserWallet(User user) {
		return this.userRepository.findUserWithUserWalletsById(user.getId());
		// TODO Auto-generated method stub

	}

	public Optional<User> findUserWallet(String id) {
		return this.userRepository.findUserWithUserWalletsById(id);
		// TODO Auto-generated method stub

	}

	public Object isPinSet() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<List<UserPin>> userPins = this.userPinRepository.getUserPinThatIsNotArchived(user);

		if (userPins.isPresent() && (userPins.get().size() > 0)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Account Ready");
			return ResponseEntity.ok(map);
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Pin not set");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);

		}

		// TODO Auto-generated method stub
	}

	public Object findDeletedPinsForUser() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.err.print("sdfjksdafnsdlkfjnskjfnkdsalfndjslfsd");
		return null;
	}

//	public Object userRegister(@Valid UserSignUp userSignUp) throws UserInputException {
//		Optional<User> userPhone = this.userRepository.findByMobile(userSignUp.getPhoneNumber());
//		Optional<User> userEmail = this.userRepository.findByEmail(userSignUp.getEmail());
//
//		if (userPhone.isPresent()) {
//			throw new UserInputException(HttpStatus.CONFLICT, "Phone already registered");
//
//		}
//		if (userEmail.isPresent()) {
//			throw new UserInputException(HttpStatus.CONFLICT, "Email already registered");
//		}
//
//		User user = User.builder().firstName(userSignUp.getFirstName()).lastName(userSignUp.getLastName())
//				.mobile(userSignUp.getPhoneNumber()).email(userSignUp.getEmail()).countryCode("+254")
//				.password(new BCryptPasswordEncoder().encode(userSignUp.getPassword())).build();
//		return this.userRepository.save(user);
//		// TODO Auto-generated method stub
//
//	}
}

package net.sasakonnect.wallet.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.ChangePin;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.PinDto;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.ResponseDto.UserResponseDTO;
import net.sasakonnect.wallet.beans.BankWebClientBean;
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
	@Autowired
	BankWebClientBean bankClientBean;
	@Value("${MAX_PIN_ATTEMPT:3}")
	private int maxpinattempt;

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
			json.put("message", "otp code is Invalid");
			return ResponseEntity.badRequest().body(json);
		}
		return null;
		// TODO Auto-generated method stub
	}

	public Object createJwtFor(User u) {
		var response = UserResponseDTO.builder().token(jwtService.generateToken(u))
				.refreshToken(jwtService.generateRefreshToken(u)).middleName(u.getMiddleName())
				.gender(u.getGender().name()).idType(u.getIdType().name()).idNumber(u.getIdNumber())
				.onboardingRequestId(u.getOnboardingRequestId()).birthday(u.getBirthday().toString())
				.updatedAt(u.getUpdatedAt().toInstant()).kraPin(u.getKraPin())
				.employmentStatus(u.getEmploymentStatus().name()).monthlyIncome(u.getMonthlyIncome().toString())
				.createdAt(u.getCreatedAt().toInstant()).id(u.getId())

				.address(u.getAddress()).firstName(u.getFirstName()).lastName(u.getLastName()).mobile(u.getMobile())
				.countryCode(u.getCountryCode()).build();
		return ResponseEntity.ok(response);
	}

	public Optional<User> findUserWallet(User user) {
		return this.userRepository.findUserWithUserWalletsById(user.getId());
		// TODO Auto-generated method stub

	}

	public Optional<User> findUserWallet(String id) {
		return this.userRepository.findUserWithUserWalletsById(id);
		// TODO Auto-generated method stub

	}

	public ResponseEntity<Map> isPinSet() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<List<UserPin>> userPins = this.userPinRepository.getUserPinThatIsNotArchived(user);

		if (userPins.isPresent() && (userPins.get().size() > 0)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Account State Valid");
			map.put("success", "true");
			return ResponseEntity.ok(map);
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Pin not set");
			map.put("success", "false");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);

		}

		// TODO Auto-generated method stub
	}

	public Object findDeletedPinsForUser() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.err.print("sdfjksdafnsdlkfjnskjfnkdsalfndjslfsd");
		return null;
	}

	public Object setPin(@Valid PinDto setPin) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<List<UserPin>> userPins = this.userPinRepository.getUserPinThatIsNotArchived(user);

		if (userPins.isPresent() && (userPins.get().size() > 0)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Pin already set please ,try to reset");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		} else {
			var passwordencoded = new BCryptPasswordEncoder().encode(user.getId() + setPin.getPin());
			var userpin = new UserPin();
			userpin.setUser(user);
			userpin.setPin(passwordencoded);
			this.userPinRepository.save(userpin);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "You pin has been set");
			map.put("success", true);
			return ResponseEntity.status(HttpStatus.OK).body(map);

		}
	}

	@Transactional
	public Object changePing(@Valid ChangePin setPin) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<List<UserPin>> userPins = this.userPinRepository.getUserPinThatIsNotArchived(user);
		if (userPins.isPresent() && (userPins.get().size() > 0)) {
			var pins = this.userPinRepository.findPinsUsedWithinLastThreeMonths(user.getId(), this.threeMonthsAgo());
			var encoder = new BCryptPasswordEncoder();
			if (pins.isPresent()) {
				// check if pin have being used for the pass three months
				var lasthreemontpin = pins.get().stream()
						.filter((data) -> encoder.matches(user.getId() + setPin.getPin(), data.getPin()))
						.collect(Collectors.toList());
				if (!lasthreemontpin.isEmpty()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("message", "Please use a pin you have not used in the past three months");
					map.put("success", false);
					return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);
				} else {
					var activeUserPin = userPins.get().get(0);
					if (encoder.matches(user.getId() + setPin.getOldPin(), activeUserPin.getPin())) {
						var passwordencoded = new BCryptPasswordEncoder().encode(user.getId() + setPin.getPin());
						var userpin = new UserPin();
						userpin.setUser(user);
						userpin.setPin(passwordencoded);
						this.userPinRepository.markUserPinAsDeleted(activeUserPin.getId());
						this.userPinRepository.save(userpin);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("message", "Pin changed Successfully");
						map.put("success", true);
						return ResponseEntity.status(HttpStatus.OK).body(map);

					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("message", "Old pin mismatch ");
						map.put("success", false);
						return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);

					}

					// compare the old pin with the existing
				}

			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Unknown Error while changing pin ");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);

		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Please set you Pin first");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);

		}

	}

	private Date threeMonthsAgo() {
		LocalDate currentDate = LocalDate.now();
		LocalDate threeMonthsAgoDate = currentDate.minusMonths(3);
		Date threeMonthsAgo = Date.from(threeMonthsAgoDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return threeMonthsAgo;
	}

	public Object createWindowPeriod(@Valid PinDto setPin) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var bycryp = new BCryptPasswordEncoder();
		var userPinRepository = this.userPinRepository.getUserPinThatIsNotArchived(user);
		if (userPinRepository.isPresent() && userPinRepository.get().size() > 0
				&& !(userPinRepository.get().get(0).getPinAttempts() >= maxpinattempt)) {
			if (bycryp.matches(user.getId() + setPin.getPin(), userPinRepository.get().get(0).getPin())) {
				var token = this.jwtService.generateTokenForWindow(user);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("window", token);
				map.put("success", true);
				this.userPinRepository.resetPinAttempts(user);

				return ResponseEntity.status(HttpStatus.OK).body(map);
			} else {
				this.userPinRepository.incrementPinAttempts(user);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "Pin Entered does not match");
				map.put("success", false);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
			}
		} else {
			Map<String, Object> map = new HashMap<String, Object>();

			if (userPinRepository.isPresent()) {
				if ((!(userPinRepository.get().isEmpty())
						&& userPinRepository.get().get(0).getPinAttempts() >= maxpinattempt)) {
					map.put("message", "Pin Blocked");

				} else {
					map.put("message", "Pin not set");

				}
			}

			map.put("success", false);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
		}

		// TODO Auto-generated method stub
	}

	public User createUser(User user) {
		return this.userRepository.save(user);

	}

	public void deleteUserById(String id) {
		this.userRepository.deleteById(id);

	}

	public User updateUser(User savedUser) {
		return this.userRepository.save(savedUser);
		// TODO Auto-generated method stub

	}

	public void deletUserByOnboardingRequestId(String onboardingRequestId) {
		this.userRepository.deleteByOnboardingRequestId(onboardingRequestId);
		// TODO Auto-generated method stub

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

package net.sasakonnect.wallet.services;

import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ChangePin;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.OpenIdRequest;
import net.sasakonnect.wallet.RequestDto.PhoneCountryPair;
import net.sasakonnect.wallet.RequestDto.PinDto;
import net.sasakonnect.wallet.RequestDto.SdkSearchCustomers;
import net.sasakonnect.wallet.RequestDto.UserDeviceToken;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.ResponseDto.UserResponseDTO;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.beans.RedisBean;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.FirebaseToken;
import net.sasakonnect.wallet.domain.Logs;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.ProfileImage;
import net.sasakonnect.wallet.domain.RejectedAccount;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserPin;
import net.sasakonnect.wallet.domain.UserRole;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.domain.WalletAccountUpgrade;
import net.sasakonnect.wallet.enums.LogTypes;
import net.sasakonnect.wallet.notification.WalletAccountUpgradeResultNotification;
import net.sasakonnect.wallet.repository.CorporateDetailsRepository;
import net.sasakonnect.wallet.repository.FirebaseTokenRepository;
import net.sasakonnect.wallet.repository.LogsRepository;
import net.sasakonnect.wallet.repository.PermissionRepository;
import net.sasakonnect.wallet.repository.ProfileImageRepository;
import net.sasakonnect.wallet.repository.RejectedAccountRepository;
import net.sasakonnect.wallet.repository.RolePermissionRepository;
import net.sasakonnect.wallet.repository.RoleRepository;
import net.sasakonnect.wallet.repository.UserPinRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.UserRoleRepository;
import net.sasakonnect.wallet.repository.WalletAccountUpgradeRepository;
import net.sasakonnect.wallet.repository.WalletClientRepository;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;

@Service
@Slf4j
public class UserService extends RestClientService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FirebaseTokenRepository firebaseTokenRepository;
	@Autowired
	private WalletAccountUpgradeRepository walletAccountUpgradeRepository;

	@Autowired
	private OtpSmsService otpsmsService;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private UserPinRepository userPinRepository;
	@Autowired
	CorporateDetailsRepository corporateRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private WalletClientRepository walletClientRepository;
	@Autowired
	private JwtService jwtService;

	@Autowired
	private LarkService larkService;

	@Autowired
	WalletRepository walletRepository;
	
	@Autowired
	RejectedAccountRepository  rejectedAccountRepository;
	
	@Autowired
	LogsRepository logsRepository;

	@Autowired
	BankWebClientBean bankClientBean;
	@Value("${MAX_PIN_ATTEMPT:3}")
	private int maxpinattempt;
	
	@Value("${PROFILE_IMAGE_PATH}")
    private Path profileImageDir;
	
	 private final int compressedImageWidth = 300; // Adjust the width as needed
	 private final float imageQuality = 0.5f;
    
	@Value("${spring.profiles.active}")
	String profileActive;
	
	@Value("${WALLET_BASE_URL}")
	String wallet_base_url;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
	@Autowired
	private RedisBean<String> redisBean;
	@Autowired
	RequestSigner requestSigner;
	
	@Autowired
	ProfileImageRepository profileImageRepository;

//	public UserService() {
//        try {
//            Files.createDirectories(this.profileImageDir);
//        } catch (Exception ex) {
//            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
//        }	
//	}
//	
	public ResponseEntity<Object> getAllUsers(Integer pageNumber, Integer pageSize) {
		Map<String, Object> resObject = new HashMap<String, Object>();
		Map<String, Object> payloadMap = new HashMap<>();
		try {
			Page<User> user = this.userRepository.findAllusers(PageRequest.of(pageNumber, pageSize));
//			var us = user.get();
//			log.error("users" + us.size());

			var usermaps = user.stream().map(u -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", u.getId());
				map.put("firstname", u.getFirstName());
				map.put("middlename",u.getMiddleName());
				map.put("lastname", u.getLastName());
				map.put("user_id", u.getId());
				map.put("phone", u.getMobile());
				map.put("status", u.getStatus());
//	            map.put("wallet", u.getUserWallets());
				map.put("corporate", u.getCorporate());
				if (u.getUserRole() != null) {
					map.put("role", u.getUserRole().getRole());

				} else {
					map.put("role", null);

				}
				if (u.getUserWallets() != null && !u.getUserWallets().isEmpty()) {
					map.put("wallet", u.getUserWallets().get(0).getWallet());
				} else {
					map.put("wallet", "null");
				}
				// Add other mappings as needed
				return map;

			}).collect(Collectors.toList());

			payloadMap.put("success", "true");
			payloadMap.put("totalRows", Double.valueOf(user.getTotalElements()));
			payloadMap.put("pageSize", user.getSize());
			payloadMap.put("currentPage", user.getNumber());
			payloadMap.put("hasMore", user.hasNext() ? true : false);
			payloadMap.put("nextPage", user.hasNext() ? user.nextPageable().getPageNumber() : null);
			payloadMap.put("hasNextPage", user.hasNext());
			payloadMap.put("hasPreviousPage", user.hasPrevious());
			payloadMap.put("users", usermaps);
			resObject.put("payload", payloadMap);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(resObject);
		} catch (Exception e) {
			payloadMap.put("success", "false");
			payloadMap.put("message", "A system error occured");
			payloadMap.put("error", e.getMessage());
			resObject.put("payload", payloadMap);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObject);
		}

	}

	public ResponseEntity<Object> searchUser(String queryString, Integer pageNumber, Integer pageSize) {
		Page<User> user = this.userRepository.searchUser(queryString, PageRequest.of(pageNumber, pageSize));
		if (!user.isEmpty()) {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> resObject = new HashMap<>();
			var res = user.stream().map(u -> {
				Map<String, Object> usermap = new HashMap<>();
				usermap.put("id", u.getId());
				usermap.put("firstname", u.getFirstName());
				usermap.put("lastname", u.getLastName());
				usermap.put("middlename",u.getMiddleName());	
				usermap.put("user_id", u.getId());
				usermap.put("phone", u.getMobile());
//            map.put("wallet", u.getUserWallets());
				usermap.put("corporate", u.getCorporate());
				if (u.getUserRole() != null) {
					usermap.put("role", u.getUserRole().getRole());

				} else {
					usermap.put("role", null);

				}
				if (u.getUserWallets() != null && !u.getUserWallets().isEmpty()) {
					usermap.put("wallet", u.getUserWallets().get(0).getWallet());
				} else {
					usermap.put("wallet", "null");
				}
				return usermap;
			}).collect(Collectors.toList());
			map.put("success", "true");
			map.put("totalRows", Double.valueOf(user.getTotalElements()));
			map.put("pageSize", user.getSize());
			map.put("currentPage", user.getNumber());
			map.put("hasMore", user.hasNext() ? true : false);
			map.put("nextPage", user.hasNext() ? user.nextPageable().getPageNumber() : null);
			map.put("hasNextPage", user.hasNext());
			map.put("hasPreviousPage", user.hasPrevious());
			map.put("message", "Request successful");
			map.put("users", res);
			resObject.put("payload", map);
			return ResponseEntity.status(HttpStatus.OK).body(resObject);

		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "User not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);

		}
	}

	public Object getCorporateUsers() {
		Map<String, Object> resObject = new HashMap<String, Object>();
		try {
			Optional<List<User>> user = this.userRepository.getCorporateUsers();
			List<Map<String, Object>> userMaps = user.orElse(Collections.emptyList()).stream().map(u -> {
				Map<String, Object> map = new HashMap<>();
				map.put("firstname", u.getFirstName());
				map.put("lastname", u.getLastName());
				map.put("user_id", u.getId());
				map.put("phone", u.getMobile());
				map.put("corporate", u.getCorporate());
				if (u.getUserRole() != null) {
					map.put("role", u.getUserRole().getRole());

				} else {
					map.put("role", null);

				}
				return map;
			}).collect(Collectors.toList());
			resObject.put("success", "true");
			resObject.put("payload", userMaps);
			return resObject;
		} catch (Exception ex) {
			resObject.put("success", "false");
			resObject.put("message", "An error occured");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObject);
		}
//			  return this.userRepository.getCorporateUsers();	  
	}

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
		/**
		 * This is to allow Google to have a test account if you find a better way why
		 * not change? so google play team will use 700000000 as phone number and 1234
		 * as otp
		 */
		Optional<User> user = Optional.empty();

		if (profileActive.equalsIgnoreCase("dev")) {
			if (userLogin.getPhoneNumber().equalsIgnoreCase("700000000")) {
				log.debug("fing this phone 703454954 and country code" + userLogin.getCountryCode());

				user = this.userRepository.findByMobileAndCountryCode("703454954",
						Integer.valueOf(userLogin.getCountryCode()));
			} else {
				user = this.userRepository.findByMobileAndCountryCode(userLogin.getSerchablePhone(),
						Integer.valueOf(userLogin.getCountryCode()));
			}
		} else {
			user = this.userRepository.findByMobileAndCountryCode(userLogin.getSerchablePhone(),
					Integer.valueOf(userLogin.getCountryCode()));
		}

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
             var log = Logs.builder()
            		 .activity(LogTypes.LOGIN)
            		 .description("Normal user login with acc. No:"+user.get().getId())
            		 .build();
             this.logsRepository.save(log);
			return this.otpsmsService.sendSms(userLogin, null, user);

		}

	}
	
	public ResponseEntity<ObjectNode> corporateLogin(UserLogin userLogin) {
		Optional<User> user = Optional.empty();
		if (profileActive.equalsIgnoreCase("dev")) {
			if (userLogin.getPhoneNumber().equalsIgnoreCase("700000000")) {
				log.debug("fing this phone 703454954 and country code" + userLogin.getCountryCode());

				user = this.userRepository.findByMobileAndCountryCode("703454954",
						Integer.valueOf(userLogin.getCountryCode()));
			} else {
				user = this.userRepository.findByMobileAndCountryCode(userLogin.getSerchablePhone(),
						Integer.valueOf(userLogin.getCountryCode()));
			}
		} else {
			user = this.userRepository.findByMobileAndCountryCode(userLogin.getSerchablePhone(),
					Integer.valueOf(userLogin.getCountryCode()));
		}
		

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
			//check if user is added to corporate
			if(user.get().getCorporate() == null) {
				ObjectMapper objectMapper = new ObjectMapper();

				ObjectNode json = JsonNodeFactory.instance.objectNode();
				ArrayNode arrayNode = objectMapper.createArrayNode();
				arrayNode.add("Access denied");
				json.put("statusCode", false);
				json.putIfAbsent("message", arrayNode);
				json.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(json);
			}
			//disalow users without role
			else if(user.get().getUserRole() == null) {
				ObjectMapper objectMapper = new ObjectMapper();

				ObjectNode json = JsonNodeFactory.instance.objectNode();
				ArrayNode arrayNode = objectMapper.createArrayNode();
				arrayNode.add("Access denied");
				json.put("statusCode", false);
				json.putIfAbsent("message", arrayNode);
				json.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(json);
			}
			
			else {
				return this.otpsmsService.sendSms(userLogin, null, user);
	
			}


		}

	}

	public Object getUseByPhone(String phone) {
		Map<String, Object> map = new HashMap<>();
		try {
			var user = this.userRepository.findByMobile(phone);
			if (user.isPresent()) {
				var u = user.get();
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("id", u.getId());
				userMap.put("firstname", u.getFirstName());
				userMap.put("lastname", u.getLastName());
				userMap.put("phone", u.getMobile());
				map.put("user", userMap);
				map.put("message", "Request successful");
				map.put("success", "true");
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("payload", map);

				return ResponseEntity.status(HttpStatus.OK).body(responseMap);
			} else {
				map.put("message", "User not found");
				map.put("success", "false");
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
		} catch (Exception ex) {
			map.put("message", "Unable to process request");
			map.put("error", ex);
			map.put("success", "false");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
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

	@Transactional
	public ResponseEntity<Object> verifyOtp(@Valid ConfirmOtp confirmOtp) {
		var opt = this.otpsmsService.verifyOtp(confirmOtp);

		if (opt.isPresent()) {

			if (!(opt.get().isValid())) {
				ObjectNode json = JsonNodeFactory.instance.objectNode();
				json.put("message", "otp code is Invalid");
				return ResponseEntity.badRequest().body(json);
			}
			var u = opt.get().getUser();
			this.otpsmsService.deleteOtp(opt.get());

			if (u != null) {
				Optional<User> walletUser  =  this.userRepository.findUserWithUserWalletsById(u.getId());
				if(walletUser.isPresent()) {
					u = this.userRepository.findUserWithUserWalletsById(u.getId()).get();
				}
				

				System.out.println(u.getCreatedAt());
				var response = UserResponseDTO.builder().wallets(u.getUserWallets().stream().map((uw) -> {
					var wallets = uw.getWallet();
					wallets.setUserWallets(null);
					return wallets;
				}).collect(Collectors.toList()))

						.token(jwtService.generateToken(u)).refreshToken(jwtService.generateRefreshToken(u))
						.middleName(u.getMiddleName()).gender(u.getGender().name()).idType(u.getIdType().name())
						.idNumber(u.getIdNumber()).onboardingRequestId(u.getOnboardingRequestId())
						.open_id(u.getOpenId()).birthday(formatter.format(u.getBirthday().toInstant()))
						.updatedAt(u.getUpdatedAt()).kraPin(u.getKraPin())
						.employmentStatus(u.getEmploymentStatus().name())
                        .profileImage(u.getProfileImage())
						.monthlyIncome(u.getMonthlyIncome().toString()).createdAt(u.getCreatedAt()).id(u.getId())
						.address(u.getAddress()).firstName(u.getFirstName()).lastName(u.getLastName())
						.mobile(u.getMobile()).countryCode(u.getCountryCode()).build();
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
				objectMapper.registerModule(new JavaTimeModule());
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

				try {
					return ResponseEntity.ok(objectMapper.writeValueAsString(response));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else {
			log.error("otp not there");

			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("message", "otp code is Invalid");
			return ResponseEntity.badRequest().body(json);
		}
		return null;
		// TODO Auto-generated method stub
	}
	
	@Transactional
	public ResponseEntity<Object> verifyAdminOtp(@Valid ConfirmOtp confirmOtp) {
		var opt = this.otpsmsService.verifyOtp(confirmOtp);

		if (opt.isPresent()) {

			if (!(opt.get().isValid())) {
				ObjectNode json = JsonNodeFactory.instance.objectNode();
				json.put("message", "otp code is Invalid");
				return ResponseEntity.badRequest().body(json);
			}
			var u = opt.get().getUser();
			this.otpsmsService.deleteOtp(opt.get());

			if (u != null) {
				if(u.getCorporate() == null) {
					ObjectNode json = JsonNodeFactory.instance.objectNode();
					json.put("message", "invalid otp type");
					return ResponseEntity.badRequest().body(json);
				}
				Optional<User> walletUser  =  this.userRepository.findUserWithUserWalletsById(u.getId());
				if(walletUser.isPresent()) {
					u = this.userRepository.findUserWithUserWalletsById(u.getId()).get();
				}
				

				System.out.println(u.getCreatedAt());
				var response = UserResponseDTO.builder().wallets(u.getUserWallets().stream().map((uw) -> {
					var wallets = uw.getWallet();
					wallets.setUserWallets(null);
					return wallets;
				}).collect(Collectors.toList()))

						.token(jwtService.generateAdminToken(u)).refreshToken(jwtService.generateAdminRefreshToken(u))
						.middleName(u.getMiddleName()).gender(u.getGender().name()).idType(u.getIdType().name())
						.idNumber(u.getIdNumber()).onboardingRequestId(u.getOnboardingRequestId())
						.open_id(u.getOpenId()).birthday(formatter.format(u.getBirthday().toInstant()))
						.updatedAt(u.getUpdatedAt()).kraPin(u.getKraPin())
						.employmentStatus(u.getEmploymentStatus().name())
                        .profileImage(u.getProfileImage())
						.monthlyIncome(u.getMonthlyIncome().toString()).createdAt(u.getCreatedAt()).id(u.getId())
						.address(u.getAddress()).firstName(u.getFirstName()).lastName(u.getLastName())
						.mobile(u.getMobile()).countryCode(u.getCountryCode()).build();
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
				objectMapper.registerModule(new JavaTimeModule());
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

				try {
					return ResponseEntity.ok(objectMapper.writeValueAsString(response));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else {
			log.error("otp not there");

			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("message", "otp code is Invalid");
			return ResponseEntity.badRequest().body(json);
		}
		return null;
		// TODO Auto-generated method stub
	}

	public ResponseEntity<Object> createJwtFor(User u) {
		var response = UserResponseDTO.builder().token(jwtService.generateToken(u))
				.refreshToken(jwtService.generateRefreshToken(u)).middleName(u.getMiddleName())
				.gender(u.getGender().name()).idType(u.getIdType().name()).idNumber(u.getIdNumber())
				.onboardingRequestId(u.getOnboardingRequestId()).birthday(formatter.format(u.getBirthday().toInstant()))
				.updatedAt(u.getUpdatedAt()).updatedAt(u.getUpdatedAt()).kraPin(u.getKraPin())
				.employmentStatus(u.getEmploymentStatus().name()).monthlyIncome(u.getMonthlyIncome().toString())
				.createdAt(u.getCreatedAt()).id(u.getId())

				.address(u.getAddress()).firstName(u.getFirstName()).lastName(u.getLastName()).mobile(u.getMobile())
				.countryCode(u.getCountryCode()).build();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		objectMapper.registerModule(new JavaTimeModule()); // Register the Java 8 date/time module
		String jsonString;
		try {
			jsonString = objectMapper.writeValueAsString(response);
			JsonNode jsonObject = objectMapper.readTree(jsonString);
			return ResponseEntity.ok(jsonObject);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("message", "something went wrong");
		map.put("success", "false");
		return ResponseEntity.ok(map);

		// Parse the JSON string into a JSON object

	}

	public Optional<User> findUserWallet(User user) {
		return this.userRepository.findUserWithUserWalletsById(user.getId());
		// TODO Auto-generated method stub

	}

	public Optional<User> findUserAndWallets(User user) {
		return this.userRepository.findUserWithWalletsById(user.getId());
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
			map.put("code", "KWEC000");
			return ResponseEntity.ok(map);
		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Pin not set");
			map.put("code", "KWEC002");

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
			var log = Logs.builder()
					.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
					+" failed to set PIN.PIN already set")
					.activity(LogTypes.PIN_SET)
					.build();
			this.logsRepository.save(log);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		} else if (this.walletRepository.findByUserWalletsUser(user).isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Account Not Verified ");
			map.put("success", "false");
			map.put("code", "KWEC003");
			var log = Logs.builder()
					.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
					+" failed to set PIN.Account not verified")
					.activity(LogTypes.PIN_SET)
					.build();
			this.logsRepository.save(log);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
		}

		else {
			var passwordencoded = new BCryptPasswordEncoder().encode(user.getId() + setPin.getPin());
			var userpin = new UserPin();
			userpin.setUser(user);
			userpin.setPin(passwordencoded);
			this.userPinRepository.save(userpin);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "You pin has been set");
			map.put("success", true);
			var log = Logs.builder()
					.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
					+"failed to set PIN.PIN already set")
					.activity(LogTypes.PIN_SET)
					.build();
			this.logsRepository.save(log);
			return ResponseEntity.status(HttpStatus.OK).body(map);

		}
	}

	@Transactional
	public Object changePing(@Valid ChangePin setPin) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<List<UserPin>> userPins = this.userPinRepository.getUserPinThatIsNotArchived(user);
		if (userPins.isPresent() && (userPins.get().size() > 0)) {
			if(userPins.get().get(0).getResetPinAttempts() >= 10) {
				Map<String,Object> map = new HashMap<>();
				map.put("success", false);
				map.put("message","Too many wrong attempts of old PIN reached");
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(map);
			}
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

					// reject update of a blocked PIN
					if (activeUserPin.getPinAttempts() >= 5) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("success", false);
						map.put("message", "Pin already blocked");
						
						var log = Logs.builder()
								.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
								+" failed to update PIN.PIN already blocked")
								.activity(LogTypes.PIN_SET)
								.build();
						this.logsRepository.save(log);
						return ResponseEntity.status(HttpStatus.OK).body(map);
						
					}
					if (encoder.matches(user.getId() + setPin.getOldPin(), activeUserPin.getPin())) {
						var passwordencoded = new BCryptPasswordEncoder().encode(user.getId() + setPin.getPin());
						var userpin = new UserPin();
						userpin.setUser(user);
						userpin.setResetPinAttempts(0);
						userpin.setPin(passwordencoded);
						this.userPinRepository.markUserPinAsDeleted(activeUserPin.getId());
						this.userPinRepository.save(userpin);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("message", "Pin changed Successfully");
						map.put("success", true);
						var log = Logs.builder()
								.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
								+" successfully  PIN PIN")
								.activity(LogTypes.PIN_SET)
								.build();
						this.logsRepository.save(log);
						return ResponseEntity.status(HttpStatus.OK).body(map);

					} else {
						var pin = userPins.get().get(0);
						pin.setResetPinAttempts(userPins.get().get(0).getResetPinAttempts() + 1);
						this.userPinRepository.save(pin);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("message", "Old pin mismatch ");
						map.put("success", false);
						var log = Logs.builder()
								.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
								+" failed to update PIN.Unable to verify old PIN")
								.activity(LogTypes.PIN_SET)
								.build();
						this.logsRepository.save(log);
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
				&& !(userPinRepository.get().get(0).getPinAttempts() >= this.maxpinattempt)) {
			if (bycryp.matches(user.getId() + setPin.getPin(), userPinRepository.get().get(0).getPin())) {
				var token = this.jwtService.generateTokenForWindow(user);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("window", token);
				map.put("success", true);
				this.userPinRepository.resetPinAttempts(user);

				return ResponseEntity.status(HttpStatus.OK).body(map);
			} else {
				// var userPins =
				this.userPinRepository.incrementPinAttempts(user);
//				if (userPins.isPresent()) {
//					var updatedpin = userPins.get();
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("message", "Pin Entered does not match");
//					map.put("attempt_remaining", maxpinattempt - updatedpin.getPinAttempts());
//					map.put("success", false);
//					return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
//
//				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "Pin Entered does not match");
				map.put("attempt_remaining", maxpinattempt - (userPinRepository.get().get(0).getPinAttempts() + 1));

				map.put("success", false);
				var log = Logs.builder()
						.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
						+" failed to login.Entered wrong PIN")
						.activity(LogTypes.LOGIN)
						.build();
				return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
			}
		} else {
			Map<String, Object> map = new HashMap<String, Object>();

			if (userPinRepository.isPresent()) {
				if ((!(userPinRepository.get().isEmpty())
						&& userPinRepository.get().get(0).getPinAttempts() >= maxpinattempt)) {
					map.put("message", "Pin Blocked");
					var log = Logs.builder()
							.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
							+" failed to login.Using blocked PIN")
							.activity(LogTypes.PIN_SET)
							.build();
					this.logsRepository.save(log);
				} else {
					map.put("message", "Pin not set");
					var log = Logs.builder()
							.description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.id
							+" failed to login.PIN not set")
							.activity(LogTypes.PIN_SET)
							.build();
					this.logsRepository.save(log);
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
	}

	@Transactional
	public void deletUserByOnboardingRequestId(String onboardingRequestId) {
		var user = this.userRepository.findByOnboardingRequestId(onboardingRequestId);
		if (user.isPresent()) {
			this.userPinRepository.deleteByUser_Id(user.get().getId());
			this.firebaseTokenRepository.deleteAll(user.get().getFirebaseTokens());
			this.userRepository.deleteByOnboardingRequestId(onboardingRequestId);

		}
		// TODO Auto-generated method stub

	}
	
	public void createRejectedAccount(RejectedAccount payload) {
		this.rejectedAccountRepository.save(payload);
	}
	
	public void deleteSuccessfulFromRejected(String IdNumber) {
		this.rejectedAccountRepository.deleteByIdNumber(IdNumber);
	}

	public Optional<User> findUserByAccountd(String accountId) {
		return this.userRepository.findUserByWalletAccountId(accountId);
		// TODO Auto-generated method stub

	}

	public ResponseEntity createRefreshToken() {
		log.warn("principal " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var token = this.jwtService.generateToken(user);
		var refresh = this.jwtService.generateRefreshToken(user);

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> payload = new HashMap<String, Object>();
		payload.put("token", token);
		payload.put("refreshToken", refresh);

		map.put("payload", payload);

		map.put("success", false);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	public Optional<User> findUserByPhoneNumber(String phoneNumber) {
		// log.error(phoneNumber);
		return this.userRepository.findByMobileAndCountryCode(phoneNumber, 254);
		// TODO Auto-generated method stub

	}
	public Optional<User> findUserByPhoneNumberLoadUserWallet(String phoneNumber, String countrycode) {
		// log.error(phoneNumber);
		return this.userRepository.findByMobileAndJoinWalletCountryCode(phoneNumber,Integer.parseInt(countrycode));
		// TODO Auto-generated method stub

	}
	
	public List<User> findUserPhoneNumberAndCountryCode(List<PhoneCountryPair> listCountryCode) {
		// log.error(phoneNumber);
		return this.userRepository.findCustomersByMultiplePhoneAndCountry(listCountryCode.stream().map(data->data.getMobile()).collect(Collectors.toList()));
		// TODO Auto-generated method stub

	}
	

	public Optional<User> findUserByWalletAccountId(String receiverAccount) {
		return this.userRepository.findUserByWalletAccountId(receiverAccount);

	}

	public void save(User user) {
		this.userRepository.save(user);
	}

	public ResponseEntity<Object> resetPinAttempts(Integer counter, String userId) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = new HashMap<>();
		Optional<User> user = this.userRepository.findById(userId);

		if (user.isPresent()) {
			Optional<UserPin> userPin = this.userPinRepository.getUserPinByUser(user.get());
			if (counter >= maxpinattempt) {
				map.put("success", true);
				map.put("message", "PIN blocked successfully");
					LocalDateTime currentTime = LocalDateTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					String formattedDateTime = currentTime.format(formatter);
					
					if (userPin.isPresent()) {
						userPin.get().setPinAttempts(counter);
						try {
							this.userPinRepository.save(userPin.get());
						}catch(Exception ex) {
							
						}
					
					this.larkService.sendPinResetNotification(loggedInUser,
							user.get().getUserWallets().get(0).getWallet(), "BLOCKING", "Success");
					var log = Logs.builder()
							.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
							+" managed to block PIN for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of id "+user.get().getId())
							.activity(LogTypes.PIN_SET)
							.build();
					this.logsRepository.save(log);
					
				return ResponseEntity.status(HttpStatus.OK).body(map);
					}
					map.put("success", false);
					map.put("message", "Unable to block PIN");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
			} else {
				if (userPin.isPresent()) {
					userPin.get().setPinAttempts(counter);
					try {
						this.userPinRepository.save(userPin.get());
						map.put("success", true);
						map.put("message", "Pin attempts updated");

							this.larkService.sendPinResetNotification(loggedInUser,
									user.get().getUserWallets().get(0).getWallet(), "ATTEMPTS", "Success");
							var log = Logs.builder()
									.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
									+"managed to reset PIN counts to "+counter+" for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id)
									.activity(LogTypes.PIN_RESET)
									.build();
							this.logsRepository.save(log);
						return ResponseEntity.status(HttpStatus.OK).body(map);
					} catch (Exception ex) {
						map.put("success", false);
						map.put("message", "Opps!!Something went wrong");
						System.out.println("ERROR: " + ex);
						var log = Logs.builder()
								.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
								+"failed to reset PIN counts to "+counter+" for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().getId()+". System error")
								.activity(LogTypes.PIN_RESET)
								.build();
						this.logsRepository.save(log);
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
					}
				} else {
						LocalDateTime currentTime = LocalDateTime.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						String formattedDateTime = currentTime.format(formatter);

						this.larkService.sendPinResetNotification(loggedInUser,
								user.get().getUserWallets().get(0).getWallet(), "ATTEMPTS", "FAILED");
					map.put("success", false);
					map.put("message", "User does not have a PIN");
					var log = Logs.builder()
							.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
							+"failed to reset PIN counts to "+counter+" for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id+". User does not have PIN.")
							.activity(LogTypes.PIN_RESET)
							.build();
					this.logsRepository.save(log);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
				}

			}
		} else {
				LocalDateTime currentTime = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String formattedDateTime = currentTime.format(formatter);
				this.larkService.sendPinResetNotification(loggedInUser, user.get().getUserWallets().get(0).getWallet(),
						"ATTEMPTS", "Failed");
			map.put("success", false);
			map.put("message", "User not found");
			var log = Logs.builder()
					.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
					+"failed to reset PIN counts to "+counter+" for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id+". User not found")
					.activity(LogTypes.PIN_RESET)
					.build();
			this.logsRepository.save(log);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	public ResponseEntity<Object> resetUserPin(String userId) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = new HashMap<>();
		Optional<User> user = this.userRepository.findById(userId);
		if (user.isPresent()) {
			Optional<UserPin> userPin = this.userPinRepository.getUserPinByUser(user.get());
			if (userPin.isPresent()) {
				try {
					this.larkService.sendPinResetNotification(loggedInUser,
								user.get().getUserWallets().get(0).getWallet(), "RESET", "Success");
					this.userPinRepository.delete(userPin.get());
					map.put("success", true);
					map.put("message", "PIN reset successfull");
					var log = Logs.builder()
							.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
							+"managed to reset PIN for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id)
							.activity(LogTypes.PIN_RESET)
							.build();
					this.logsRepository.save(log);
					return ResponseEntity.status(HttpStatus.OK).body(map);
				} catch (Exception ex) {
					map.put("success", false);
					map.put("message", "Opps!!Something went wrong");
					System.out.println("ERROR: " + ex);
					var log = Logs.builder()
							.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
							+"failed to reset PIN  for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id+". A server error ocurred")
							.activity(LogTypes.PIN_RESET)
							.build();
					this.logsRepository.save(log);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
				}
			} else {
				map.put("success", false);
				map.put("message", "User does not have a PIN");
				this.larkService.sendPinResetNotification(loggedInUser, user.get().getUserWallets().get(0).getWallet(),
						"RESET", "Failed");
				var log = Logs.builder()
						.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
						+"failed to reset PIN for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id)
						.activity(LogTypes.PIN_SET)
						.build();
				this.logsRepository.save(log);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
			}

		} else {
			
				this.larkService.sendPinResetNotification(loggedInUser, user.get().getUserWallets().get(0).getWallet(),
						"RESET", "Failed");
			map.put("success", false);
			map.put("message", "User does not exist");
			var log = Logs.builder()
					.description(loggedInUser.getFirstName()+" "+loggedInUser.getLastName()+"of id:"+loggedInUser.id
					+"failed to reset PIN  for user "+user.get().getFirstName()+" "+user.get().getLastName()+" of Id:"+user.get().id+". User does not exist")
					.activity(LogTypes.PIN_SET)
					.build();
//	            this.larkService.sendPinResetNotification(loggedInUser,user.get().getUserWallets().get(0).getWallet(),"RESET","Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	public ResponseEntity createUserOpenId(@Valid OpenIdRequest openId) {
		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var value = redisBean.getRecord(openId.getPublicKey());
		Map<String, Object> map = new HashMap<>();

		if (value.isEmpty()) {
			map.put("success", false);
			map.put("message", "Open Id process failed or time out");

			map.put("code", "unmet_authentication_requirements");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		var walletClient = this.walletClientRepository.findById(value.get());
		if (value != null && walletClient.isPresent()) {

			var result = this.jwtService.generateOpenIdWithSecret(loggedInUser, value.get(), openId.getPublicKey(),
					openId.getAppId());

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} else {
			map.put("success", false);
			map.put("message", "Open Id process failed or time out");

			map.put("code", "unmet_authentication_requirements");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	public void pushUpgradeNotification(WalletAccountUpgradeResultNotification params) {
		var walletAccount = WalletAccountUpgrade.builder().accountId(params.getAccountId())
				.onboardingRequestId(params.getOnboardingRequestId()).accountType(params.getAccountType())
				.rejectionReasonIds(params.getRejectionReasonIds()).rejectionReasonMsgs(params.getRejectionReasonMsgs())
				.accountType(params.getAccountType()).status(params.getStatus()).build();
		this.walletAccountUpgradeRepository.save(walletAccount);
	}

	public Optional<User> findUserByOpenId(String open_id) {
		// TODO Auto-generated method stub
		return this.userRepository.findByOpenId(open_id);
	}

	public ResponseEntity<Object> getAuthenticatedUserProfile() {
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Wallet> wallet = this.walletRepository.findByUserWalletsUser(u);
		var response = UserResponseDTO.builder().wallets(wallet.stream().toList())
				.middleName(u.getMiddleName())
				.gender(u.getGender()
				.name())
				.idType(u.getIdType().name())
				.idNumber(u.getIdNumber())
				.onboardingRequestId(u.getOnboardingRequestId()).open_id(u.getOpenId())
				.birthday(formatter.format(u.getBirthday().toInstant()))
				.updatedAt(u.getUpdatedAt())
				.kraPin(u.getKraPin())
				.employmentStatus(u.getEmploymentStatus().name())
				.monthlyIncome(u.getMonthlyIncome().toString())
				.createdAt(u.getCreatedAt())
				.id(u.getId())
				.address(u.getAddress())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.mobile(u.getMobile())
				.countryCode(u.getCountryCode())
				.profileImage(u.getProfileImage())
				.build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			return ResponseEntity.ok(objectMapper.writeValueAsString(response));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ResponseEntity<Object> updateFirebaseToken(@Valid UserDeviceToken userDeviceToken) {
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var firebaseToken = FirebaseToken.builder().user(u).token(userDeviceToken.getToken()).build();
		var saveToken = this.firebaseTokenRepository.save(firebaseToken);
		Map<String, Object> map = new HashMap<>();
		map.put("message", "Firebase Token updated");

		return ResponseEntity.status(HttpStatus.CREATED).body(map);

	}
	
	public ResponseEntity<Object> getUserPermissions(){
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	Optional<UserRole> role = this.userRoleRepository.findUserRoleByUserId(u.getId());
		if(role.isPresent()) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("success",true);
			map.put("message","Request complete");
			map.put("role",role.get().getRole().getRoleName());
			map.put("permissions",role.get().getRole().getRolePermissions().stream().map(r->{
				Map<String,Object> per = new HashMap<String,Object>();
				var p=r.getPermission();
				per.put("id",p.getId());
				per.put("name",p.getName());
				per.put("description", p.getDescription());

				return per;
			}).collect(Collectors.toList()));
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}else {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("success",true);
			map.put("message","Request complete");
			map.put("role",null);
			map.put("permissions",new ArrayList<>());
			return ResponseEntity.status(HttpStatus.OK).body(map); 
		}
	}
	
	public ResponseEntity<Object> getDailyOnboardingTrend(int month,int year){
		List<Object[]> obTrend = this.userRepository.findDailyOnBoardingTrend(month, year);
		Map<String,Object> resMap = new HashMap<>();
		if(!obTrend.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			
			var trend =  obTrend.stream().map(obt->{
				Map<String,Object> tMap = new HashMap<>();
				tMap.put("date", obt[0]);
				tMap.put("users",obt[1]);
				
				return tMap;
			}).collect(Collectors.toList());
			map.put("trend", trend);
			resMap.put("payload", map);
			
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			map.put("trend",new ArrayList<>());
			resMap.put("payload", map);
			
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}
	}
	
	public ResponseEntity<Object> getMonthlyOnboardingTrend(int month,int year){
		List<Object[]> obTrend = this.userRepository.findMonthlyOnBoardingTrend(year);
		Map<String,Object> resMap = new HashMap<>();
		if(!obTrend.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			
			var trend =  obTrend.stream().map(obt->{
				Map<String,Object> tMap = new HashMap<>();
				tMap.put("date", obt[0]);
				tMap.put("users",obt[1]);
				
				return tMap;
			}).collect(Collectors.toList());
			
			map.put("trend", trend);
			resMap.put("payload", map);
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			map.put("trend",new ArrayList<>());
			resMap.put("payload", map);
			
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}
	}
	
	
	public ResponseEntity<Object> getAnnualOnboardingTrend(){
		List<Object[]> obTrend = this.userRepository.findAnnualOnBoardingTrend();
		Map<String,Object> resMap = new HashMap<>();
		if(!obTrend.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			
			var trend =  obTrend.stream().map(obt->{
				Map<String,Object> tMap = new HashMap<>();
				tMap.put("date", obt[0]);
				tMap.put("users",obt[1]);
				
				return tMap;
			}).collect(Collectors.toList());
			
			map.put("trend", trend);
			resMap.put("payload", map);
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request succcessful");
			map.put("trend",new ArrayList<>());
			resMap.put("payload", map);
			
			return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}
	}
	
	
	public ResponseEntity<Object> getOnBoardingDeviation(){
		
		List<Object[]> deviation =  this.userRepository.findOnBoardingDeviation();
		if(!deviation.isEmpty()) {
		      Map<String,Object> devMap = new HashMap<>();
		      devMap.put("today",deviation.get(0)[0]);//				
		      devMap.put("yesterday",deviation.get(0)[1]);
		Map<String,Object> map =  new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("deviation", devMap);
		Map<String,Object> resMap =  new HashMap<>();
		
		resMap.put("payload",map);
		return ResponseEntity.status(HttpStatus.OK).body(resMap);
		}
		return null;
	}
	
	public ResponseEntity<Object> uploadProfileImage(MultipartFile file){
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 String fileName = StringUtils.cleanPath(file.getOriginalFilename());

	        try {
	            if (fileName.contains("..")) {
	                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
	            }
	            if (file.getSize() > 5000000) {
	            	Map<String,Object> map =  new HashMap<>();
	            	map.put("success",false);
	            	map.put("message","File exists maximum size");
	            	
	            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);	       
	            }
	            
	            //check if file type is image
	            if (!file.getContentType().equalsIgnoreCase("image/png") && !file.getContentType().equalsIgnoreCase("image/jpg") && !file.getContentType().equalsIgnoreCase("image/jpeg")) {
	            	Map<String,Object> map =  new HashMap<>();
	            	map.put("success",false);
	            	map.put("message","Invalid file format.Allowed types:.png,.jpg,.jpeg");	            
	            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);	       
	            }
	            byte[] compressedImageData = compressImage(file.getBytes());
	            String newFileName = UUID.randomUUID().toString() + "_" + user.getId()+"."+file.getContentType().split("/")[1];
	            Path targetLocation = this.profileImageDir.resolve(newFileName);
	            Files.write(targetLocation, compressedImageData);
	            ProfileImage profileImage  = ProfileImage.builder()
	            		.name(fileName)
	            		.type(file.getContentType())
	            		.filePath(this.wallet_base_url+targetLocation.toString().substring(targetLocation.toString().indexOf("/profiles")))
	            		.build();
	            
	            this.profileImageRepository.save(profileImage);
	            
	             user.setProfileImage(profileImage);
	            this.userRepository.save(user);
                Map<String,Object> map =  new HashMap<>();
                map.put("success",true);
                map.put("message","Request completed");
                map.put("profileImage",profileImage);
                Map<String,Object> resMap = new HashMap<>();
                resMap.put("payload",map);
                
	          return ResponseEntity.status(HttpStatus.OK).body(resMap);
	        } catch (IOException ex) {
	            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
	        }
	}
	
	   private byte[] compressImage(byte[] imageData) throws IOException {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        Thumbnails.of(new ByteArrayInputStream(imageData))
	                .width(compressedImageWidth)
	                .outputQuality(imageQuality)
	                .toOutputStream(outputStream);
	        return outputStream.toByteArray();
	    }

	public Object  findUsersCreatedBetweenStartAndEndDate( SdkSearchCustomers sdkSearchCustomer) {
		        Date endDate = new Date(sdkSearchCustomer.getCreatedAtEnd()*1000);
		        Date startDate = new Date(sdkSearchCustomer.getCreatedAtStart()*1000);
				Map<Object, Object> message= new HashMap<>();

           var pageable= PageRequest.of(sdkSearchCustomer.getPageNumber(),sdkSearchCustomer.getPageSize());

		var results= this.userRepository.findByCreatedAtBetweenOrderByCreatedAtAsc(startDate,endDate,pageable);
		
		 
			var data=results.stream().map(founduser->{
				Map<Object, Object> m= new HashMap<>();
				m.put("openId",founduser.getOpenId());
				m.put("countryCode", founduser.getCountryCode());
				m.put("firstName",founduser.getFirstName());
				m.put("lastName",founduser.getLastName());
				m.put("middleName",founduser.getMiddleName());
				m.put("mobile",founduser.getMobile());
				m.put("verified",founduser.getUserWallets().isEmpty()?false:true);
				m.put("createdAt",founduser.getCreatedAt());
				return m;
			}).collect(Collectors.toList());		
			message.put("payload", data);
			var page= new HashMap<String,Object>();
			page.put("hasNext", results.hasNext());
			page.put("hasPrevious", results.hasPrevious());
			page.put("nextPage", results.hasNext()?results.nextPageable().getPageNumber():null);
			page.put("totalPages",results.getTotalPages());
			page.put("itemsPage",results.getPageable().getPageSize());
			page.put("currentPage", results.getNumber());
			page.put("totalItems", results.getTotalElements());
			page.put("previousPage", results.previousOrFirstPageable().getPageNumber());
			page.put("fromDate", sdkSearchCustomer.getCreatedAtStart());
			page.put("toDate", sdkSearchCustomer.getCreatedAtEnd());
			message.put("page", page);
			
			
		    return ResponseEntity.status(HttpStatus.OK).body(message);
		
	
		// TODO Auto-generated method stub
		
	}
	
	

	

}

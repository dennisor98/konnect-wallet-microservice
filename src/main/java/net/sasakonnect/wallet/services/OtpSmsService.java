package net.sasakonnect.wallet.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.LoginOtpResendDto;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.provider.AfricasTalking;
import net.sasakonnect.wallet.provider.Celcom;
import net.sasakonnect.wallet.provider.SmsManager;
import net.sasakonnect.wallet.tools.RequestSigner;

@Service
@Slf4j
public class OtpSmsService {
	@Autowired
	private Celcom celcom;
	@Autowired
	private AfricasTalking africasTalking;

	private final OtpService otpService;

	@Value("${OTP_TTL}")
	private Integer otp_ttl;

	@Value("${spring.profiles.active}")
	String profileActive;

	@Value("${WALLET_TEMPLATE_LOGIN}")
	private String template;

	@Value("${SME_TEMPLATE_LOGIN}")
	private String sme_template;

	private SmsManager smsManager;

	public OtpSmsService(OtpService otpService, SmsManager smsManager) {
		this.otpService = otpService;
		this.smsManager = smsManager;
	}

	public ResponseEntity<ObjectNode> sendSms(UserLogin userLogin, String template, Optional<User> user) {
		StringBuilder stringbuilder = new StringBuilder();

		if (template == null) {
			template = this.template;
		}
		stringbuilder.append(template);

		// Check if SMS can be sent for the provided phone number
		String data = otpService.canSendSms(userLogin.getFullPhone());

		if (data == null) {
			// Generate a random OTP
			int otp = new Random().nextInt(9000) + 1000;
			/**
			 * This is to allow Google to have a test account if you find a better way why
			 * not change? so google play team will use 700000000 as phone number and 1234
			 * as otp
			 */
			if (profileActive.equalsIgnoreCase("dev")) {
				log.error("edv " + otp + " phone is" + userLogin.getPhoneNumber());

				if (userLogin.getPhoneNumber().equalsIgnoreCase("700000000")) {
					otp = 1234;
					userLogin.setPhoneNumber("703454954");
				}
			}

			Otp otpEntity = new Otp();
			otpEntity.setCode(String.valueOf(otp));
			otpEntity.setPhoneNumber(userLogin.getFullPhone());
			otpEntity.setUser(user.get());
			otpEntity.setHashUseCount(0);
			otpEntity.setTtl(otp_ttl);
			Instant now = Instant.now();
			long microsecondsSinceEpoch = Duration.between(Instant.EPOCH, now).toNanos() / 1_000;

			otpEntity.setHash(RequestSigner.createHashFrom(user.get().getId() + otp + microsecondsSinceEpoch));
			log.error("otp saved is " + otp);
			Otp savedOtp = this.otpService.saveOtp(otpEntity);
			stringbuilder.append(":" + savedOtp.getCode());
			if (userLogin.getMessageSignature() != null && userLogin.getMessageSignature().length() == 11) {
				stringbuilder.append(" " + userLogin.getMessageSignature());

			}

			smsManager.sendMessage(stringbuilder.toString(), userLogin.getFullPhone());
			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("hash", otpEntity.getHash());
			json.put("message",
					"otp message sent it will expire within the next " + savedOtp.getTtl() / 60 + " minutes");

			json.put("success", true);
			return ResponseEntity.status(HttpStatus.OK).body(json);

		} else {
			// Prepare error response for cases where SMS cannot be sent
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode errorResponse = objectMapper.createObjectNode();
			errorResponse.put("success", false);
			errorResponse.put("message", data);

			return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
		}
	}
	
	public ResponseEntity<Object> resendLoginOtp(LoginOtpResendDto otpDto){
		Optional<Otp> otpOptional = this.otpService.getOtpWithByHash(otpDto.getHash());
		if(otpOptional.isEmpty()) {
			Map<String,Object> map  = new HashMap<>();
			map.put("success", false);
			map.put("message","Malformed hash");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		
		
		var otp =  otpOptional.get();
		if(otp.getDeletedAt() !=null && !otp.isValid()) {
			Map<String,Object> map  = new HashMap<>();
			map.put("success", false);
			map.put("message","Otp already used");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
			
		otp.setTtl(otp_ttl);
		otp.setCreatedAt(new Date());

		Otp savedOtp = this.otpService.saveOtp(otp);
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(template);
		stringbuilder.append(":" + otp.getCode());

		smsManager.sendMessage(stringbuilder.toString(), otp.getPhoneNumber());
		ObjectNode json = JsonNodeFactory.instance.objectNode();
		json.put("hash", otp.getHash());
		json.put("message",
				"otp message sent it will expire within the next " + savedOtp.getTtl() / 60 + " minutes");

		json.put("success", true);
		return ResponseEntity.status(HttpStatus.OK).body(json);
	}

	public ResponseEntity<ObjectNode> sendSmeUserSms(SmeUserLogin userLogin,Sme sme,String template, Optional<User> user) {
		StringBuilder stringbuilder = new StringBuilder();

		if (template == null) {
			template = this.sme_template;
		}
		stringbuilder.append(template);

		// Check if SMS can be sent for the provided phone number
		String data = otpService.canSendSms(userLogin.getFullPhone());

		if (data == null) {
			// Generate a random OTP
			int otp = new Random().nextInt(900000) + 100000;
			/**
			 * This is to allow Google to have a test account if you find a better way why
			 * not change? so google play team will use 700000000 as phone number and 1234
			 * as otp
			 */
			if (profileActive.equalsIgnoreCase("dev")) {
				log.error("edv " + otp + " phone is" + userLogin.getPhoneNumber());

				if (userLogin.getPhoneNumber().equalsIgnoreCase("700000000")) {
					otp = 1234;
					userLogin.setPhoneNumber("703454954");
				}
			}

			Otp otpEntity = new Otp();
			otpEntity.setCode(String.valueOf(otp));
			otpEntity.setSme(sme);
			otpEntity.setPhoneNumber(userLogin.getFullPhone());
			otpEntity.setUser(user.get());
			otpEntity.setHashUseCount(0);
			otpEntity.setTtl(otp_ttl);
			Instant now = Instant.now();
			long microsecondsSinceEpoch = Duration.between(Instant.EPOCH, now).toNanos() / 1_000;

			otpEntity.setHash(RequestSigner.createHashFrom(user.get().getId() + otp + microsecondsSinceEpoch));
			log.error("otp saved is " + otp);
			Otp savedOtp = this.otpService.saveOtp(otpEntity);
			stringbuilder.append(":" + savedOtp.getCode());

			smsManager.sendMessage(stringbuilder.toString(), userLogin.getFullPhone());
			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("hash", otpEntity.getHash());
			json.put("message",
					"otp message sent it will expire within the next " + savedOtp.getTtl() / 60 + " minutes");

			json.put("success", true);
			return ResponseEntity.status(HttpStatus.OK).body(json);

		} else {
			// Prepare error response for cases where SMS cannot be sent
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode errorResponse = objectMapper.createObjectNode();
			errorResponse.put("success", false);
			errorResponse.put("message", data);

			return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
		}
	}
	
	public ResponseEntity<ObjectNode> sendSmeWindowSms(String phone,Sme sme,String template,User user) {
		StringBuilder stringbuilder = new StringBuilder();

		if (template == null) {
			template = this.sme_template;
		}
		stringbuilder.append(template);
       var fullphone = "254"+phone.substring(phone.length() -9);
		// Check if SMS can be sent for the provided phone number
		String data = otpService.canSendSms(fullphone);

		if (data == null) {
			// Generate a random OTP
			int otp = new Random().nextInt(900000) + 100000;
			Otp otpEntity = new Otp();
			otpEntity.setCode(String.valueOf(otp));
			otpEntity.setSme(sme);
			otpEntity.setPhoneNumber(fullphone);
			otpEntity.setUser(user);
			otpEntity.setHashUseCount(0);
			otpEntity.setTtl(otp_ttl);
			Instant now = Instant.now();
			long microsecondsSinceEpoch = Duration.between(Instant.EPOCH, now).toNanos() / 1_000;

			otpEntity.setHash(RequestSigner.createHashFrom(user.getId() + otp + microsecondsSinceEpoch));
			log.error("otp saved is " + otp);
			Otp savedOtp = this.otpService.saveOtp(otpEntity);
			stringbuilder.append(":" + savedOtp.getCode());

			smsManager.sendMessage(stringbuilder.toString(),fullphone);
			ObjectNode json = JsonNodeFactory.instance.objectNode();
			json.put("hash", otpEntity.getHash());
			json.put("message",
					"otp message sent it will expire within the next " + savedOtp.getTtl() / 60 + " minutes");

			json.put("success", true);
			return ResponseEntity.status(HttpStatus.OK).body(json);

		} else {
			// Prepare error response for cases where SMS cannot be sent
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode errorResponse = objectMapper.createObjectNode();
			errorResponse.put("success", false);
			errorResponse.put("message", data);

			return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
		}
	}
	
	

	public Optional<Otp> verifyOtp(@Valid ConfirmOtp confirmOtp) {
		return this.otpService.getOtpWithByHashAndCode(confirmOtp.getHash(), confirmOtp.getOtp());

	}

	public void deleteOtp(Otp opt) {
		this.otpService.deleteOtp(opt);

		// TODO Auto-generated method stub

	}

}

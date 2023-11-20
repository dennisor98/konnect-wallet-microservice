package net.sasakonnect.wallet.services;

import java.time.Duration;
import java.time.Instant;
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
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.provider.AfricasTalking;
import net.sasakonnect.wallet.provider.Celcom;
import net.sasakonnect.wallet.provider.SmsManager;
import net.sasakonnect.wallet.tools.RequestSigner;

@Service
@Slf4j
public class SmsService {
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
	private SmsManager smsManager;

	public SmsService(OtpService otpService, SmsManager smsManager) {
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
			switch (profileActive) {
			case "dev": {
				log.error("edv " + otp + " phone is" + userLogin.getPhoneNumber());

				if (userLogin.getPhoneNumber().equalsIgnoreCase("700000000")) {
					otp = 1234;
					userLogin.setPhoneNumber("703454954");
				}

			}
			default: {

			}
			}
			Otp otpEntity = new Otp();
			otpEntity.setCode(String.valueOf(otp));
			otpEntity.setPhoneNumber(userLogin.getFullPhone());
			otpEntity.setUser(user.get());
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

	public Optional<Otp> verifyOtp(@Valid ConfirmOtp confirmOtp) {
		return this.otpService.getOtpWithByHashAndCode(confirmOtp.getHash(), confirmOtp.getOtp());

	}

}

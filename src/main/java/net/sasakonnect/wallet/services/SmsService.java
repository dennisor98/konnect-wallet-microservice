package net.sasakonnect.wallet.services;

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
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.UserLogin;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.provider.AfricasTalking;
import net.sasakonnect.wallet.provider.Celcom;
import net.sasakonnect.wallet.provider.SmsManager;
import net.sasakonnect.wallet.tools.Signature;

@Service
public class SmsService {
	private static final long serialVersionUID = 1L;
	@Autowired
	private Celcom celcom;
	@Autowired
	private AfricasTalking africasTalking;

	private final OtpService otpService;

	@Value("${OTP_TTL}")
	private Integer otp_ttl;

	@Value("${WALLET_TEMPLATE_LOGIN}")
	private String template;
	private SmsManager smsManager;

	public SmsService(OtpService otpService, SmsManager smsManager) {
		this.otpService = otpService;
		this.smsManager = smsManager;
	}

	public ResponseEntity<ObjectNode> sendSms(UserLogin userLogin, String template, Optional<User> user) {
		if (template == null) {
			template = this.template;
		}

		// Check if SMS can be sent for the provided phone number
		String data = otpService.canSendSms(userLogin.getFullPhone());

		if (data == null) {
			// Generate a random OTP
			int otp = new Random().nextInt(9000) + 1000;
			Otp otpEntity = new Otp();
			otpEntity.setCode(String.valueOf(otp));
			otpEntity.setPhoneNumber(userLogin.getFullPhone());
			otpEntity.setUser(user.get());
			otpEntity.setTtl(otp_ttl);
			otpEntity.setHash(Signature.createHashFrom(user.get().getId() + otp));
			Otp savedOtp = this.otpService.saveOtp(otpEntity);

//			var smsJob = new Celcom();
//			smsJob.setCelcomKey(this.celcomKey);
//			smsJob.setCelcom_cookie(this.celcom_cookie);
//			smsJob.setShortcode(this.shortcode);
//			smsJob.setOtp_ttl(this.otp_ttl);
//			smsJob.setPartnerID(this.partnerID);
//			smsJob.setCelcomUrl(this.celcomUrl);
//			smsJob.setTemplate(
//					this.template + ((userLogin.getMessageSignature() == null) ? "" : userLogin.getMessageSignature()));
//			smsJob.params = savedOtp;
			smsManager.sendMessage(template, userLogin.getPhoneNumber());
			// this.jobProducer.enqueueJob(smsJob);
			ObjectNode json = JsonNodeFactory.instance.objectNode();

			json.put("hash", Signature.createHashFrom(user.get().getId() + otp));
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

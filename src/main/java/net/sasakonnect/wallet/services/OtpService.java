package net.sasakonnect.wallet.services;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.repository.OtpRepository;

@Service
@Slf4j
public class OtpService {

	private final OtpRepository otpRepository;
	@Value("${spring.profiles.active}")
	String profileActive;

	@Autowired
	public OtpService(OtpRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	@Transactional
	public Otp saveOtp(Otp otpentity) {
		return otpRepository.save(otpentity);
	}

	public String canSendSms(String phone) {
		var phone_length = phone.length();
		var original_phone = phone;
		if (phone_length > 9) {
			phone = phone.substring(phone_length - 9);
		}
		log.debug(phone);
		String phoneRegex = "^(?:254|\\+254|0)?([17][0-9]{8})$";
		if (!Pattern.matches(phoneRegex, phone)) {
			return "Phone number entered is wrong";
		}

		Optional<Otp> smsOptional = otpRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(original_phone);

		if (smsOptional.isPresent()) {
			System.out.println(smsOptional.get().getHash());

			Otp sms = smsOptional.get();
			long currentTimeMillis = System.currentTimeMillis();
			long smsCreatedAtMillis = sms.getCreatedAt().getTime();
			long smsTtlMillis = sms.getTtl() * 1000;
			boolean isWaiting = currentTimeMillis <= smsCreatedAtMillis + smsTtlMillis;
			System.out.println(isWaiting);
			System.out.println(isWaiting);

			System.out.println(isWaiting);

			if (isWaiting) {
				long secondsToWait = (smsCreatedAtMillis + smsTtlMillis - currentTimeMillis) / 1000;
				return "Please wait for " + secondsToWait + " seconds before requesting";
			}
		}

		return null;
	}

	@Transactional
	public ResponseEntity<String> confirmOtp(String id, String code) {
		Optional<Otp> otpOptional = otpRepository.findByIdAndCode(id, code);

		if (otpOptional.isPresent()) {
			otpRepository.delete(otpOptional.get());
			return ResponseEntity.ok("OTP verified");
		}

		return ResponseEntity.badRequest().body("OTP hash or code is Invalid");
	}

	@Transactional
	public Otp confirmOtpLocal(String hash, String code) {
		Optional<Otp> otpOptional = otpRepository.findByHashAndCode(hash, code);

		if (otpOptional.isPresent()) {
			Otp otp = otpOptional.get();
			otpRepository.delete(otp);
			return otp;
		}

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP code is Invalid");
	}

	public Optional<Otp> getOtpWithByHash(String hash) {
		return otpRepository.findByHashWithUser(hash);
	}

	public Optional<Otp> getOtpWithByHashAndCode(String hash, String code) {
		// TODO Auto-generated method stub
		var otp = otpRepository.findByHashAndCodeWithUser(hash, code);
		System.out.print("otp is present" + otp.isPresent());
		if (otp.isPresent()) {
			if (!(profileActive.equalsIgnoreCase("dev") && code.equalsIgnoreCase("1234"))) {
				otp.get().setDeletedAt(new Date());
				this.otpRepository.save(otp.get());

			}

			return Optional.of(otp.get());
		} else {
			return Optional.empty();
		}
	}

}

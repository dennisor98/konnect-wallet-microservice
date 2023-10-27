package net.sasakonnect.wallet.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.repository.OtpRepository;

@Service
public class OtpService {

	private final OtpRepository otpRepository;

	@Autowired
	public OtpService(OtpRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	@Transactional
	public Otp saveOtp(Otp otpentity) {
		return otpRepository.save(otpentity);
	}

	public String canSendSms(String phone) {
		String phoneRegex = "^(?:254|\\+254|0)?([17][0-9]{8})$";
		if (!Pattern.matches(phoneRegex, phone)) {
			return "Phone number entered is wrong";
		}

		Optional<Otp> smsOptional = otpRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(phone);

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
		if (otp.isPresent() && !otp.isEmpty()) {
			List<Otp> otps = otp.get().stream().filter((item) -> item.isValid()).map((item) -> {

				item.setDeletedAt(new Date());
				return item;
			}).collect(Collectors.toList());

			this.otpRepository.saveAll(otps);
			return Optional.of(otps.isEmpty() ? null : otps.get(0));
		} else {
			return Optional.empty();
		}
	}
}

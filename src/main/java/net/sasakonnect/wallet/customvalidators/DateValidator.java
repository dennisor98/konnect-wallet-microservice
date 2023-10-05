package net.sasakonnect.wallet.customvalidators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sasakonnect.wallet.annotations.ValidDate;

public class DateValidator implements ConstraintValidator<ValidDate, String> {
	private String format;

	@Override
	public void initialize(ValidDate constraintAnnotation) {
		this.format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

		// No initialization needed
	}

	@Override
	public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			LocalDateTime currentDateTime = LocalDateTime.now();
			dateFormat.parse(date != null ? date : currentDateTime.format(formatter).toString());
			return true;
		} catch (ParseException e) {
			return false;
		} catch (NullPointerException e) {
			return false;

		}
	}
}

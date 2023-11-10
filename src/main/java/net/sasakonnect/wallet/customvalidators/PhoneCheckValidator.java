package net.sasakonnect.wallet.customvalidators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sasakonnect.wallet.annotations.PhoneCheck;

public class PhoneCheckValidator implements ConstraintValidator<PhoneCheck, String> {

	@Override
	public void initialize(PhoneCheck constraintAnnotation) {
		// No initialization needed
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Check if the phone number has at least 9 digits
		return value != null && value.matches("\\d{9,}");
	}
}

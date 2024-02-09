package net.sasakonnect.wallet.customvalidators;

import java.util.Base64;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sasakonnect.wallet.annotations.Base64ImageValidator;

public class Base64Image implements ConstraintValidator<Base64ImageValidator, String> {
	@Override
	public void initialize(Base64ImageValidator constraintAnnotation) {
		// No initialization needed
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		try {
			// Decode base64 string
			byte[] decodedBytes = Base64.getDecoder().decode(value);

			// Attempt to create an image from the decoded bytes
			// You can implement additional validation logic here based on your requirements
			// For example, checking the image format, dimensions, etc.
			// If decoding succeeds, it is considered a valid image
			// If not, validation fails
			return decodedBytes.length > 0;
		} catch (IllegalArgumentException e) {
			// If an error occurs during decoding, the base64 string is not a valid image
			return false;
		}
	}
}

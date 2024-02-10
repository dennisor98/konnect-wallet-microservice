package net.sasakonnect.wallet.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sasakonnect.wallet.customvalidators.Base64Image;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Base64Image.class)
@Documented
public @interface Base64ImageValidator {
	String message() default "Invalid base64-encoded image";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

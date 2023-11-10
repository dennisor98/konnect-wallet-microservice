package net.sasakonnect.wallet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sasakonnect.wallet.customvalidators.PhoneCheckValidator;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneCheckValidator.class)
public @interface PhoneCheck {
	String message() default "Phone number must be at least 9 digits";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

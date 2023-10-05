package net.sasakonnect.wallet.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sasakonnect.wallet.customvalidators.DateValidator;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
	String message() default "Please enter a valid date in the format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

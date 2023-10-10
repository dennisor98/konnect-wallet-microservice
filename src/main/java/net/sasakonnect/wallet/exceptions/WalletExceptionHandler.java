package net.sasakonnect.wallet.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class WalletExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserInputException.class)
	public ResponseEntity<Object> handleValidationError(UserInputException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
		return new ResponseEntity<>(errorResponse, ex.getErrorCode());
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);

	}

	@ExceptionHandler({ ResponseStatusException.class })
	public ResponseStatusException handleConstraintViolation(ResponseStatusException ex, WebRequest request)
			throws JsonProcessingException {
		List<String> errors = new ArrayList<String>();

		return new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getReason());

	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, List<String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors
				.groupingBy(e -> e.getField(), Collectors.mapping(e -> e.getDefaultMessage(), Collectors.toList())));

		return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {

		if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException validationError = (ConstraintViolationException) ex;

			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("status", statusCode.toString());
			errorResponse.put("message", "Validation errors occurred");

			List<Map<String, String>> fieldErrors = new ArrayList<>();
			for (ConstraintViolation<?> fieldError : validationError.getConstraintViolations()) {
				var list = fieldError.getPropertyPath().toString().split(".arg0.");
				Map<String, String> fieldErrorMap = new HashMap<>();
				fieldErrorMap.put("field", list[list.length - 1]);
				fieldErrorMap.put("message", fieldError.getMessage());
				fieldErrors.add(fieldErrorMap);
			}
			errorResponse.put("errors", fieldErrors);

			// Create a JSON response from the errorResponse map
			return ResponseEntity.status(statusCode).headers(headers).body(errorResponse);
		}

		else {
			return ResponseEntity.status(statusCode).headers(headers).body(body);
		}

	}

}

class ApiError {
	private HttpStatusCode status;
	private Object message;
	private Object errors;

	public ApiError(HttpStatusCode status, Object message, Object errors) {
		super();
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public HttpStatusCode getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getErrors() {
		return errors;
	}

	public void setErrors(Object errors) {
		this.errors = errors;
	}

}

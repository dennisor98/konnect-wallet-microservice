package net.sasakonnect.wallet.exceptions;

import org.springframework.http.HttpStatus;

public class UserInputException extends RuntimeException {
	private HttpStatus errorCode = HttpStatus.BAD_REQUEST;
	private String errorMessage = "";

	public UserInputException(HttpStatus errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public HttpStatus getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}

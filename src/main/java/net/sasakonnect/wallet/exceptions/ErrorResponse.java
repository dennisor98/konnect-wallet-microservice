package net.sasakonnect.wallet.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
	private final HttpStatus errorCode;
	private final String errorMessage;

	public ErrorResponse(HttpStatus errorCode, String errorMessage) {
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

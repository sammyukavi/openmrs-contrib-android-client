package org.openmrs.mobile.data;

public class DataOperationException extends RuntimeException {
	public static final long serialVersionUID = 1L;

	public DataOperationException() {

	}

	public DataOperationException(String message) {
		super(message);
	}

	public DataOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataOperationException(Throwable cause) {
		super(cause);
	}
}

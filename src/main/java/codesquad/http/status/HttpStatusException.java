package codesquad.http.status;

public class HttpStatusException extends RuntimeException {

	private final HttpStatus status;

	public HttpStatusException(HttpStatus httpStatus, String message) {
		super(message);
		this.status = httpStatus;
	}

	public HttpStatusException(HttpStatus httpStatus, String message, Throwable cause) {
		super(message, cause);
		this.status = httpStatus;
	}

	public HttpStatus getStatus() {
		return status;
	}
}

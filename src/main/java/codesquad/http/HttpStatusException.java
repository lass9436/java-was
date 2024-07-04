package codesquad.http;

public class HttpStatusException extends RuntimeException {

	private final HttpStatus status;
	private final String message;

	public HttpStatusException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}

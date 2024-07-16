package codesquad.http.status;

import java.util.List;
import java.util.Map;

public class HttpStatusException extends RuntimeException {

	private final HttpStatus status;
	private final Map<String, List<String>> headers;

	public HttpStatusException(HttpStatus httpStatus, String message) {
		super(message);
		this.status = httpStatus;
		this.headers = Map.of();
	}

	public HttpStatusException(HttpStatus httpStatus, String message, Map<String, List<String>> headers) {
		super(message);
		this.status = httpStatus;
		this.headers = headers;
	}

	public HttpStatusException(HttpStatus httpStatus, String message, Throwable cause) {
		super(message, cause);
		this.status = httpStatus;
		this.headers = Map.of();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}
}

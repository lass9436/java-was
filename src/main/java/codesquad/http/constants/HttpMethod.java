package codesquad.http.constants;

public enum HttpMethod {
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	PATCH("PATCH"),
	HEAD("HEAD"),
	OPTIONS("OPTIONS"),
	TRACE("TRACE");

	private final String method;

	HttpMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}
}

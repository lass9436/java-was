package codesquad.http.constants;

public enum HttpHandleType {
	STATIC("STATIC"),
	DYNAMIC("DYNAMIC");

	private final String type;

	HttpHandleType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}

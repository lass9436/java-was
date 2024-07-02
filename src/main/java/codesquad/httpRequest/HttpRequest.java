package codesquad.httpRequest;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpRequest {

	private final String method;
	private final String url;
	private final String version;
	private final Map<String, List<String>> headers;

	public HttpRequest(String method, String url, String version, Map<String, List<String>> headers) {
		this.method = method;
		this.url = url;
		this.version = version;
		this.headers = headers;
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getVersion() {
		return version;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		StringBuilder headersString = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			StringJoiner valuesJoiner = new StringJoiner(", ");
			for (String value : entry.getValue()) {
				valuesJoiner.add(value);
			}
			headersString.append(entry.getKey()).append(": ").append(valuesJoiner).append("\n");
		}
		return "HttpRequest{" +
			"method='" + method + '\'' +
			", url='" + url + '\'' +
			", version='" + version + '\'' +
			", headers=" + headersString +
			'}';
	}
}

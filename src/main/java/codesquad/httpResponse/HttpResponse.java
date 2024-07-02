package codesquad.httpResponse;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponse {

	private final String version;
	private final int statusCode;
	private final String statusMessage;

	private final Map<String, List<String>> headers;
	private final String body;

	public HttpResponse(String version, int statusCode, String statusMessage, Map<String, List<String>> headers,
		String body) {
		this.version = version;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.headers = headers;
		this.body = body;
	}

	@Override
	public String toString() {
		StringBuilder response = new StringBuilder();
		response.append(version).append(" ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			StringJoiner valuesJoiner = new StringJoiner(", ");
			for (String value : entry.getValue()) {
				valuesJoiner.add(value);
			}
			response.append(entry.getKey()).append(": ").append(valuesJoiner).append("\r\n");
		}

		response.append("\r\n");
		response.append(body);
		response.append("\r\n");

		return response.toString();
	}
}

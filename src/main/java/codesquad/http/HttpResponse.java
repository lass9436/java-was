package codesquad.http;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponse {

	private final String version;
	private final int statusCode;
	private final String statusMessage;
	private final Map<String, List<String>> headers;
	private final byte[] body;

	public HttpResponse(String version, HttpStatus httpStatus, Map<String, List<String>> headers,
		byte[] body) {
		this.version = version;
		this.statusCode = httpStatus.getCode();
		this.statusMessage = httpStatus.getReasonPhrase();
		this.headers = headers;
		this.body = body;
	}

	public byte[] getBytes() {
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

		byte[] headerBytes = response.toString().getBytes();
		byte[] responseBytes = new byte[headerBytes.length + body.length];

		System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
		System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

		return responseBytes;
	}

	public String getVersion() {
		return version;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}
}

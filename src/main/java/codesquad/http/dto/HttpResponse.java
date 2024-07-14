package codesquad.http.dto;

import static codesquad.utils.StringConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import codesquad.http.constants.HttpVersion;
import codesquad.http.status.HttpStatus;

public class HttpResponse {
	private HttpVersion version;
	private HttpStatus status;
	private Map<String, List<String>> headers;
	private byte[] body;

	public HttpResponse() {
		this.version = HttpVersion.HTTP_1_1;
		this.status = HttpStatus.OK;
		this.headers = new HashMap<>();
		this.body = null;
	}

	public HttpResponse(HttpVersion version, HttpStatus status, Map<String, List<String>> headers, byte[] body) {
		this.version = version;
		this.status = status;
		this.headers = headers;
		this.body = body;
	}

	// Getters
	public HttpVersion getVersion() {
		return version;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}

	// Setters
	public void setVersion(HttpVersion version) {
		this.version = version;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public void setResponse(HttpVersion version, HttpStatus status, Map<String, List<String>> headers, byte[] body) {
		this.version = version;
		this.status = status;
		this.headers = headers;
		this.body = body;
	}

	public byte[] getBytes() {
		StringBuilder response = new StringBuilder();
		response.append(version.getVersion())
			.append(SPACE)
			.append(status.getCode())
			.append(SPACE)
			.append(status.getReasonPhrase())
			.append(CRLF);

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			StringJoiner valuesJoiner = new StringJoiner(COMMA_SPACE);
			for (String value : entry.getValue()) {
				valuesJoiner.add(value);
			}
			response.append(entry.getKey()).append(COLON_SPACE).append(valuesJoiner).append(CRLF);
		}

		response.append(CRLF);

		byte[] headerBytes = response.toString().getBytes();
		byte[] responseBytes = new byte[headerBytes.length + body.length];

		System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
		System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

		return responseBytes;
	}
}

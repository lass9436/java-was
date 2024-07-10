package codesquad.http.dto;

import static codesquad.utils.StringConstants.*;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import codesquad.http.status.HttpStatus;

public record HttpResponse(String version, int statusCode, String statusMessage, Map<String, List<String>> headers,
						   byte[] body) {

	public HttpResponse(String version, HttpStatus httpStatus, Map<String, List<String>> headers, byte[] body) {
		this(version, httpStatus.getCode(), httpStatus.getReasonPhrase(), headers, body);
	}

	public byte[] getBytes() {
		StringBuilder response = new StringBuilder();
		response.append(version).append(SPACE).append(statusCode).append(SPACE).append(statusMessage).append(CRLF);

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

package codesquad.http.dto;

import static codesquad.utils.StringConstants.*;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import codesquad.http.constants.HttpVersion;
import codesquad.http.status.HttpStatus;

public record HttpResponse(HttpVersion version, HttpStatus status, Map<String, List<String>> headers, byte[] body) {

	public byte[] getBytes() {
		StringBuilder response = new StringBuilder();
		response.append(version.getVersion()).append(SPACE).append(status.getCode()).append(SPACE).append(status.getReasonPhrase()).append(CRLF);

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

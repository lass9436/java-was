package codesquad.http.parser;

import static codesquad.utils.StringConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyParser {

	public static Map<String, List<String>> parseRequestBody(InputStream inputStream,
		Map<String, List<String>> headers) throws IOException {
		int contentLength = getContentLength(headers);
		if (contentLength == -1) {
			return Map.of();  // 빈 JSON 객체를 반환합니다.
		}

		String contentType = headers.getOrDefault("Content-Type", List.of("")).get(0);
		byte[] bodyBytes = InputStreamUtils.readBytes(inputStream, contentLength);
		if (bodyBytes == null) {
			return Map.of();
		}

		if (contentType.equals("application/x-www-form-urlencoded")) {
			return parseFormUrlEncodedBody(new String(bodyBytes));
		}
		if (contentType.equals("application/json")) {
			return parseJsonBody(new String(bodyBytes));
		}

		return Map.of();  // 빈 JSON 객체를 반환합니다.
	}

	private static int getContentLength(Map<String, List<String>> headers) {
		List<String> contentLengthHeaders = headers.get("Content-Length");
		if (contentLengthHeaders == null || contentLengthHeaders.isEmpty()) {
			return -1;  // Content-Length 헤더가 없는 경우
		}

		try {
			return Integer.parseInt(contentLengthHeaders.get(0));
		} catch (NumberFormatException e) {
			return -1;  // Content-Length 헤더 값이 유효하지 않은 경우
		}
	}

	private static Map<String, List<String>> parseFormUrlEncodedBody(String body) throws IOException {
		Map<String, List<String>> result = new HashMap<>();
		String[] pairs = body.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = URLDecoder.decode(keyValue[0], UTF8);
			String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF8) : "";
			result.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
		}

		return result;
	}

	private static Map<String, List<String>> parseJsonBody(String body) throws IOException {
		Map<String, List<String>> result = new HashMap<>();

		body = body.trim();
		if (body.startsWith("{") && body.endsWith("}")) {
			body = body.substring(1, body.length() - 1).trim();
		} else {
			throw new IOException("Invalid JSON format");
		}

		String[] pairs = body.split(",");
		for (String pair : pairs) {
			String[] keyValue = pair.split(":", 2);
			if (keyValue.length == 2) {
				String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
				String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
				result.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
			}
		}

		return result;
	}
}

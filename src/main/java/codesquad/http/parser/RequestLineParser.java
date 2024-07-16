package codesquad.http.parser;

import java.util.HashMap;
import java.util.Map;

import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class RequestLineParser {

	public static Map<String, String> parseRequestLine(String requestLine) {
		if (requestLine == null || requestLine.isEmpty()) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "요청 라인이 비어있습니다.");
		}

		String[] requestLineParts = requestLine.split(" ");
		if (requestLineParts.length != 3) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "요청 라인 형식이 잘못되었습니다.");
		}

		Map<String, String> requestLineMap = new HashMap<>();
		requestLineMap.put("method", requestLineParts[0]);
		requestLineMap.put("url", requestLineParts[1]);
		requestLineMap.put("version", requestLineParts[2]);

		return requestLineMap;
	}

	public static String extractPath(String url) {
		int queryIndex = url.indexOf('?');
		if (queryIndex == -1) {
			return url;
		}
		return url.substring(0, queryIndex);
	}

	public static String extractQueryString(String url) {
		int queryIndex = url.indexOf('?');
		if (queryIndex == -1) {
			return "";
		}
		return url.substring(queryIndex + 1);
	}
}

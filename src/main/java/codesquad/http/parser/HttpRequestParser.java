package codesquad.http.parser;

import static codesquad.utils.StringConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;
import codesquad.session.SessionManager;

public class HttpRequestParser {

	public static HttpRequest parse(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();
		Map<String, String> requestLineMap = parseRequestLine(requestLine);

		String url = requestLineMap.get("url");
		String path = extractPath(url);
		String queryString = extractQueryString(url);

		Map<String, List<String>> queryParams = parseQueryParams(queryString);
		Map<String, List<String>> headers = parseHeaders(reader);
		Map<String, List<String>> body = parseRequestBody(reader, headers);

		HttpMethod httpMethod = HttpMethod.valueOf(requestLineMap.get("method"));
		HttpVersion httpVersion = HttpVersion.valueOf(
			requestLineMap.get("version").replace('/', '_').replace('.', '_'));

		return new HttpRequest(
			httpMethod,
			path,
			httpVersion,
			headers,
			queryParams,
			body
		);
	}

	private static Map<String, String> parseRequestLine(String requestLine) {
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

	private static String extractPath(String url) {
		int queryIndex = url.indexOf('?');
		if (queryIndex == -1) {
			return url;
		}
		return url.substring(0, queryIndex);
	}

	private static String extractQueryString(String url) {
		int queryIndex = url.indexOf('?');
		if (queryIndex == -1) {
			return "";
		}
		return url.substring(queryIndex + 1);
	}

	private static Map<String, List<String>> parseHeaders(BufferedReader reader) throws IOException {
		Map<String, List<String>> headers = new HashMap<>();
		String headerLine;

		while (!(headerLine = reader.readLine()).isEmpty()) {
			int index = headerLine.indexOf(":");
			if (index == -1) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST, "헤더 라인 형식이 잘못되었습니다.");
			}

			String key = headerLine.substring(0, index).trim();
			String value = headerLine.substring(index + 1).trim();

			if (key.isEmpty() || value.isEmpty()) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST, "헤더 키 또는 밸류가 비어있습니다.");
			}

			if (key.equalsIgnoreCase("Cookie")) {
				String[] cookieValues = value.split(";");
				for (String cookieValue : cookieValues) {
					String[] cookiePair = cookieValue.trim().split("=");
					if (cookiePair.length == 2) {
						String cookieName = cookiePair[0].trim();
						String cookieData = cookiePair[1].trim();
						if (cookieName.equalsIgnoreCase("SID")) {
							// Set SID in session manager
							SessionManager.setThreadLocalSID(cookieData);
						}
					}
				}
				continue;
			}

			String[] values = value.split(",");
			for (String val : values) {
				headers.computeIfAbsent(key, k -> new ArrayList<>()).add(val.trim());
			}
		}
		return headers;
	}

	public static Map<String, List<String>> parseQueryParams(String queryString) {
		Map<String, List<String>> queryParams = new HashMap<>();
		if (queryString == null || queryString.isEmpty()) {
			return queryParams;
		}

		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			parsePair(pair, queryParams);
		}
		return queryParams;
	}

	private static void parsePair(String pair, Map<String, List<String>> queryParams) {
		String[] keyValue = pair.split("=");
		try {
			if (keyValue.length == 2) {
				addParam(queryParams, keyValue[0], keyValue[1]);
			} else if (keyValue.length == 1) {
				addParam(queryParams, keyValue[0], "");
			}
		} catch (UnsupportedEncodingException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "URL 쿼리 파라미터 파싱에 실패했습니다.", e);
		}
	}

	private static void addParam(Map<String, List<String>> queryParams, String key, String value) throws
		UnsupportedEncodingException {
		String decodedKey = URLDecoder.decode(key, UTF8);
		String decodedValue = URLDecoder.decode(value, UTF8);
		queryParams.computeIfAbsent(decodedKey, k -> new ArrayList<>()).add(decodedValue);
	}

	private static Map<String, List<String>> parseRequestBody(BufferedReader reader,
		Map<String, List<String>> headers) throws IOException {
		int contentLength = getContentLength(headers);
		if (contentLength == -1) {
			return Map.of();  // 빈 JSON 객체를 반환합니다.
		}

		String contentType = headers.getOrDefault("Content-Type", List.of("")).get(0);
		String body = readBody(reader, contentLength);
		if (body == null) {
			return Map.of();
		}

		if (contentType.equals("application/x-www-form-urlencoded")) {
			return parseFormUrlEncodedBody(body);
		}
		if (contentType.equals("application/json")) {
			return parseJsonBody(body);
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

	private static String readBody(BufferedReader reader, int contentLength) throws IOException {
		char[] buffer = new char[1024];
		StringBuilder body = new StringBuilder();
		int totalRead = 0;
		int bytesRead;

		while (totalRead < contentLength
			&& (bytesRead = reader.read(buffer, 0, Math.min(buffer.length, contentLength - totalRead))) != -1) {
			body.append(buffer, 0, bytesRead);
			totalRead += bytesRead;
		}

		if (totalRead != contentLength) {
			return null;  // 실제로 읽은 길이가 Content-Length와 다르면 null 반환
		}

		return body.toString();
	}

	private static Map<String, List<String>> parseFormUrlEncodedBody(String body) throws UnsupportedEncodingException {
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

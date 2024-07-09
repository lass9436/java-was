package codesquad.http.parser;

import static codesquad.StringUtils.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.http.dto.HttpRequest;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class HttpRequestParser {

	public static HttpRequest parse(BufferedReader reader) throws IOException {
		Map<String, String> requestLineMap = new HashMap<>();
		String requestLine = reader.readLine();

		if (requestLine == null || requestLine.isEmpty()) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "요청 라인이 비어있습니다.");
		}

		String[] requestLineParts = requestLine.split(" ");
		if (requestLineParts.length != 3) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "요청 라인 형식이 잘못되었습니다.");
		}

		// URL 파싱
		String url = requestLineParts[1];
		String path;
		String queryString;
		int queryIndex = url.indexOf('?');
		if (queryIndex == -1) {
			path = url;
			queryString = "";
		} else {
			path = url.substring(0, queryIndex);
			queryString = url.substring(queryIndex + 1);
		}

		requestLineMap.put("method", requestLineParts[0]);
		requestLineMap.put("url", path);
		requestLineMap.put("version", requestLineParts[2]);
		Map<String, List<String>> queryParams = parseQueryParams(queryString);

		Map<String, List<String>> headers = new HashMap<>();
		String headerLine = null;
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

			String[] values = value.split(",");
			for (String val : values) {
				headers.computeIfAbsent(key, k -> new ArrayList<>()).add(val.trim());
			}
		}

		return new HttpRequest(requestLineMap.get("method"), requestLineMap.get("url"), requestLineMap.get("version"),
			headers, queryParams);
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
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "URL 쿼리 파라미터 파싱에 실패했습니다.");
		}
	}

	private static void addParam(Map<String, List<String>> queryParams, String key, String value) throws UnsupportedEncodingException {
		String decodedKey = URLDecoder.decode(key, UTF8);
		String decodedValue = URLDecoder.decode(value, UTF8);
		queryParams.computeIfAbsent(decodedKey, k -> new ArrayList<>()).add(decodedValue);
	}
}

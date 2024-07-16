package codesquad.http.parser;

import static codesquad.utils.StringConstants.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class QueryStringParser {

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
}

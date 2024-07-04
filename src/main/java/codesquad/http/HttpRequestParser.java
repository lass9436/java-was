package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

	public static HttpRequest parse(BufferedReader reader) throws IOException {
		Map<String, String> requestLineMap = new HashMap<>();
		String requestLine = reader.readLine();

		if (requestLine == null || requestLine.isEmpty()) {
			throw new IllegalArgumentException("요청 라인이 비어있습니다.");
		}

		String[] requestLineParts = requestLine.split(" ");
		if (requestLineParts.length != 3) {
			throw new IllegalArgumentException("요청 라인 형식이 잘못되었습니다.");
		}

		requestLineMap.put("method", requestLineParts[0]);
		requestLineMap.put("url", requestLineParts[1]);
		requestLineMap.put("version", requestLineParts[2]);

		Map<String, List<String>> headers = new HashMap<>();
		String headerLine = null;
		while (!(headerLine = reader.readLine()).isEmpty()) {
			int index = headerLine.indexOf(":");

			if (index == -1) {
				throw new IllegalArgumentException("헤더 라인 형식이 잘못되었습니다.");
			}

			String key = headerLine.substring(0, index).trim();
			String value = headerLine.substring(index + 1).trim();

			if (key.isEmpty() || value.isEmpty()) {
				throw new IllegalArgumentException("헤더 키 또는 밸류가 비어있습니다.");
			}

			String[] values = value.split(",");
			for (String val : values) {
				headers.computeIfAbsent(key, k -> new ArrayList<>()).add(val.trim());
			}
		}

		return new HttpRequest(requestLineMap.get("method"), requestLineMap.get("url"), requestLineMap.get("version"), headers);
	}
}

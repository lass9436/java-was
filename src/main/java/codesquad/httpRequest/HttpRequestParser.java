package codesquad.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

	public static HttpRequest parse(BufferedReader reader) throws IOException {
		Map<String, String> requestLineMap = new HashMap<>();
		String[] requestLineParts = reader.readLine().split(" ");
		if (requestLineParts.length == 3) {
			requestLineMap.put("method", requestLineParts[0]);
			requestLineMap.put("url", requestLineParts[1]);
			requestLineMap.put("version", requestLineParts[2]);
		}

		Map<String, List<String>> headers = new HashMap<>();
		String headerLine = null;
		while (!(headerLine = reader.readLine()).isEmpty()) {
			int index = headerLine.indexOf(":");
			if (index != -1) {
				String key = headerLine.substring(0, index).trim();
				String value = headerLine.substring(index + 1).trim();

				String[] values = value.split(",");
				for (String val : values) {
					headers.computeIfAbsent(key, k -> new ArrayList<>()).add(val.trim());
				}
			}
		}

		return new HttpRequest(requestLineMap.get("method"), requestLineMap.get("url"), requestLineMap.get("version"), headers);
	}
}

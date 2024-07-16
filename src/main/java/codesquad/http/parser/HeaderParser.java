package codesquad.http.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;
import codesquad.session.SessionManager;

public class HeaderParser {

	public static Map<String, List<String>> parseHeaders(InputStream inputStream) throws IOException {
		Map<String, List<String>> headers = new HashMap<>();
		String headerLine;

		while (!(headerLine = InputStreamUtils.readLine(inputStream)).isEmpty()) {
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
}

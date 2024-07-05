package codesquad.http.dto;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public record HttpRequest(String method, String url, String version, Map<String, List<String>> headers,
						  Map<String, List<String>> parameters) {

	@Override
	public String toString() {
		StringBuilder headersString = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			StringJoiner valuesJoiner = new StringJoiner(", ");
			for (String value : entry.getValue()) {
				valuesJoiner.add(value);
			}
			headersString.append(entry.getKey()).append(": ").append(valuesJoiner).append("\n");
		}
		return "HttpRequest{" +
			"method='" + method + '\'' +
			", url='" + url + '\'' +
			", version='" + version + '\'' +
			", headers=" + headersString +
			'}';
	}
}

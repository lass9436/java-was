package codesquad.http.dto;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;

public record HttpRequest(HttpMethod method, String path, HttpVersion version, Map<String, List<String>> headers,
						  Map<String, List<String>> parameters, Map<String, List<String>> body) {

	@Override
	public String toString() {
		return "HttpRequest{" +
			"method='" + method.getMethod() + '\'' +
			", path='" + path + '\'' +
			", version='" + version.getVersion() + '\'' +
			", headers=" + mapToString(headers) +
			", parameters=" + mapToString(parameters) +
			", body=" + mapToString(body) +
			'}';
	}

	private String mapToString(Map<String, List<String>> map) {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			StringJoiner valuesJoiner = new StringJoiner(", ");
			for (String value : entry.getValue()) {
				valuesJoiner.add(value);
			}
			result.append(entry.getKey()).append(": ").append(valuesJoiner).append("\n");
		}
		return result.toString();
	}
}

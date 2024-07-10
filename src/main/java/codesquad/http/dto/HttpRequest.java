package codesquad.http.dto;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public record HttpRequest(String method, String path, String version, Map<String, List<String>> headers,
						  Map<String, List<String>> parameters, Map<String, List<String>> body) {

	@Override
	public String toString() {
		String headersString = mapToString(headers);
		String parametersString = mapToString(parameters);
		String bodyString = mapToString(body);

		return "HttpRequest{" +
			"method='" + method + '\'' +
			", path='" + path + '\'' +
			", version='" + version + '\'' +
			", headers=" + headersString +
			", parameters=" + parametersString +
			", body=" + bodyString +
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

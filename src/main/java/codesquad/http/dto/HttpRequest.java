package codesquad.http.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;

public class HttpRequest {
	private HttpMethod method;
	private String path;
	private HttpVersion version;
	private Map<String, List<String>> headers;
	private Map<String, List<String>> parameters;
	private Map<String, List<String>> body;

	public HttpRequest() {
		method = HttpMethod.GET;
		path = "/";
		version = HttpVersion.HTTP_1_1;
		headers = new HashMap<>();
		parameters = new HashMap<>();
		body = new HashMap<>();
	}

	public HttpRequest(HttpMethod method, String path, HttpVersion version,
		Map<String, List<String>> headers, Map<String, List<String>> parameters,
		Map<String, List<String>> body) {
		this.method = method;
		this.path = path;
		this.version = version;
		this.headers = headers;
		this.parameters = parameters;
		this.body = body;
	}

	// Getters
	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public HttpVersion getVersion() {
		return version;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	public Map<String, List<String>> getBody() {
		return body;
	}

	// Setters
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setVersion(HttpVersion version) {
		this.version = version;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public void setParameters(Map<String, List<String>> parameters) {
		this.parameters = parameters;
	}

	public void setBody(Map<String, List<String>> body) {
		this.body = body;
	}

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

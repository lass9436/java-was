package codesquad.httpHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.httpRequest.HttpRequest;
import codesquad.httpResponse.HttpResponse;

public class HttpHandler {

	private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
	private static final String STATIC_ROOT_PATH = "src/main/resources/static/";

	private static final Map<String, String> MIME_TYPES = Map.of(
		"html", "text/html",
		"css", "text/css",
		"js", "application/javascript",
		"json", "application/json",
		"png", "image/png",
		"jpg", "image/jpeg",
		"jpeg", "image/jpeg",
		"gif", "image/gif",
		"svg", "image/svg+xml",
		"ico", "image/x-icon"
	);

	private static final Map<String, String> urlMapping = Map.of(
		"/", "/index.html",
		"/registration", "/registration/index.html"
	);

	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		String url = httpRequest.getUrl();
		String path = urlMapping.getOrDefault(url, url);
		File file = new File(STATIC_ROOT_PATH + path);

		if (!file.exists() || file.isDirectory()) {
			return new HttpResponse(httpRequest.getVersion(), 404, "Not Found", Map.of(), "<h1>404 Not Found</h1>");
		}

		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		String version = httpRequest.getVersion();
		int statusCode = 200;
		String statusMessage = "OK";

		String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		String mimeType = MIME_TYPES.get(extension);

		Map<String, List<String>> headers = new HashMap<>();
		headers.put("Content-Type", List.of(mimeType));

		StringBuilder body = new StringBuilder();
		String line = null;
		while ((line = fileReader.readLine()) != null) {
			body.append(line).append("\r\n");
		}

		return new HttpResponse(version, statusCode, statusMessage, headers, body.toString());
	}
}

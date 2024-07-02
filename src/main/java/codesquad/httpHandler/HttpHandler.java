package codesquad.httpHandler;

import java.io.BufferedReader;
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

	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader("src/main/resources/static/index.html"));
		String version = httpRequest.getVersion();
		int statusCode = 200;
		String statusMessage = "OK";

		Map<String, List<String>> headers = new HashMap<>();
		headers.put("Content-Type", List.of("text/html"));

		StringBuilder body = new StringBuilder();
		String line = null;
		while ((line = fileReader.readLine()) != null) {
			body.append(line).append("\r\n");
		}

		return new HttpResponse(version, statusCode, statusMessage, headers, body.toString());
	}
}

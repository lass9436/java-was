package codesquad.http.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpHandler;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.exception.HttpStatusException;
import codesquad.http.status.HttpStatus;

public class HttpStaticHandler {

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

	public HttpResponse handle(HttpRequest httpRequest) {
		String url = httpRequest.url();
		String path = urlMapping.getOrDefault(url, url);
		File file = new File(STATIC_ROOT_PATH + path);

		if (!file.exists() || file.isDirectory()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "File not found");
		}

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			String version = httpRequest.version();

			String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			String mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

			Map<String, List<String>> headers = new HashMap<>();
			headers.put("Content-Type", List.of(mimeType));
			headers.put("Content-Length", List.of(String.valueOf(file.length())));

			byte[] body = new byte[(int)file.length()];
			int bytesRead = fileInputStream.read(body);
			if (bytesRead != body.length) {
				throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 정상적으로 읽지 못했습니다.");
			}
			return new HttpResponse(version, HttpStatus.OK, headers, body);
		} catch (IOException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 정상적으로 읽지 못했습니다.");
		}
	}
}

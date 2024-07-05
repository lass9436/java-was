package codesquad.http.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpHandler;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class HttpStaticHandler {

	private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
	private static final String STATIC_ROOT_PATH = "static";

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
		String resourcePath = STATIC_ROOT_PATH + path;

		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
			if (inputStream == null) {
				throw new HttpStatusException(HttpStatus.NOT_FOUND, "File not found");
			}

			byte[] body = readAllBytes(inputStream);
			String version = httpRequest.version();
			String extension = resourcePath.substring(resourcePath.lastIndexOf(".") + 1);
			String mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

			Map<String, List<String>> headers = new HashMap<>();
			headers.put("Content-Type", List.of(mimeType));
			headers.put("Content-Length", List.of(String.valueOf(body.length)));

			return new HttpResponse(version, HttpStatus.OK, headers, body);
		} catch (IOException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read the file");
		}
	}

	private byte[] readAllBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int nRead;
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}
}

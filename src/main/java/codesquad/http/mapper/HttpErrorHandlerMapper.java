package codesquad.http.mapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class HttpErrorHandlerMapper {

	private static final String ERROR_PAGES_PATH = "static/error/";

	private static final Map<HttpStatus, String> ERROR_PAGE_MAPPING = Map.of(
		HttpStatus.NOT_FOUND, "404.html",
		HttpStatus.METHOD_NOT_ALLOWED, "405.html",
		HttpStatus.INTERNAL_SERVER_ERROR, "500.html"
	);

	public HttpResponse handle(HttpStatusException e) {
		HttpStatus status = e.getStatus();
		String errorPage = ERROR_PAGE_MAPPING.getOrDefault(status, "500.html");
		String resourcePath = ERROR_PAGES_PATH + errorPage;

		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
			if (inputStream == null) {
				throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error page not found");
			}

			byte[] body = inputStream.readAllBytes();
			String mimeType = "text/html";

			Map<String, List<String>> headers = new HashMap<>();
			headers.put("Content-Type", List.of(mimeType));
			headers.put("Content-Length", List.of(String.valueOf(body.length)));

			return new HttpResponse(HttpVersion.HTTP_1_1, status, headers, body);
		} catch (Exception ex) {
			// 만약 에러 페이지 로딩 중 또 다른 예외가 발생하면 기본 500 에러 페이지를 반환
			String defaultErrorMessage = "500 Internal Server Error";
			Map<String, List<String>> headers = new HashMap<>();
			headers.put("Content-Type", List.of("text/plain"));
			headers.put("Content-Length", List.of(String.valueOf(defaultErrorMessage.length())));
			return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR, headers,
				defaultErrorMessage.getBytes());
		}
	}
}

package codesquad.http.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.model.User;

public class HttpDynamicHandler {

	private final Logger logger = LoggerFactory.getLogger(HttpDynamicHandler.class);

	public HttpResponse handle(HttpRequest httpRequest) {
		if ("/create".equalsIgnoreCase(httpRequest.path())) {
			Map<String, List<String>> queryParams = httpRequest.parameters();
			String userId = getFirstValue(queryParams, "userId");
			String password = getFirstValue(queryParams, "password");
			String name = getFirstValue(queryParams, "name");
			String email = getFirstValue(queryParams, "email");

			final User user = new User(userId, password, name, email);
			logger.info("Creating user: {}", user);

			return new HttpResponse("HTTP/1.1", HttpStatus.OK, Map.of(),
				new byte[0]);
		}
		// 처리할 수 없는 요청에 대해서는 null 반환
		return null;
	}

	private String getFirstValue(Map<String, List<String>> queryParams, String key) {
		List<String> values = queryParams.get(key);
		return (values == null || values.isEmpty()) ? null : values.get(0);
	}
}

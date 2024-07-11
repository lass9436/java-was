package codesquad.http.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.annotation.HttpFunction;
import codesquad.http.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.model.User;
import codesquad.model.UserRepository;
import codesquad.session.SessionManager;

@HttpHandler
public class UserHandler {

	private final Logger logger = LoggerFactory.getLogger(UserHandler.class);

	private final UserRepository userRepository = new UserRepository();

	@HttpFunction(path = "/create", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public HttpResponse join(HttpRequest httpRequest) {
		Map<String, List<String>> body = httpRequest.body();
		String userId = body.get("userId").get(0);
		String password = body.get("password").get(0);
		String name = body.get("name").get(0);
		String email = body.get("email").get(0);

		final User user = new User(userId, password, name, email);
		userRepository.create(user);

		logger.info("Creating user: {}", user);
		Map<String, List<String>> headers = Map.of("Location", List.of("/index.html"));
		return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
	}

	@HttpFunction(path = "/login", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public HttpResponse login(HttpRequest httpRequest) {
		Map<String, List<String>> body = httpRequest.body();
		String id = body.get("id").get(0);
		String password = body.get("password").get(0);

		User user = userRepository.findById(id);
		if (user != null && user.password().equals(password)) {
			String sessionId = SessionManager.putSession("user", user);
			Map<String, List<String>> headers = Map.of(
				"Location", List.of("/index.html"),
				"Set-Cookie", List.of("SID=" + sessionId + "; Path=/")
			);
			return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
		} else {
			Map<String, List<String>> headers = Map.of("Location", List.of("/user/login_failed.html"));
			return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
		}
	}
}
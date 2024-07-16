package codesquad.http.handler;

import static codesquad.server.WebWorker.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.render.RenderData;
import codesquad.http.status.HttpStatus;
import codesquad.model.user.User;
import codesquad.model.user.UserRepository;
import codesquad.session.SessionManager;

@HttpHandler
public class UserApiHandler {

	private final Logger logger = LoggerFactory.getLogger(UserApiHandler.class);

	private final UserRepository userRepository;

	public UserApiHandler(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@HttpFunction(path = "/create", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public RenderData join() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		Map<String, List<String>> body = httpRequest.getBody();
		String userId = body.get("userId").get(0);
		String password = body.get("password").get(0);
		String name = body.get("name").get(0);
		String email = body.get("email").get(0);

		final User user = new User(userId, password, name, email);
		userRepository.create(user);

		logger.info("Creating user: {}", user);
		Map<String, List<String>> headers = Map.of("Location", List.of("/"));
		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);

		return null;
	}

	@HttpFunction(path = "/login", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public RenderData login() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		Map<String, List<String>> body = httpRequest.getBody();
		String id = body.get("id").get(0);
		String password = body.get("password").get(0);
		try {
			User user = userRepository.findById(id);
			if (user.getPassword().equals(password)) {
				String sessionId = SessionManager.putSession("user", user);
				Map<String, List<String>> headers = Map.of(
					"Location", List.of("/"),
					"Set-Cookie", List.of("SID=" + sessionId + "; Path=/")
				);
				httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
				return null;
			}
			Map<String, List<String>> headers = Map.of("Location", List.of("/login"));
			httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
			return null;
		} catch (Exception e) {
			Map<String, List<String>> headers = Map.of("Location", List.of("/login"));
			httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
			return null;
		}
	}

	@HttpFunction(path = "/isLogin", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData isLogin() {
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		User user = (User)SessionManager.getSession("user");
		if (user != null) {
			httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, Map.of(), user.toString().getBytes());
			return null;
		}
		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.UNAUTHORIZED, Map.of(), new byte[0]);
		return null;
	}

	@HttpFunction(path = "/users", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData getUsers() {
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		List<User> list = userRepository.findAll();
		String body = list.stream()
			.map(User::toString)
			.collect(Collectors.joining(",", "[", "]"));
		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, Map.of(), body.getBytes());
		return null;
	}

	@HttpFunction(path = "/logout", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public RenderData logout() {
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		SessionManager.removeSession();
		Map<String, List<String>> headers = Map.of(
			"Set-Cookie", List.of("SID=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/"),
			"Location", List.of("/") // 리다이렉트 헤더 추가
		);
		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND, headers, new byte[0]);
		return null;
	}
}

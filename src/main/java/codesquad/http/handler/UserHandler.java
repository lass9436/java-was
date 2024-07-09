package codesquad.http.handler;

import static codesquad.utils.JsonUtils.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.model.User;
import codesquad.model.UserRepository;

public class UserHandler {

	private final Logger logger = LoggerFactory.getLogger(UserHandler.class);

	private final UserRepository userRepository = new UserRepository();

	public HttpResponse join(HttpRequest httpRequest) {
		JsonNode body = httpRequest.body();
		String userId = getFirstValueFromBody(body, "userId");
		String password = getFirstValueFromBody(body, "password");
		String name = getFirstValueFromBody(body, "name");
		String email = getFirstValueFromBody(body, "email");

		final User user = new User(userId, password, name, email);
		userRepository.create(user);

		logger.info("Creating user: {}", user);

		return new HttpResponse("HTTP/1.1", HttpStatus.OK, Map.of(), new byte[0]);
	}
}

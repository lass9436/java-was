package codesquad.http.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.model.User;
import codesquad.model.UserRepository;

public class UserHandlerTest {

	private static UserHandler userHandler;
	private static ObjectMapper objectMapper;
	private static UserRepository userRepository;

	@BeforeAll
	public static void setUp() {
		userHandler = new UserHandler();
		objectMapper = new ObjectMapper();
		userRepository = new UserRepository();
	}

	@Test
	public void 사용자_생성_성공() throws Exception {
		String jsonString = "{ \"userId\": \"john_doe\", \"password\": \"password123\", \"name\": \"John Doe\", \"email\": \"john@example.com\" }";
		JsonNode body = objectMapper.readTree(jsonString);
		HttpRequest request = new HttpRequest(
			"POST",
			"/join",
			"HTTP/1.1",
			Map.of(),
			Map.of(),
			body
		);

		HttpResponse response = userHandler.join(request);

		assertEquals(HttpStatus.FOUND.getCode(), response.statusCode());
		assertTrue(response.headers().containsKey("Location"));
		assertEquals("/index.html", response.headers().get("Location").get(0));

		User user = userRepository.findById("john_doe");
		assertEquals("john_doe", user.userId());
		assertEquals("password123", user.password());
		assertEquals("John Doe", user.name());
		assertEquals("john@example.com", user.email());
	}

}

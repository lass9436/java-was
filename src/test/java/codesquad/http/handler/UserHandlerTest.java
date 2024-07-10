package codesquad.http.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.parser.HttpRequestParser;
import codesquad.http.status.HttpStatus;
import codesquad.model.User;
import codesquad.model.UserRepository;

public class UserHandlerTest {

	private static UserHandler userHandler;
	private static UserRepository userRepository;

	@BeforeAll
	public static void setUp() {
		userHandler = new UserHandler();
		userRepository = new UserRepository();
	}

	@Test
	public void 사용자_생성_성공() throws Exception {
		String jsonBody = "{ \"userId\": \"john_doe\", \"password\": \"password123\", \"name\": \"John Doe\", \"email\": \"john@example.com\" }";
		byte[] jsonBodyBytes = jsonBody.getBytes("UTF-8");
		String httpRequestString =
			"POST /join HTTP/1.1\r\n" +
				"Host: localhost:8080\r\n" +
				"Connection: keep-alive\r\n" +
				"Content-Type: application/json\r\n" +
				"Content-Length: " + jsonBodyBytes.length + "\r\n" +
				"\r\n" +
				jsonBody;

		BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
		HttpRequest request = HttpRequestParser.parse(reader);

		HttpResponse response = userHandler.join(request);

		assertEquals(HttpStatus.FOUND.getCode(), response.status().getCode());
		assertTrue(response.headers().containsKey("Location"));
		assertEquals("/index.html", response.headers().get("Location").get(0));

		User user = userRepository.findById("john_doe");
		assertEquals("john_doe", user.userId());
		assertEquals("password123", user.password());
		assertEquals("John Doe", user.name());
		assertEquals("john@example.com", user.email());
	}
}

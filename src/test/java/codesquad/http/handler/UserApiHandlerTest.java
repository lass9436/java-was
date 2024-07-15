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
import codesquad.model.UserRepositoryImpl;
import codesquad.server.WebWorker;
import codesquad.utils.StringConstants;

public class UserApiHandlerTest {

	private static UserApiHandler userApiHandler;
	private static UserRepositoryImpl userRepositoryImpl;

	@BeforeAll
	public static void setUp() {
		userRepositoryImpl = new UserRepositoryImpl();
		userApiHandler = new UserApiHandler(userRepositoryImpl);
	}

	@Test
	public void 사용자_생성_성공() throws Exception {
		String jsonBody = "{ \"userId\": \"john_doe\", \"password\": \"password123\", \"name\": \"John Doe\", \"email\": \"john@example.com\" }";
		byte[] jsonBodyBytes = jsonBody.getBytes(StringConstants.UTF8);
		String httpRequestString =
			"POST /create HTTP/1.1\r\n" +
				"Host: localhost:8080\r\n" +
				"Connection: keep-alive\r\n" +
				"Content-Type: application/json\r\n" +
				"Content-Length: " + jsonBodyBytes.length + "\r\n" +
				"\r\n" +
				jsonBody;

		BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
		HttpRequest request = HttpRequestParser.parse(reader);

		// 스레드 로컬에 요청과 응답 설정
		WebWorker.HTTP_REQUEST_THREAD_LOCAL.set(request);
		HttpResponse response = new HttpResponse();
		WebWorker.HTTP_RESPONSE_THREAD_LOCAL.set(response);

		// 핸들러 메서드 호출
		userApiHandler.join();

		// 응답 검증
		assertEquals(HttpStatus.FOUND.getCode(), response.getStatus().getCode());
		assertTrue(response.getHeaders().containsKey("Location"));
		assertEquals("/", response.getHeaders().get("Location").get(0));

		// 저장소에 사용자가 제대로 생성되었는지 검증
		User user = userRepositoryImpl.findById("john_doe");
		assertEquals("john_doe", user.getUserId());
		assertEquals("password123", user.getPassword());
		assertEquals("John Doe", user.getName());
		assertEquals("john@example.com", user.getEmail());

		// 스레드 로컬 변수 정리
		WebWorker.HTTP_REQUEST_THREAD_LOCAL.remove();
		WebWorker.HTTP_RESPONSE_THREAD_LOCAL.remove();
	}
}

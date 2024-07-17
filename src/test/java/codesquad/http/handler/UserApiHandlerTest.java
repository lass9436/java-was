package codesquad.http.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import codesquad.dto.HttpRequest;
import codesquad.dto.HttpResponse;
import codesquad.http.handler.user.UserApiHandler;
import codesquad.http.status.HttpStatus;
import codesquad.model.user.User;
import codesquad.model.user.UserRepositoryImpl;
import codesquad.server.WebWorker;
import codesquad.utils.StringConstants;
import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		// JSON Body 설정
		String jsonBody = "{ \"userId\": \"john_doe\", \"password\": \"password123\", \"name\": \"John Doe\", \"email\": \"john@example.com\" }";
		byte[] jsonBodyBytes = jsonBody.getBytes(StringConstants.UTF8);

		// HttpRequest 설정
		Map<String, List<String>> headers = new HashMap<>();
		headers.put("Host", List.of("localhost:8080"));
		headers.put("Connection", List.of("keep-alive"));
		headers.put("Content-Type", List.of("application/json"));
		headers.put("Content-Length", List.of(String.valueOf(jsonBodyBytes.length)));

		Map<String, List<Object>> body = new HashMap<>();
		body.put("userId", List.of((Object) "john_doe"));
		body.put("password", List.of((Object) "password123"));
		body.put("name", List.of((Object) "John Doe"));
		body.put("email", List.of((Object) "john@example.com"));

		HttpRequest request = new HttpRequest(HttpMethod.POST, "/create", HttpVersion.HTTP_1_1, headers, new HashMap<>(), body);

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

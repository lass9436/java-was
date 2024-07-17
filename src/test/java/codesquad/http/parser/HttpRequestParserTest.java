package codesquad.http.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.dto.HttpRequest;
import codesquad.http.status.HttpStatusException;

@DisplayName("HttpRequestParser 테스트")
class HttpRequestParserTest {

	@Nested
	class 요청_라인과_헤더를_테스트 {

		@Test
		void 요청_라인이_비어있는_경우_예외_발생() {
			String httpRequestString = "\r\n";
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(inputStream);
			});

			assertEquals("요청 라인이 비어있습니다.", exception.getMessage());
		}

		@Test
		void 요청_라인_형식이_잘못된_경우_예외_발생() {
			String httpRequestString = "GET /index.html\r\n";  // 잘못된 형식
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(inputStream);
			});

			assertEquals("요청 라인 형식이 잘못되었습니다.", exception.getMessage());
		}

		@Test
		void 헤더_라인_형식이_잘못된_경우_예외_발생() {
			String httpRequestString =
				"GET /index.html HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection keep-alive\r\n" + // 잘못된 헤더 형식 (콜론이 없음)
					"\r\n";
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(inputStream);
			});

			assertEquals("헤더 라인 형식이 잘못되었습니다.", exception.getMessage());
		}

		@Test
		void 헤더_키_또는_밸류가_비어있는_경우_예외_발생() {
			String httpRequestString =
				"GET /index.html HTTP/1.1\r\n" +
					"Host: \r\n" +  // 밸류가 비어있음
					"Connection: keep-alive\r\n" +
					"\r\n";
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(inputStream);
			});

			assertEquals("헤더 키 또는 밸류가 비어있습니다.", exception.getMessage());
		}

		@Test
		void 요청_라인과_헤더를_파싱한다() throws IOException {
			String httpRequestString =
				"GET /index.html HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
					"\r\n";
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

			assertEquals(HttpMethod.GET, httpRequest.getMethod());
			assertEquals("/index.html", httpRequest.getPath());
			assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());
			assertEquals("localhost:8080", httpRequest.getHeaders().get("Host").get(0));
			assertEquals("keep-alive", httpRequest.getHeaders().get("Connection").get(0));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("text/html"));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("application/xhtml+xml"));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("application/xml;q=0.9"));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("image/webp"));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("image/apng"));
			assertTrue(httpRequest.getHeaders().get("Accept").contains("*/*;q=0.8"));
		}
	}

	@Nested
	class 요청_라인과_헤더와_바디를_테스트 {

		@Test
		void 요청_라인과_헤더와_바디를_파싱한다() throws IOException {
			String jsonBody = "{\"name\":\"John\",\"age\":\"30\",\"email\":\"john@email.com\"}";
			byte[] jsonBodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/json\r\n" +
					"Content-Length: " + jsonBodyBytes.length + "\r\n" +
					"\r\n" +
					jsonBody;
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

			// 요청 라인 검증
			assertEquals(HttpMethod.POST, httpRequest.getMethod());
			assertEquals("/submit", httpRequest.getPath());
			assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());

			// 헤더 검증
			assertEquals("localhost:8080", httpRequest.getHeaders().get("Host").get(0));
			assertEquals("keep-alive", httpRequest.getHeaders().get("Connection").get(0));
			assertEquals("application/json", httpRequest.getHeaders().get("Content-Type").get(0));
			assertEquals(String.valueOf(jsonBodyBytes.length), httpRequest.getHeaders().get("Content-Length").get(0));

			// 바디 검증
			Map<String, List<Object>> expectedBody = Map.of(
				"name", List.of((Object) "John"),
				"age", List.of((Object) "30"),
				"email", List.of((Object) "john@email.com")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}

		@Test
		void 요청_라인과_헤더와_폼_데이터를_파싱한다() throws IOException {
			String formBody = "name=John&age=30";
			byte[] formBodyBytes = formBody.getBytes(StandardCharsets.UTF_8);
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"Content-Length: " + formBodyBytes.length + "\r\n" +
					"\r\n" +
					formBody;
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

			// 요청 라인 검증
			assertEquals(HttpMethod.POST, httpRequest.getMethod());
			assertEquals("/submit", httpRequest.getPath());
			assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());

			// 헤더 검증
			assertEquals("localhost:8080", httpRequest.getHeaders().get("Host").get(0));
			assertEquals("keep-alive", httpRequest.getHeaders().get("Connection").get(0));
			assertEquals("application/x-www-form-urlencoded", httpRequest.getHeaders().get("Content-Type").get(0));
			assertEquals(String.valueOf(formBodyBytes.length), httpRequest.getHeaders().get("Content-Length").get(0));

			// 바디 검증
			Map<String, List<Object>> expectedBody = Map.of(
				"name", List.of((Object) "John"),
				"age", List.of((Object) "30")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}

		@Test
		void 요청_라인과_헤더와_중복된_키를_포함한_폼_데이터를_파싱한다() throws IOException {
			String formBody = "name=John&age=30&name=Jane";
			byte[] formBodyBytes = formBody.getBytes(StandardCharsets.UTF_8);
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"Content-Length: " + formBodyBytes.length + "\r\n" +
					"\r\n" +
					formBody;
			InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes(StandardCharsets.UTF_8));

			HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

			// 요청 라인 검증
			assertEquals(HttpMethod.POST, httpRequest.getMethod());
			assertEquals("/submit", httpRequest.getPath());
			assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());

			// 헤더 검증
			assertEquals("localhost:8080", httpRequest.getHeaders().get("Host").get(0));
			assertEquals("keep-alive", httpRequest.getHeaders().get("Connection").get(0));
			assertEquals("application/x-www-form-urlencoded", httpRequest.getHeaders().get("Content-Type").get(0));
			assertEquals(String.valueOf(formBodyBytes.length), httpRequest.getHeaders().get("Content-Length").get(0));

			// 바디 검증
			Map<String, List<Object>> expectedBody = Map.of(
				"name", List.of((Object) "John", (Object) "Jane"),
				"age", List.of((Object) "30")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}
	}
}

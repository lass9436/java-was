package codesquad.http.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.status.HttpStatusException;

@DisplayName("HttpRequestParser 테스트")
class HttpRequestParserTest {

	@Nested
	class 요청_라인과_헤더를_테스트 {

		@Test
		void 요청_라인이_비어있는_경우_예외_발생() {
			String httpRequestString = "\r\n";

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(reader);
			});

			assertEquals("요청 라인이 비어있습니다.", exception.getMessage());
		}

		@Test
		void 요청_라인_형식이_잘못된_경우_예외_발생() {
			String httpRequestString = "GET /index.html\r\n";  // 잘못된 형식

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(reader);
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

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(reader);
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

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			Exception exception = assertThrows(HttpStatusException.class, () -> {
				HttpRequestParser.parse(reader);
			});

			assertEquals("헤더 키 또는 밸류가 비어있습니다.", exception.getMessage());
		}

		@Test
		void 요청_라인과_헤더를_파싱한다() throws IOException {
			String httpRequestString =
				"GET /index.html HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n"
					+
					"\r\n";

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			HttpRequest httpRequest = HttpRequestParser.parse(reader);

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
			byte[] jsonBodyBytes = jsonBody.getBytes("UTF-8");
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/json\r\n" +
					"Content-Length: " + jsonBodyBytes.length + "\r\n" +
					"\r\n" +
					jsonBody;

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			HttpRequest httpRequest = HttpRequestParser.parse(reader);

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
			Map<String, List<String>> expectedBody = Map.of(
				"name", List.of("John"),
				"age", List.of("30"),
				"email", List.of("john@email.com")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}

		@Test
		void 요청_라인과_헤더와_폼_데이터를_파싱한다() throws IOException {
			String formBody = "name=John&age=30";
			byte[] formBodyBytes = formBody.getBytes("UTF8");
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"Content-Length: " + formBodyBytes.length + "\r\n" +
					"\r\n" +
					formBody;

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			HttpRequest httpRequest = HttpRequestParser.parse(reader);

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
			Map<String, List<String>> expectedBody = Map.of(
				"name", List.of("John"),
				"age", List.of("30")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}

		@Test
		void 요청_라인과_헤더와_중복된_키를_포함한_폼_데이터를_파싱한다() throws IOException {
			String formBody = "name=John&age=30&name=Jane";
			byte[] formBodyBytes = formBody.getBytes("UTF8");
			String httpRequestString =
				"POST /submit HTTP/1.1\r\n" +
					"Host: localhost:8080\r\n" +
					"Connection: keep-alive\r\n" +
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"Content-Length: " + formBodyBytes.length + "\r\n" +
					"\r\n" +
					formBody;

			BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
			HttpRequest httpRequest = HttpRequestParser.parse(reader);

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
			Map<String, List<String>> expectedBody = Map.of(
				"name", List.of("John", "Jane"),
				"age", List.of("30")
			);
			assertEquals(expectedBody, httpRequest.getBody());
		}
	}

	@Nested
	class 쿼리_파라미터_파싱_테스트 {
		@Test
		void 빈_쿼리_문자열() {
			String queryString = "";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertTrue(result.isEmpty());
		}

		@Test
		void 단일_쿼리_파라미터() {
			String queryString = "key=value";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(1, result.size());
			assertEquals("value", result.get("key").get(0));
		}

		@Test
		void 여러_쿼리_파라미터() {
			String queryString = "key1=value1&key2=value2";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(2, result.size());
			assertEquals("value1", result.get("key1").get(0));
			assertEquals("value2", result.get("key2").get(0));
		}

		@Test
		void 동일한_키_여러_값() {
			String queryString = "key=value1&key=value2";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(1, result.size());
			assertEquals(2, result.get("key").size());
			assertEquals("value1", result.get("key").get(0));
			assertEquals("value2", result.get("key").get(1));
		}

		@Test
		void 값이_없는_키() {
			String queryString = "key=";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(1, result.size());
			assertEquals("", result.get("key").get(0));
		}

		@Test
		void 인코딩된_쿼리_파라미터() {
			String queryString = "key1=value%201&key2=value%202";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(2, result.size());
			assertEquals("value 1", result.get("key1").get(0));
			assertEquals("value 2", result.get("key2").get(0));
		}

		@Test
		void 값이_없는_키_여러개() {
			String queryString = "key1=&key2=";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(2, result.size());
			assertEquals("", result.get("key1").get(0));
			assertEquals("", result.get("key2").get(0));
		}

		@Test
		void 값이_없는_마지막_키() {
			String queryString = "key1=value1&key2=";
			Map<String, List<String>> result = HttpRequestParser.parseQueryParams(queryString);
			assertEquals(2, result.size());
			assertEquals("value1", result.get("key1").get(0));
			assertEquals("", result.get("key2").get(0));
		}
	}
}

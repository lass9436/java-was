package codesquad.httpRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestParser 테스트")
class HttpRequestParserTest {

	@Test
	void 요청라인과_헤더를_파싱한다() throws IOException {
		String httpRequestString =
			"GET /index.html HTTP/1.1\r\n" +
			"Host: localhost:8080\r\n" +
			"Connection: keep-alive\r\n" +
			"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
			"\r\n";

		BufferedReader reader = new BufferedReader(new StringReader(httpRequestString));
		HttpRequest httpRequest = HttpRequestParser.parse(reader);

		assertEquals("GET", httpRequest.getMethod());
		assertEquals("/index.html", httpRequest.getUrl());
		assertEquals("HTTP/1.1", httpRequest.getVersion());
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

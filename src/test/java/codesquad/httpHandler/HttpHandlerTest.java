package codesquad.httpHandler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import codesquad.httpRequest.HttpRequest;
import codesquad.httpResponse.HttpResponse;

@DisplayName("HttpHandler 테스트")
class HttpHandlerTest {

	private static HttpHandler httpHandler;

	@BeforeAll
	static void setUp() {
		httpHandler = new HttpHandler();
	}

	@Test
	void HttpRequest를_받아_HttpResponse를_반환한다() throws IOException {
		HttpResponse httpResponse = httpHandler.handle(new HttpRequest("GET", "/", "HTTP/1.1", null));
		assertNotNull(httpResponse);
	}

	@ParameterizedTest(name = "{index} => url={0}, expectedStatusCode={1}, expectedContentType={2}")
	@CsvSource({
		"'/', 200, 'text/html'",
		"'/global.css', 200, 'text/css'",
		"'/img/like.svg', 200, 'image/svg+xml'",
		"'/favicon.ico', 200, 'image/x-icon'",
		"'/nonexistent.html', 404, ''"
	})
	void url에_따라_정적_리소스를_반환한다(String url, int expectedStatusCode, String expectedContentType) throws IOException {
		HttpRequest httpRequest = new HttpRequest("GET", url, "HTTP/1.1", null);
		HttpResponse httpResponse = httpHandler.handle(httpRequest);

		assertEquals("HTTP/1.1", httpResponse.getVersion());
		assertEquals(expectedStatusCode, httpResponse.getStatusCode());
		if (!expectedContentType.isEmpty()) {
			assertTrue(httpResponse.getHeaders().containsKey("Content-Type"));
			assertEquals(expectedContentType, httpResponse.getHeaders().get("Content-Type").get(0));
		}
		assertFalse(httpResponse.getBody().isEmpty());
	}
}

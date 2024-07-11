package codesquad.http.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;

@DisplayName("HttpStaticHandler 테스트")
class HttpStaticHandlerMapperTest {

	private static HttpStaticHandlerMapper httpStaticHandlerMapper;

	@BeforeAll
	static void setUp() {
		httpStaticHandlerMapper = new HttpStaticHandlerMapper();
	}

	@Test
	void HttpRequest를_받아_HttpResponse를_반환한다() throws IOException {
		HttpResponse httpResponse = httpStaticHandlerMapper.handle(new HttpRequest(HttpMethod.GET, "/", HttpVersion.HTTP_1_1, null, null, null));
		assertNotNull(httpResponse);
	}

	@ParameterizedTest(name = "{index} => path={0}, expectedStatusCode={1}, expectedContentType={2}")
	@CsvSource({
		"'/', 200, 'text/html'",
		"'/global.css', 200, 'text/css'",
		"'/img/like.svg', 200, 'image/svg+xml'",
		"'/favicon.ico', 200, 'image/x-icon'",
	})
	void path에_따라_정적_리소스를_반환한다(String path, int expectedStatusCode, String expectedContentType) {
		HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, path, HttpVersion.HTTP_1_1, null, null, null);
		HttpResponse httpResponse = httpStaticHandlerMapper.handle(httpRequest);

		assertEquals(HttpVersion.HTTP_1_1, httpResponse.version());
		assertEquals(expectedStatusCode, httpResponse.status().getCode());
		if (!expectedContentType.isEmpty()) {
			assertTrue(httpResponse.headers().containsKey("Content-Type"));
			assertEquals(expectedContentType, httpResponse.headers().get("Content-Type").get(0));
		}
		assertTrue(httpResponse.body().length > 0);
	}
}
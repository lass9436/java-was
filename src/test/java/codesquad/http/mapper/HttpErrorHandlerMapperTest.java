package codesquad.http.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquad.http.constants.HttpVersion;
import codesquad.dto.HttpResponse;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

@DisplayName("HttpErrorHandlerMapper 테스트")
class HttpErrorHandlerMapperTest {

	private final HttpErrorHandlerMapper errorHandlerMapper = new HttpErrorHandlerMapper();

	@Test
	void handle_404_예외() {
		HttpStatusException exception = new HttpStatusException(HttpStatus.NOT_FOUND, "Page not found");

		HttpResponse response = errorHandlerMapper.handle(exception);

		assertEquals(HttpVersion.HTTP_1_1, response.getVersion());
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
		assertTrue(response.getHeaders().containsKey("Content-Type"));
		assertEquals("text/html", response.getHeaders().get("Content-Type").get(0));
		assertTrue(response.getBody().length > 0);
	}

	@Test
	void handle_405_예외() {
		HttpStatusException exception = new HttpStatusException(HttpStatus.METHOD_NOT_ALLOWED,
			"Method not allowed");

		HttpResponse response = errorHandlerMapper.handle(exception);

		assertEquals(HttpVersion.HTTP_1_1, response.getVersion());
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatus());
		assertTrue(response.getHeaders().containsKey("Content-Type"));
		assertEquals("text/html", response.getHeaders().get("Content-Type").get(0));
		assertTrue(response.getBody().length > 0);
	}

	@Test
	void handle_500_예외() {
		HttpStatusException exception = new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
			"Internal server error");

		HttpResponse response = errorHandlerMapper.handle(exception);

		assertEquals(HttpVersion.HTTP_1_1, response.getVersion());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
		assertTrue(response.getHeaders().containsKey("Content-Type"));
		assertEquals("text/html", response.getHeaders().get("Content-Type").get(0));
		assertTrue(response.getBody().length > 0);
	}

}

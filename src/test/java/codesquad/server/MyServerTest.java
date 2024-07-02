package codesquad.server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MyServer 테스트")
class MyServerTest {

	@BeforeAll
	static void setUp() {
		new Thread(() -> {
			try {
				MyServer.start();
			} catch (IOException ignored) {
			}
		}).start();
	}

	@Test
	void HTTP_요청을_보내고_HTTP_응답을_받는다() throws IOException, InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080")).GET().build();
		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
	}

	@AfterAll
	static void tearDown() throws IOException {
		MyServer.stop();
	}
}

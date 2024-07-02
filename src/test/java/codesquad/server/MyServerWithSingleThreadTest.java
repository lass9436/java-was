package codesquad.server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("MyServerWithSingleThread 테스트")
class MyServerWithSingleThreadTest {

	private static final Logger logger = LoggerFactory.getLogger(MyServerWithSingleThreadTest.class);

	@BeforeAll
	static void setUp() {
		new Thread(() -> {
			try {
				MyServerWithSingleThread.start();
			} catch (IOException ignored) {
			}
		}).start();
	}

	@Test
	void HTTP_요청을_보내고_HTTP_응답을_받는다() throws IOException, InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081")).GET().build();
		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
	}

	@Test
	void 동시에_많은_HTTP_요청을_보내고_응답을_받으려고_하면_소켓이_터진다() {
		int numRequests = 300; // 동시 요청 수
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (int i = 0; i < numRequests; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				HttpClient httpClient = HttpClient.newHttpClient();
				HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:8080"))
					.GET()
					.build();
				try {
					HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
					assertEquals(200, response.statusCode());
				} catch (IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
			futures.add(future);
		}

		long startTime = System.currentTimeMillis();

		Exception exception = assertThrows(Exception.class, () -> {
			for (CompletableFuture<Void> future : futures) {
				future.get(); // 각 태스크의 완료를 기다리고 예외가 발생했는지 확인
			}
		});

		long endTime = System.currentTimeMillis();

		logger.debug("get duration (millis): {}", (endTime - startTime));
		logger.debug(exception.getMessage());
	}

	@AfterAll
	static void tearDown() throws IOException {
		MyServerWithSingleThread.stop();
	}
}

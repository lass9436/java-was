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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("MyServer 테스트")
class WebServerTest {

	private static final Logger logger = LoggerFactory.getLogger(WebServerTest.class);
	private static final int BACKLOG = 5;
	private static final int requests = 20;

	@DisplayName("멀티 스레드 상황에서")
	@Nested
	class MultiThread {

		private static final int PORT = 8080;
		private static final int THREAD_POOL_SIZE = 2;
		private static final String URL = "http://localhost:" + PORT + "/";

		private static WebServer webServer;

		@BeforeAll
		static void setUp() throws IOException {
			webServer = new WebServer(PORT, BACKLOG, THREAD_POOL_SIZE);
			new Thread(() -> {
				try {
					webServer.start();
				} catch (IOException ignored) {
				}
			}).start();
		}

		@Test
		void HTTP_요청을_보내고_HTTP_응답을_받는다() throws IOException, InterruptedException {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, response.statusCode());
		}

		@Test
		void 동시에_여러_HTTP_요청을_보내고_응답을_받을_수_있다() throws InterruptedException, ExecutionException {
			List<CompletableFuture<Void>> futures = new ArrayList<>();

			for (int i = 0; i < requests; i++) {
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					HttpClient httpClient = HttpClient.newHttpClient();
					HttpRequest httpRequest = HttpRequest.newBuilder()
						.uri(URI.create(URL))
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

			for (CompletableFuture<Void> future : futures) {
				future.get(); // 각 태스크의 완료를 기다리고 예외가 발생하지 않았는지 확인
			}

			long endTime = System.currentTimeMillis();

			logger.debug("get duration (millis): {}", (endTime - startTime));
		}

		@AfterAll
		static void tearDown() throws IOException {
			webServer.stop();
		}

	}

	@DisplayName("싱글 스레드 상황에서")
	@Nested
	class SingleThread {

		private static final int PORT = 8081;
		private static final int THREAD_POOL_SIZE = 1;
		private static final String URL = "http://localhost:" + PORT + "/";

		private static WebServer webServer;

		@BeforeAll
		static void setUp() throws IOException {
			webServer = new WebServer(PORT, BACKLOG, THREAD_POOL_SIZE);
			new Thread(() -> {
				try {
					webServer.start();
				} catch (IOException ignored) {
				}
			}).start();
		}

		@Test
		void HTTP_요청을_보내고_HTTP_응답을_받는다() throws IOException, InterruptedException {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, response.statusCode());
		}

		@Test
		void 동시에_여러_HTTP_요청을_보내고_응답을_받을_수_있다() throws InterruptedException, ExecutionException {
			List<CompletableFuture<Void>> futures = new ArrayList<>();

			for (int i = 0; i < requests; i++) {
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					HttpClient httpClient = HttpClient.newHttpClient();
					HttpRequest httpRequest = HttpRequest.newBuilder()
						.uri(URI.create(URL))
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

			for (CompletableFuture<Void> future : futures) {
				future.get(); // 각 태스크의 완료를 기다리고 예외가 발생하지 않았는지 확인
			}

			long endTime = System.currentTimeMillis();

			logger.debug("get duration (millis): {}", (endTime - startTime));
		}

		@AfterAll
		static void tearDown() throws IOException {
			webServer.stop();
		}

	}

}

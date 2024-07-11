package codesquad.session;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

	@Test
	public void 세션_생성_및_검색_테스트() {
		String sessionId = SessionManager.putSession("user", "testUser");
		assertNotNull(sessionId);

		Object value = SessionManager.getSession("user");
		assertEquals("testUser", value);
	}

	@Test
	public void 세션_만료_테스트() throws InterruptedException {
		SessionManager.setSessionTimeoutSecond(1); // 세션 타임아웃을 2초로 설정
		SessionManager.setThreadLocalSID(null); // 활성화된 세션이 없도록 설정

		String sessionId = SessionManager.putSession("user", "testUser");
		assertNotNull(sessionId);

		// 세션 타임아웃 세팅 복구
		SessionManager.setSessionTimeoutSecond(30 * 60);

		// 세션 만료 시뮬레이션
		Thread.sleep(1500); // 3초 동안 대기

		Object value = SessionManager.getSession("user");
		assertNull(value);
	}

	@Test
	public void 스레드_로컬_세션_관리_테스트() throws InterruptedException {
		int numThreads = 5;
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		CountDownLatch latch = new CountDownLatch(numThreads);

		for (int i = 0; i < numThreads; i++) {
			final int threadIndex = i;
			executorService.submit(() -> {
				try {
					SessionManager.setThreadLocalSID(null); // 활성화된 세션이 없도록 설정
					String sessionId = SessionManager.putSession("user", "testUser" + threadIndex);
					assertNotNull(sessionId);

					Object value = SessionManager.getSession("user");
					assertEquals("testUser" + threadIndex, value);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();
	}

	@Test
	public void 세션_속성_업데이트_테스트() {
		SessionManager.setThreadLocalSID(null); // 활성화된 세션이 없도록 설정

		String sessionId = SessionManager.putSession("user", "testUser");
		assertNotNull(sessionId);

		SessionManager.putSession("role", "admin");

		assertEquals("testUser", SessionManager.getSession("user"));
		assertEquals("admin", SessionManager.getSession("role"));
	}
}

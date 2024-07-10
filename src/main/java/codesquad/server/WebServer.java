package codesquad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

	private final Logger logger = LoggerFactory.getLogger(WebServer.class);

	private final int PORT;
	private final int BACKLOG;
	private final int THREAD_POOL_SIZE;

	private boolean running;

	public WebServer(int port, int backlog, int threadPoolSize) {
		this.PORT = port;
		this.BACKLOG = backlog;
		this.THREAD_POOL_SIZE = threadPoolSize;
	}

	public void start() {
		try {
			running = true;
			ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
			logger.info("Starting web server on port {}", PORT);
			try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG)) {
				while (running) {
					threadPool.submit(new WebWorker(serverSocket.accept()));
				}
			}
			threadPool.shutdown();
		} catch (IOException e) {
			logger.error("서버가 비정상적으로 종료되었습니다.");
			logger.error(e.getMessage(), e);
		}
	}

	public void stop() {
		running = false;
	}
}

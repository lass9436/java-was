package codesquad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.container.Container;

public class WebServer {

	private final Logger logger = LoggerFactory.getLogger(WebServer.class);

	private final int PORT;
	private final int BACKLOG;
	private final int THREAD_POOL_SIZE;

	private final WebWorker webWorker = Container.getInstance().getWebWorker();

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
					threadPool.submit(() -> {
						try (Socket clientSocket = serverSocket.accept()){
							webWorker.process(clientSocket);
						} catch (IOException e) {
							logger.error("소켓 연결에 실패했습니다. ", e);
						}
					});
				}
			}
			threadPool.shutdown();
		} catch (IOException e) {
			logger.error("서버가 비정상적으로 종료되었습니다. ", e);
		}
	}

	public void stop() {
		running = false;
	}
}

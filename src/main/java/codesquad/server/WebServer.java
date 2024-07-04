package codesquad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import codesquad.http.HttpProcessor;

public class WebServer {

	private final int PORT;
	private final int BACKLOG;
	private final int THREAD_POOL_SIZE;

	private boolean running;

	public WebServer(int port, int backlog, int threadPoolSize) throws IOException {
		this.PORT = port;
		this.BACKLOG = backlog;
		this.THREAD_POOL_SIZE = threadPoolSize;
	}

	public void start() throws IOException {
		running = true;
		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		try(ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG)){
			while (running) {
				threadPool.submit(new HttpProcessor(serverSocket.accept()));
			}
		}
		threadPool.shutdown();
	}

	public void stop() {
		running = false;
	}
}

package codesquad.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.runnable.MyRunnable;
import codesquad.socket.MySocket;

public class MyServer {

	private static final MyServer instance = new MyServer();
	private static final Logger logger = LoggerFactory.getLogger(MyServer.class);
	private static final int THREAD_POOL_SIZE = 10;
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	private volatile boolean running = true;

	private final int PORT = 8080;
	private MySocket mySocket;

	private MyServer() {
		try {
			mySocket = new MySocket(PORT);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static void start() throws IOException {
		while (instance.running) {
			threadPool.submit(new MyRunnable(instance.mySocket.accept()));
		}
	}

	public static void stop() throws IOException {
		instance.running = false;
		instance.mySocket.close();
		threadPool.shutdown();
	}
}

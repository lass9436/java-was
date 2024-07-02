package codesquad.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.runnable.MyRunnable;
import codesquad.socket.MySocket;

public class MyServerWithSingleThread {

	private static final MyServerWithSingleThread instance = new MyServerWithSingleThread();
	private static final Logger logger = LoggerFactory.getLogger(MyServerWithSingleThread.class);

	private volatile boolean running = true;

	private final int PORT = 8081;
	private MySocket mySocket;

	private MyServerWithSingleThread() {
		try {
			mySocket = new MySocket(PORT, 5);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static void start() throws IOException {
		while (instance.running) {
			new MyRunnable(instance.mySocket.accept()).run();
		}
	}

	public static void stop() throws IOException {
		instance.running = false;
		instance.mySocket.close();
	}
}

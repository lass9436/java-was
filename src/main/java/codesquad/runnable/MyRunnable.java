package codesquad.runnable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.httpHandler.HttpHandler;
import codesquad.httpRequest.HttpRequest;
import codesquad.httpRequest.HttpRequestParser;
import codesquad.httpResponse.HttpResponse;

public class MyRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MyRunnable.class);

	private final Socket socket;

	private final HttpHandler httpHandler = new HttpHandler();

	public MyRunnable(Socket clientSocket) {
		socket = clientSocket;
	}

	@Override
	public void run() {
		try (OutputStream clientOutput = socket.getOutputStream();
			 BufferedReader clientInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 BufferedReader fileReader = new BufferedReader(new FileReader("src/main/resources/static/index.html"));) {
			// 클라이언트 연결 로그 출력
			logger.info("Client connected");

			// HTTP Request
			HttpRequest httpRequest = HttpRequestParser.parse(clientInputReader);
			logger.debug(httpRequest.toString());

			// HTTP Response
			HttpResponse httpResponse = httpHandler.handle(httpRequest);
			clientOutput.write(httpResponse.toString().getBytes());

			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}

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

import codesquad.httpRequest.HttpRequestParser;

public class MyRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MyRunnable.class);

	private final Socket socket;

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

			// HTTP Request 로그 debug 출력
			logger.debug(HttpRequestParser.parse(clientInputReader).toString());

			// HTTP Response Status
			clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
			// Content-Type header
			clientOutput.write("Content-Type: text/html\r\n".getBytes());
			clientOutput.write("\r\n".getBytes());
			// Response Body
			String line = null;
			while ((line = fileReader.readLine()) != null) {
				clientOutput.write(line.getBytes());
				clientOutput.write("\r\n".getBytes());
			}

			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}

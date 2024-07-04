package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.exception.HttpStatusException;
import codesquad.http.handler.HttpDynamicHandler;
import codesquad.http.handler.HttpStaticHandler;
import codesquad.http.parser.HttpRequestParser;

public class HttpProcessor implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(HttpProcessor.class);

	private final Socket socket;

	private final HttpDynamicHandler httpDynamicHandler = new HttpDynamicHandler();
	private final HttpStaticHandler httpStaticHandler = new HttpStaticHandler();

	public HttpProcessor(Socket clientSocket) {
		socket = clientSocket;
	}

	@Override
	public void run() {
		try (OutputStream clientOutput = socket.getOutputStream();
			 BufferedReader clientInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			// 클라이언트 연결 로그 출력
			logger.info("Client connected");

			try {
				// HTTP Request
				HttpRequest httpRequest = HttpRequestParser.parse(clientInputReader);
				logger.debug(httpRequest.toString());

				// HTTP Response
				// Dynamic
				HttpResponse httpResponse = httpDynamicHandler.handle(httpRequest);
				// Static
				if (httpResponse == null) {
					httpResponse = httpStaticHandler.handle(httpRequest);
				}
				clientOutput.write(httpResponse.getBytes());

			} catch (HttpStatusException e) {
				logger.error("HTTP 상태 코드 예외 발생: " + e.getStatus().getCode() + " " + e.getMessage());
				HttpResponse errorResponse = new HttpResponse("HTTP/1.1", e.getStatus(), Map.of(),
					e.getStatus().getReasonPhrase().getBytes());
				clientOutput.write(errorResponse.getBytes());
			}

			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error("클라이언트 소켓의 write 또는 flush 에 실패했습니다.");
			logger.error(e.getMessage());
		}
	}
}

package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.mapper.HttpDynamicHandlerMapper;
import codesquad.http.mapper.HttpStaticHandlerMapper;
import codesquad.http.parser.HttpRequestParser;
import codesquad.http.status.HttpStatusException;

public class HttpProcessor implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(HttpProcessor.class);

	private final Socket socket;

	private final HttpDynamicHandlerMapper httpDynamicHandlerMapper = new HttpDynamicHandlerMapper();
	private final HttpStaticHandlerMapper httpStaticHandlerMapper = new HttpStaticHandlerMapper();

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
				HttpResponse httpResponse = httpDynamicHandlerMapper.handle(httpRequest);
				// Static
				if (httpResponse == null) {
					httpResponse = httpStaticHandlerMapper.handle(httpRequest);
				}
				clientOutput.write(httpResponse.getBytes());

			} catch (HttpStatusException e) {
				logger.error("HTTP 상태 코드 예외 발생: { }", e);
				HttpResponse errorResponse = new HttpResponse(HttpVersion.HTTP_1_1, e.getStatus(), Map.of(),
					e.getStatus().getReasonPhrase().getBytes());
				clientOutput.write(errorResponse.getBytes());
			}
			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error("클라이언트 소켓의 예외 발생: { }", e);
		}
	}
}

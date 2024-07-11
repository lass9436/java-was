package codesquad.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.container.Container;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.mapper.HttpDynamicHandlerMapper;
import codesquad.http.mapper.HttpStaticHandlerMapper;
import codesquad.http.parser.HttpRequestParser;
import codesquad.http.status.HttpStatusException;

public class WebWorker implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(WebWorker.class);

	private final Socket socket;
	private final Container container;
	private final HttpDynamicHandlerMapper httpDynamicHandlerMapper;
	private final HttpStaticHandlerMapper httpStaticHandlerMapper;

	public WebWorker(Socket clientSocket) {
		socket = clientSocket;
		container = Container.getInstance();
		httpDynamicHandlerMapper = container.getHttpDynamicHandlerMapper();
		httpStaticHandlerMapper = container.getHttpStaticHandlerMapper();
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
				logger.error("HTTP 상태 코드 예외 발생: ", e);
				HttpResponse errorResponse = new HttpResponse(HttpVersion.HTTP_1_1, e.getStatus(), Map.of(),
					e.getStatus().getReasonPhrase().getBytes());
				clientOutput.write(errorResponse.getBytes());
			}
			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error("클라이언트 소켓의 예외 발생: ", e);
		}
	}
}
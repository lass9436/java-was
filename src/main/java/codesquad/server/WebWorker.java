package codesquad.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.constants.HttpMethod;
import codesquad.dto.HttpRequest;
import codesquad.dto.HttpResponse;
import codesquad.http.filter.FilterChain;
import codesquad.http.mapper.HttpDynamicHandlerMapper;
import codesquad.http.mapper.HttpErrorHandlerMapper;
import codesquad.http.mapper.HttpStaticHandlerMapper;
import codesquad.http.parser.HttpRequestParser;
import codesquad.http.render.RenderData;
import codesquad.http.render.RenderEngine;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;
import codesquad.session.SessionManager;

public class WebWorker {

	private static final Logger logger = LoggerFactory.getLogger(WebWorker.class);

	public static final ThreadLocal<HttpRequest> HTTP_REQUEST_THREAD_LOCAL = new ThreadLocal<>();
	public static final ThreadLocal<HttpResponse> HTTP_RESPONSE_THREAD_LOCAL = new ThreadLocal<>();

	private final FilterChain filterChain;
	private final HttpDynamicHandlerMapper httpDynamicHandlerMapper;
	private final HttpStaticHandlerMapper httpStaticHandlerMapper;
	private final HttpErrorHandlerMapper httpErrorHandlerMapper;
	private final RenderEngine renderEngine;

	public WebWorker(FilterChain filterChain, HttpDynamicHandlerMapper httpDynamicHandlerMapper,
		HttpStaticHandlerMapper httpStaticHandlerMapper,
		HttpErrorHandlerMapper httpErrorHandlerMapper,
		RenderEngine renderEngine
	) {
		this.filterChain = filterChain;
		this.httpDynamicHandlerMapper = httpDynamicHandlerMapper;
		this.httpStaticHandlerMapper = httpStaticHandlerMapper;
		this.httpErrorHandlerMapper = httpErrorHandlerMapper;
		this.renderEngine = renderEngine;
	}

	public void process(Socket socket) {
		try (OutputStream clientOutput = socket.getOutputStream();
			 InputStream clientInput = socket.getInputStream()) {
			// 클라이언트 연결 로그 출력
			//logger.info("Client connected");
			HTTP_REQUEST_THREAD_LOCAL.set(new HttpRequest());
			HTTP_RESPONSE_THREAD_LOCAL.set(new HttpResponse());
			try {
				// HTTP Request Parse
				HTTP_REQUEST_THREAD_LOCAL.set(HttpRequestParser.parse(clientInput));
				HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
				logger.info("{} {}", httpRequest.getMethod(), httpRequest.getPath());

				// Filter Chain
				filterChain.doFilter();

				// find handler
				Function<Void, RenderData> handler = httpDynamicHandlerMapper.findHandler();

				// 405 Method Not Allowed 처리
				if (handler == null && httpRequest.getMethod() != HttpMethod.GET) {
					throw new HttpStatusException(HttpStatus.METHOD_NOT_ALLOWED, "지원되지 않는 HTTP 메서드입니다");
				}

				// static handle
				if (handler == null) {
					HTTP_RESPONSE_THREAD_LOCAL.set(httpStaticHandlerMapper.handle());
				} else {
					// dynamic handle
					RenderData renderData = handler.apply(null);
					// render
					if (renderData != null) {
						renderEngine.render(renderData);
					}
				}
				clientOutput.write(HTTP_RESPONSE_THREAD_LOCAL.get().getBytes());

			} catch (HttpStatusException e) {
				logger.error("HTTP 상태 코드 예외 발생: ", e);
				HttpResponse httpResponse = httpErrorHandlerMapper.handle(e);
				clientOutput.write(httpResponse.getBytes());
			}
			// write flush
			clientOutput.flush();
		} catch (IOException e) {
			logger.error("클라이언트 소켓의 예외 발생: ", e);
		} catch (Exception e) {
			logger.error("알 수 없는 에러 ", e);
		} finally {
			SessionManager.removeThreadLocalSID();
			HTTP_REQUEST_THREAD_LOCAL.remove();
			HTTP_RESPONSE_THREAD_LOCAL.remove();
		}
	}
}

package codesquad.http.mapper;

import static codesquad.server.WebWorker.*;

import java.util.Map;
import java.util.function.Function;

import codesquad.http.constants.HttpHandleType;
import codesquad.http.dto.HttpEndPoint;
import codesquad.http.dto.HttpRequest;
import codesquad.http.registry.HttpHandlerRegistry;
import codesquad.http.render.RenderData;

public class HttpDynamicHandlerMapper {

	private final Map<HttpEndPoint, Function<Void, RenderData>> handlers;

	public HttpDynamicHandlerMapper(HttpHandlerRegistry registry) {
		this.handlers = registry.getHandlers(HttpHandleType.DYNAMIC);
	}

	public Function<Void, RenderData> findHandler() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		Function<Void, RenderData> handler = handlers.get(
			new HttpEndPoint(httpRequest.getPath(), httpRequest.getMethod()));
		return handler;
	}
}

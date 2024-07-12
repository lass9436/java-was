package codesquad.http.mapper;

import java.util.Map;
import java.util.function.Function;

import codesquad.http.constants.HttpHandleType;
import codesquad.http.dto.HttpEndPoint;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.registry.HttpHandlerRegistry;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class HttpDynamicHandlerMapper {

	private final Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> handlers;
	private final HttpHandleType type = HttpHandleType.DYNAMIC;

	public HttpDynamicHandlerMapper(HttpHandlerRegistry registry) {
		handlers = registry.getHandlers(type);
	}

	public HttpResponse handle(HttpRequest httpRequest) {
		Function<HttpRequest, HttpResponse> handler = handlers.get(
			new HttpEndPoint(httpRequest.path(), httpRequest.method()));
		try {
			return handler != null ? handler.apply(httpRequest) : null;
		} catch (Exception e) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

}

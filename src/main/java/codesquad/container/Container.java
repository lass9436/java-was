package codesquad.container;

import codesquad.http.mapper.HttpDynamicHandlerMapper;
import codesquad.http.mapper.HttpStaticHandlerMapper;
import codesquad.http.registry.HttpHandlerRegistry;
import codesquad.server.WebWorker;

public class Container {

	private static Container instance;

	private final HttpStaticHandlerMapper httpStaticHandlerMapper;
	private final HttpDynamicHandlerMapper httpDynamicHandlerMapper;
	private final HttpHandlerRegistry httpHandlerRegistry;
	private final WebWorker webWorker;

	private Container() {
		this.httpHandlerRegistry = new HttpHandlerRegistry();
		this.httpDynamicHandlerMapper = new HttpDynamicHandlerMapper(httpHandlerRegistry);
		this.httpStaticHandlerMapper = new HttpStaticHandlerMapper();
		this.webWorker = new WebWorker(httpDynamicHandlerMapper, httpStaticHandlerMapper);
	}

	public static Container getInstance() {
		if (instance == null) {
			instance = new Container();
		}
		return instance;
	}

	public HttpStaticHandlerMapper getHttpStaticHandlerMapper() {
		return httpStaticHandlerMapper;
	}

	public HttpDynamicHandlerMapper getHttpDynamicHandlerMapper() {
		return httpDynamicHandlerMapper;
	}

	public HttpHandlerRegistry getHttpHandlerRegistry() { return httpHandlerRegistry; }

	public WebWorker getWebWorker() { return webWorker; }
}

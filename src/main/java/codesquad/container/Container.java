package codesquad.container;

import codesquad.http.filter.FilterChain;
import codesquad.http.mapper.HttpDynamicHandlerMapper;
import codesquad.http.mapper.HttpErrorHandlerMapper;
import codesquad.http.mapper.HttpStaticHandlerMapper;
import codesquad.http.registry.HttpHandlerRegistry;
import codesquad.http.render.RenderEngine;
import codesquad.server.WebWorker;

public class Container {

	private static Container instance;

	private final FilterChain filterChain;
	private final HttpStaticHandlerMapper httpStaticHandlerMapper;
	private final HttpDynamicHandlerMapper httpDynamicHandlerMapper;
	private final HttpHandlerRegistry httpHandlerRegistry;
	private final HttpErrorHandlerMapper httpErrorHandlerMapper;
	private final RenderEngine renderEngine;
	private final WebWorker webWorker;

	private Container() {
		this.filterChain = new FilterChain();
		this.httpHandlerRegistry = new HttpHandlerRegistry();
		this.httpDynamicHandlerMapper = new HttpDynamicHandlerMapper(httpHandlerRegistry);
		this.httpStaticHandlerMapper = new HttpStaticHandlerMapper();
		this.httpErrorHandlerMapper = new HttpErrorHandlerMapper();
		this.renderEngine = new RenderEngine();
		this.webWorker = new WebWorker(filterChain, httpDynamicHandlerMapper, httpStaticHandlerMapper,
			httpErrorHandlerMapper, renderEngine);
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

	public HttpHandlerRegistry getHttpHandlerRegistry() {
		return httpHandlerRegistry;
	}

	public WebWorker getWebWorker() {
		return webWorker;
	}
}

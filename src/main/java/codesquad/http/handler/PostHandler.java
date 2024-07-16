package codesquad.http.handler;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.render.RenderData;

@HttpHandler
public class PostHandler {

	@HttpFunction(path = "/post/write", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handleUserWrite() {
		RenderData renderData = new RenderData("/post/write");
		return renderData;
	}
}

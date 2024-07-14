package codesquad.http.handler;

import static codesquad.server.WebWorker.*;
import static codesquad.session.SessionManager.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.annotation.HttpFunction;
import codesquad.http.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.dto.HttpRequest;
import codesquad.http.render.RenderData;
import codesquad.model.User;

@HttpHandler
public class IndexHandler {

	private final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

	@HttpFunction(path = "/", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handleIndex() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();

		// 세션에서 User 인스턴스 획득
		User user = (User)getSession("user");
		Map<String, Object> model = new HashMap<>();
		model.put("user", user);

		// RenderData 객체를 생성하여 뷰 이름과 모델을 설정합니다.
		RenderData renderData = new RenderData("/index");
		renderData.getModel().putAll(model);

		return renderData;
	}
}

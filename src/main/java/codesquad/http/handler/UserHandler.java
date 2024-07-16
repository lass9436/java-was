package codesquad.http.handler;

import static codesquad.session.SessionManager.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.render.RenderData;
import codesquad.model.Post;
import codesquad.model.PostRepository;
import codesquad.model.User;
import codesquad.model.UserRepository;

@HttpHandler
public class UserHandler {

	private final Logger logger = LoggerFactory.getLogger(UserHandler.class);

	private final UserRepository userRepository;
	private final PostRepository postRepository;

	public UserHandler(UserRepository userRepository, PostRepository postRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	@HttpFunction(path = "/", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handleIndex() {
		// 세션에서 User 인스턴스 획득
		User user = (User)getSession("user");
		Map<String, Object> model = new HashMap<>();
		model.put("user", user);

		// 글 목록 획득
		List<Post> posts = postRepository.findAll();
		model.put("posts", posts);

		// RenderData 객체를 생성하여 뷰 이름과 모델을 설정합니다.
		RenderData renderData = new RenderData("/index");
		renderData.getModel().putAll(model);

		return renderData;
	}

	@HttpFunction(path = "/user/list", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handleUserList() {
		// 자기 자신 조회
		User user = (User)getSession("user");

		// 전체 유저 목록 조회
		List<User> users = userRepository.findAll();

		// 모델
		Map<String, Object> model = new HashMap<>();
		model.put("users", users);
		model.put("user", user);

		// 렌더 데이터
		RenderData renderData = new RenderData("/user/list");
		renderData.getModel().putAll(model);

		return renderData;
	}

}

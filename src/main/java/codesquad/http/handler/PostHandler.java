package codesquad.http.handler;

import static codesquad.server.WebWorker.*;
import static codesquad.session.SessionManager.*;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.dto.HttpRequest;
import codesquad.http.render.RenderData;
import codesquad.model.Post;
import codesquad.model.PostRepository;
import codesquad.model.User;

@HttpHandler
public class PostHandler {

	private final PostRepository postRepository;

	public PostHandler(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@HttpFunction(path = "/post/write", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handleUserWrite() {
		User user = (User)getSession("user");
		RenderData renderData = new RenderData("/post/write");
		renderData.addAttribute("user", user);
		return renderData;
	}

	@HttpFunction(path = "/post/detail", method = HttpMethod.GET, type = HttpHandleType.DYNAMIC)
	public RenderData handlePostDetail() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		User user = (User)getSession("user");
		RenderData renderData = new RenderData("/post/detail");
		renderData.addAttribute("user", user);
		int postId = Integer.parseInt(httpRequest.getParameters().get("postId").get(0));
		Post post = postRepository.findById(postId);
		renderData.addAttribute("post", post);
		return renderData;
	}
}

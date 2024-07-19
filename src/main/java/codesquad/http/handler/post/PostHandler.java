package codesquad.http.handler.post;

import static codesquad.server.WebWorker.*;
import static codesquad.session.SessionManager.*;

import java.util.List;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.dto.HttpRequest;
import codesquad.http.render.RenderData;
import codesquad.model.comment.Comment;
import codesquad.model.comment.CommentRepository;
import codesquad.model.post.Post;
import codesquad.model.post.PostRepository;
import codesquad.model.user.User;

@HttpHandler
public class PostHandler {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public PostHandler(PostRepository postRepository, CommentRepository commentRepository) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
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
		if (post == null) {
			throw new IllegalArgumentException("존재하지 않는 글입니다.");
		}
		renderData.addAttribute("post", post);
		List<Comment> comments = commentRepository.findByPostId(postId);
		renderData.addAttribute("comments", comments);
		return renderData;
	}
}

package codesquad.http.handler.comment;

import static codesquad.server.WebWorker.*;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;
import codesquad.http.render.RenderData;
import codesquad.http.status.HttpStatus;
import codesquad.model.comment.Comment;
import codesquad.model.comment.CommentRepository;
import codesquad.model.user.User;
import codesquad.session.SessionManager;

@HttpHandler
public class CommentApiHandler {

	private final Logger logger = LoggerFactory.getLogger(CommentApiHandler.class);
	private final CommentRepository commentRepository;

	public CommentApiHandler(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	@HttpFunction(path = "/comment/write", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public RenderData createComment() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		User user = (User) SessionManager.getSession("user");
		if (user == null) {
			httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.UNAUTHORIZED,
				Map.of("Location", List.of("/login")), new byte[0]);
			return null;
		}

		Map<String, List<Object>> body = httpRequest.getBody();
		String content = (String)body.get("content").get(0);
		int postId = Integer.parseInt((String)body.get("postId").get(0));
		Comment comment = new Comment(postId, user.getUserId(), content);
		commentRepository.create(comment);

		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND,
			Map.of("Location", List.of("/post/detail?postId=" + postId)), new byte[0]);

		logger.info("Comment created: {}", comment);

		return null;
	}
}

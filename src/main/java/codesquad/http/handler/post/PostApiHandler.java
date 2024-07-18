package codesquad.http.handler.post;

import static codesquad.server.WebWorker.*;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.dto.FileData;
import codesquad.dto.HttpRequest;
import codesquad.dto.HttpResponse;
import codesquad.file.FileUtil;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.render.RenderData;
import codesquad.http.status.HttpStatus;
import codesquad.model.post.Post;
import codesquad.model.post.PostRepository;
import codesquad.model.user.User;
import codesquad.session.SessionManager;

@HttpHandler
public class PostApiHandler {

	private final Logger logger = LoggerFactory.getLogger(PostApiHandler.class);
	private final PostRepository postRepository;

	public PostApiHandler(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@HttpFunction(path = "/post/write", method = HttpMethod.POST, type = HttpHandleType.DYNAMIC)
	public RenderData createPost() {
		HttpRequest httpRequest = HTTP_REQUEST_THREAD_LOCAL.get();
		HttpResponse httpResponse = HTTP_RESPONSE_THREAD_LOCAL.get();

		User user = (User)SessionManager.getSession("user");
		if (user == null) {
			httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.UNAUTHORIZED,
				Map.of("Location", List.of("/login")), new byte[0]);
			return null;
		}

		Map<String, List<Object>> body = httpRequest.getBody();
		String title = (String)body.get("title").get(0);
		String content = (String)body.get("content").get(0);

		// 파일 업로드 처리
		FileData fileData = (FileData)body.get("file").get(0);
		String newFileName = FileUtil.saveFile(fileData);

		Post post = new Post(user.getUserId(), title, content, newFileName);
		postRepository.create(post);

		httpResponse.setResponse(HttpVersion.HTTP_1_1, HttpStatus.FOUND,
			Map.of("Location", List.of("/")), new byte[0]);

		logger.info("Post created: {}", post);

		return null;
	}
}
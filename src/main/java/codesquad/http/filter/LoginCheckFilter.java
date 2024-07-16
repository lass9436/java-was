package codesquad.http.filter;

import static codesquad.server.WebWorker.*;

import java.util.List;
import java.util.Map;

import codesquad.http.dto.HttpRequest;
import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;
import codesquad.model.user.User;
import codesquad.session.SessionManager;

public class LoginCheckFilter implements Filter {

	@Override
	public void doFilter() {
		HttpRequest request = HTTP_REQUEST_THREAD_LOCAL.get();
		User user = (User)SessionManager.getSession("user");
		if (user == null && "/user/list".equals(request.getPath())) {
			throw new HttpStatusException(HttpStatus.FOUND, "로그인한 사용자만 접근할 수 있습니다.",
				(Map.of("Location", List.of("/"))));
		}

		if (user == null && "/post/write".equals(request.getPath())) {
			throw new HttpStatusException(HttpStatus.FOUND, "로그인한 사용자만 접근할 수 있습니다.",
				(Map.of("Location", List.of("/login"))));
		}
	}
}

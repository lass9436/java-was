package codesquad.http.render;

import static codesquad.server.WebWorker.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class RenderEngine {

	private static final String TEMPLATE_ROOT_PATH = "templates";
	private static final String TEMPLATE_SUFFIX = ".html";

	private static final Logger logger = LoggerFactory.getLogger(RenderEngine.class);

	public void render(RenderData renderData) {
		String viewName = renderData.getViewName() + TEMPLATE_SUFFIX;
		Map<String, Object> model = renderData.getModel();

		// 템플릿 엔진을 사용하여 뷰를 렌더링하는 예제 코드
		String renderedContent = renderTemplate(viewName, model);
		HTTP_RESPONSE_THREAD_LOCAL.get().setBody(renderedContent.getBytes());
	}

	private String renderTemplate(String viewName, Map<String, Object> model) {
		// 템플릿 파일 로드
		String templateContent = loadTemplate(viewName);

		// {{ key.property }} 패턴을 찾기 위한 정규 표현식
		Pattern pattern = Pattern.compile("\\{\\{\\s*(\\w+)\\.(\\w+)\\s*\\}\\}");
		Matcher matcher = pattern.matcher(templateContent);

		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String modelKey = matcher.group(1);
			String property = matcher.group(2);

			Object modelObject = model.get(modelKey);
			if (modelObject != null) {
				try {
					Method getterMethod = modelObject.getClass().getMethod("get" + capitalize(property));
					Object value = getterMethod.invoke(modelObject);
					matcher.appendReplacement(sb, value != null ? value.toString() : "");
				} catch (Exception e) {
					e.printStackTrace();
					matcher.appendReplacement(sb, ""); // 에러 발생 시 빈 문자열로 대체
				}
			} else {
				matcher.appendReplacement(sb, ""); // 모델 객체가 없을 경우 빈 문자열로 대체
			}
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private String loadTemplate(String viewName) {
		String resourcePath = TEMPLATE_ROOT_PATH + viewName;
		logger.info("Loading template: {}", resourcePath);
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			if (inputStream == null) {
				throw new HttpStatusException(HttpStatus.NOT_FOUND, "템플릿 파일 없음: " + viewName);
			}
			StringBuilder templateContent = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				templateContent.append(line).append("\n");
			}
			return templateContent.toString();
		} catch (IOException e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "템플릿 파일 로드 실패", e);
		}
	}

	private String capitalize(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
}

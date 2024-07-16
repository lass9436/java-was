package codesquad.http.render;

import static codesquad.server.WebWorker.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collection;
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
		// 템플릿 콘텐츠 위치
		String viewName = renderData.getViewName();
		// 렌더링에 쓰이는 모델
		Map<String, Object> model = renderData.getModel();

		// 템플릿 콘텐츠 로드
		String templateContent = loadTemplate(viewName);
		// 템플릿 콘텐츠레 렌더링
		String renderedContent = renderTemplate(templateContent, model);

		// 렌더링 결과를 응답 바디에 저장
		HTTP_RESPONSE_THREAD_LOCAL.get().setBody(renderedContent.getBytes());
	}

	private String loadTemplate(String viewName) {
		String resourcePath = TEMPLATE_ROOT_PATH + viewName + TEMPLATE_SUFFIX;
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

	private String renderTemplate(String templateContent, Map<String, Object> model) {
		// if 문 패턴 처리
		templateContent = processIfStatements(templateContent, model);

		// for 문 패턴 처리
		templateContent = processForStatements(templateContent, model);

		// placeHolder 문 패턴 처리
		templateContent = processPlaceHolder(templateContent, model);

		return templateContent;
	}

	private String processIfStatements(String templateContent, Map<String, Object> model) {
		Pattern ifPattern = Pattern.compile("\\{%\\s*if\\s+(!?\\w+(?:\\.\\w+)?)\\s*%\\}(.*?)\\{%\\s*endif\\s*%\\}",
			Pattern.DOTALL);
		Matcher matcher = ifPattern.matcher(templateContent);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String condition = matcher.group(1);
			String content = matcher.group(2);

			boolean negate = false;
			if (condition.startsWith("!")) {
				negate = true;
				condition = condition.substring(1);
			}

			String[] parts = condition.split("\\.");
			String modelKey = parts[0];
			String property = parts.length > 1 ? parts[1] : null;

			Object modelObject = model.get(modelKey);
			boolean conditionMet = evaluateCondition(modelObject, property);

			if (negate) {
				conditionMet = !conditionMet;
			}

			if (conditionMet) {
				matcher.appendReplacement(sb, content);
			} else {
				matcher.appendReplacement(sb, "");
			}
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private boolean evaluateCondition(Object modelObject, String property) {
		if (modelObject == null) {
			return false;
		}

		if (property == null) {
			return true;
		}

		try {
			Method getterMethod = modelObject.getClass().getMethod("get" + capitalize(property));
			Object value = getterMethod.invoke(modelObject);

			if (value == null) {
				return false;
			}

			if (value instanceof Boolean) {
				return (Boolean)value;
			}

			if (value instanceof Integer) {
				return (Integer)value != 0;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String processForStatements(String templateContent, Map<String, Object> model) {
		Pattern forPattern = Pattern.compile("\\{%\\s*for\\s+(\\w+)\\s+in\\s+(\\w+)\\s*%\\}(.*?)\\{%\\s*endfor\\s*%\\}",
			Pattern.DOTALL);
		Matcher matcher = forPattern.matcher(templateContent);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String itemName = matcher.group(1);
			String collectionName = matcher.group(2);
			String content = matcher.group(3);

			Object collectionObject = model.get(collectionName);
			if (collectionObject instanceof Collection) {
				Collection<?> collection = (Collection<?>)collectionObject;
				StringBuffer repeatedContent = new StringBuffer();
				for (Object item : collection) {
					Map<String, Object> itemModel = Map.of(itemName, item);
					repeatedContent.append(processPlaceHolder(content, itemModel));
				}
				matcher.appendReplacement(sb, repeatedContent.toString());
			} else {
				matcher.appendReplacement(sb, "");
			}
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private String processPlaceHolder(String templateContent, Map<String, Object> model) {
		// StringBuffer 초기화
		StringBuffer sb = new StringBuffer();

		// {{ key.property }} 패턴을 찾기 위한 정규 표현식
		Pattern pattern = Pattern.compile("\\{\\{\\s*(\\w+)\\.(\\w+)\\s*\\}\\}");
		Matcher matcher = pattern.matcher(templateContent);

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
}

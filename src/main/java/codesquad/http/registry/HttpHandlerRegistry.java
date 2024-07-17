package codesquad.http.registry;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.HttpFunction;
import codesquad.annotation.HttpHandler;
import codesquad.container.ComponentScanner;
import codesquad.container.DependencyFactory;
import codesquad.http.constants.HttpHandleType;
import codesquad.dto.HttpEndPoint;
import codesquad.http.render.RenderData;

public class HttpHandlerRegistry {

	private final Logger logger = LoggerFactory.getLogger(HttpHandlerRegistry.class);

	private final String packageName = "codesquad.http.handler";

	private final ComponentScanner componentScanner = new ComponentScanner();
	private final DependencyFactory dependencyFactory = new DependencyFactory();

	public Map<HttpEndPoint, Function<Void, RenderData>> getHandlers(HttpHandleType type) {
		Map<HttpEndPoint, Function<Void, RenderData>> handlers = new HashMap<>();
		try {
			Class<?>[] classes = componentScanner.getClasses(packageName);
			Arrays.stream(classes)
				.filter(handlerClass -> handlerClass.isAnnotationPresent(HttpHandler.class))
				.forEach(handlerClass -> registerHandlerMethods(handlers, handlerClass, type));
		} catch (Exception e) {
			throw new RuntimeException("패키지에서 핸들러 클래스를 로드하지 못했습니다: " + packageName, e);
		}
		return handlers;
	}

	private void registerHandlerMethods(Map<HttpEndPoint, Function<Void, RenderData>> handlers,
		Class<?> handlerClass, HttpHandleType type) {
		try {
			Object handlerInstance = dependencyFactory.createHandlerInstance(handlerClass);
			Arrays.stream(handlerClass.getMethods())
				.filter(method -> method.isAnnotationPresent(HttpFunction.class))
				.forEach(method -> {
					HttpFunction httpFunction = method.getAnnotation(HttpFunction.class);
					if (httpFunction.type() == type) {
						HttpEndPoint endPoint = new HttpEndPoint(httpFunction.path(), httpFunction.method());
						Function<Void, RenderData> handlerFunction = createHandlerFunction(handlerInstance, method);
						handlers.put(endPoint, handlerFunction);
					}
				});
		} catch (Exception e) {
			throw new RuntimeException("클래스에 대한 핸들러 메서드를 등록하지 못했습니다: " + handlerClass.getName(), e);
		}
	}

	private Function<Void, RenderData> createHandlerFunction(Object handlerInstance, Method method) {
		return (unused) -> {
			try {
				return (RenderData)method.invoke(handlerInstance);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}

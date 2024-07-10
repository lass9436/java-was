package codesquad.http.registry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import codesquad.http.annotation.HttpFunction;
import codesquad.http.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.dto.HttpEndPoint;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;

public class HttpHandlerRegistry {

	private final String packageName = "codesquad.http.handler";

	public Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> getHandlers(HttpHandleType type) {
		Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> handlers = new HashMap<>();
		try {
			Class<?>[] classes = getClasses(packageName);
			Arrays.stream(classes)
				.filter(handlerClass -> handlerClass.isAnnotationPresent(HttpHandler.class))
				.forEach(handlerClass -> registerHandlerMethods(handlers, handlerClass, type));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load handler classes from package: " + packageName, e);
		}
		return handlers;
	}

	private void registerHandlerMethods(Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> handlers,
		Class<?> handlerClass, HttpHandleType type) {
		try {
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();
			Arrays.stream(handlerClass.getMethods())
				.filter(method -> method.isAnnotationPresent(HttpFunction.class))
				.forEach(method -> {
					HttpFunction httpFunction = method.getAnnotation(HttpFunction.class);
					if (httpFunction.type() == type) {
						HttpEndPoint endPoint = new HttpEndPoint(httpFunction.path(), httpFunction.method());
						Function<HttpRequest, HttpResponse> handlerFunction = createHandlerFunction(handlerInstance,
							method);
						handlers.put(endPoint, handlerFunction);
					}
				});
		} catch (Exception e) {
			throw new RuntimeException("Failed to register handler methods for class: " + handlerClass.getName(), e);
		}
	}

	private Function<HttpRequest, HttpResponse> createHandlerFunction(Object handlerInstance, Method method) {
		return (request) -> {
			try {
				return (HttpResponse)method.invoke(handlerInstance, request);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	private Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		String path = packageName.replace('.', '/');
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);
		if (resource == null) {
			throw new ClassNotFoundException("No resource for " + path);
		}

		File directory = new File(resource.getFile());
		if (!directory.exists()) {
			throw new ClassNotFoundException(
				packageName + " (" + directory.getPath() + ") does not appear to be a valid package");
		}

		File[] files = directory.listFiles();
		if (files == null) {
			throw new ClassNotFoundException("Failed to list files for package " + packageName);
		}

		return Arrays.stream(files)
			.filter(file -> file.getName().endsWith(".class"))
			.map(file -> {
				String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			})
			.toArray(Class<?>[]::new);
	}
}

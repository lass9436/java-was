package codesquad.http.registry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.http.annotation.HttpFunction;
import codesquad.http.annotation.HttpHandler;
import codesquad.http.constants.HttpHandleType;
import codesquad.http.dto.HttpEndPoint;
import codesquad.http.dto.HttpRequest;
import codesquad.http.dto.HttpResponse;

public class HttpHandlerRegistry {

	private final Logger logger = LoggerFactory.getLogger(HttpHandlerRegistry.class);

	private final String packageName = "codesquad.http.handler";

	public Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> getHandlers(HttpHandleType type) {
		Map<HttpEndPoint, Function<HttpRequest, HttpResponse>> handlers = new HashMap<>();
		try {
			Class<?>[] classes = getClasses(packageName);
			Arrays.stream(classes)
				.filter(handlerClass -> handlerClass.isAnnotationPresent(HttpHandler.class))
				.forEach(handlerClass -> registerHandlerMethods(handlers, handlerClass, type));
		} catch (Exception e) {
			throw new RuntimeException("패키지에서 핸들러 클래스를 로드하지 못했습니다: " + packageName, e);
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
			throw new RuntimeException("클래스에 대한 핸들러 메서드를 등록하지 못했습니다: " + handlerClass.getName(), e);
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
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(path);
		if (resource == null) {
			throw new ClassNotFoundException("리소스를 찾을 수 없습니다: " + path);
		}

		if (resource.getProtocol().equals("file")) {
			return getClassesFromFileSystem(packageName, resource);
		} else if (resource.getProtocol().equals("jar")) {
			return getClassesFromJarFile(packageName, resource);
		} else {
			throw new ClassNotFoundException("지원되지 않는 프로토콜입니다: " + resource.getProtocol());
		}
	}

	private Class<?>[] getClassesFromFileSystem(String packageName, URL resource) throws ClassNotFoundException {
		logger.info("파일 시스템을 사용합니다");
		File directory;
		try {
			directory = new File(resource.toURI());
		} catch (URISyntaxException e) {
			directory = new File(resource.getPath());
		}

		if (!directory.exists()) {
			throw new ClassNotFoundException(
				packageName + " (" + directory.getPath() + ") 는 유효한 패키지로 보이지 않습니다");
		}

		File[] files = directory.listFiles();
		if (files == null) {
			throw new ClassNotFoundException("패키지에 대한 파일 목록을 가져오지 못했습니다: " + packageName);
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

	private Class<?>[] getClassesFromJarFile(String packageName, URL resource) throws IOException {
		logger.info("JAR 시스템을 사용합니다");
		String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			return Collections.list(entries).stream()
				.filter(entry -> entry.getName().startsWith(packageName.replace('.', '/')))
				.filter(entry -> entry.getName().endsWith(".class"))
				.filter(entry -> !entry.isDirectory())
				.map(entry -> {
					String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
					try {
						return Class.forName(className);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				})
				.toArray(Class<?>[]::new);
		}
	}
}

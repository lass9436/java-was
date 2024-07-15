package codesquad.container;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.Primary;
import codesquad.annotation.RepositoryImpl;

public class DependencyFactory {

	private static final Logger logger = LoggerFactory.getLogger(DependencyFactory.class);
	private final Map<Class<?>, Object> dependencies = new HashMap<>();
	private final ComponentScanner componentScanner = new ComponentScanner();
	private static final String packageName = "codesquad.model";

	public DependencyFactory() {
		scanAndRegister(packageName);
	}

	public <T> T getDependency(Class<T> type) {
		return type.cast(dependencies.get(type));
	}

	public <T> void registerDependency(Class<T> type, T instance) {
		dependencies.put(type, instance);
	}

	// 지정된 패키지를 스캔하고 의존성을 등록하는 메서드
	public void scanAndRegister(String packageName) {
		try {
			for (Class<?> clazz : componentScanner.getClasses(packageName)) {
				// RepositoryImpl 어노테이션이 붙은 클래스 처리
				if (clazz.isAnnotationPresent(RepositoryImpl.class)) {
					// 클래스가 구현한 모든 인터페이스를 가져옴
					Class<?>[] interfaces = clazz.getInterfaces();
					if (interfaces.length > 0) {
						// 첫 번째 인터페이스를 사용하여 등록
						registerRepositoryImpl(interfaces[0], clazz);
					}
				}
			}
		} catch (Exception e) {
			logger.error("패키지에서 컴포넌트를 스캔하고 등록하는데 실패했습니다: {}", packageName, e);
			throw new RuntimeException("컴포넌트를 스캔하고 등록하는데 실패했습니다", e);
		}
	}

	private void registerRepositoryImpl(Class<?> repositoryInterface, Class<?> repositoryImplClass) throws Exception {
		// 구현체의 인스턴스를 생성합니다.
		Constructor<?> constructor = repositoryImplClass.getDeclaredConstructor();
		Object instance = constructor.newInstance();

		// 이미 해당 인터페이스에 대한 구현체가 등록되어 있는 경우
		if (dependencies.containsKey(repositoryInterface)) {
			// 새로운 구현체에 Primary 어노테이션이 붙어 있는 경우
			if (repositoryImplClass.isAnnotationPresent(Primary.class)) {
				// 기존의 구현체를 덮어쓰고 새로운 구현체를 등록합니다.
				dependencies.put(repositoryInterface, instance);
				logger.info("기본 저장소 구현체를 등록했습니다: {}", repositoryImplClass.getName());
			} else {
				// Primary 어노테이션이 없는 경우, 새로운 구현체를 등록하지 않습니다.
				logger.info("보조 저장소 구현체를 건너뛰었습니다: {}", repositoryImplClass.getName());
			}
		} else {
			// 해당 인터페이스에 대한 구현체가 아직 등록되지 않은 경우
			// 새로운 구현체를 등록합니다.
			dependencies.put(repositoryInterface, instance);
			logger.info("저장소 구현체를 등록했습니다: {}", repositoryImplClass.getName());
		}
	}

	// 핸들러 인스턴스를 생성하는 메서드
	public Object createHandlerInstance(Class<?> handlerClass) throws Exception {
		// 핸들러 클래스의 생성자를 가져옴
		Constructor<?> constructor = handlerClass.getDeclaredConstructors()[0];
		Class<?>[] parameterTypes = constructor.getParameterTypes();

		// 생성자 파라미터 타입에 따라 의존성을 주입
		Object[] parameters = Arrays.stream(parameterTypes)
			.map(this::getDependency)
			.toArray();

		return constructor.newInstance(parameters);
	}

}

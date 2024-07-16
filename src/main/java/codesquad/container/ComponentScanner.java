package codesquad.container;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentScanner {

	private final Logger logger = LoggerFactory.getLogger(ComponentScanner.class);

	public Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
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

		List<Class<?>> classes = new ArrayList<>();
		findClassesInDirectory(packageName, directory, classes);
		return classes.toArray(new Class<?>[0]);
	}

	private void findClassesInDirectory(String packageName, File directory, List<Class<?>> classes) throws
		ClassNotFoundException {
		File[] files = directory.listFiles();
		if (files == null) {
			throw new ClassNotFoundException("패키지에 대한 파일 목록을 가져오지 못했습니다: " + packageName);
		}

		for (File file : files) {
			if (file.isDirectory()) {
				findClassesInDirectory(packageName + "." + file.getName(), file, classes);
			} else if (file.getName().endsWith(".class")) {
				String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private Class<?>[] getClassesFromJarFile(String packageName, URL resource) throws IOException {
		logger.info("JAR 시스템을 사용합니다");
		String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			List<Class<?>> classes = new ArrayList<>();
			String packagePath = packageName.replace('.', '/');
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(packagePath) && entry.getName().endsWith(".class")
					&& !entry.isDirectory()) {
					String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return classes.toArray(new Class<?>[0]);
		}
	}
}

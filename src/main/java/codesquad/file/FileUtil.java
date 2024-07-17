package codesquad.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.dto.FileData;

public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static String saveFile(FileData fileData) {
		String directoryPath = getDirectoryPath();
		String originalFileName = fileData.getFileName();
		String fileExtension = getFileExtension(originalFileName);
		String newFileName = UUID.randomUUID().toString() + fileExtension;
		String filePath = directoryPath + newFileName;

		// 디렉터리가 존재하지 않으면 생성
		createDirectoryIfNotExists(directoryPath);

		// 파일 저장
		File file = new File(filePath);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(fileData.getData());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		logger.info("save file : {}", filePath);

		return newFileName;
	}

	private static String getDirectoryPath() {
		// 사용자 홈 디렉토리 기반으로 절대 경로 설정
		String homeDirectory = System.getProperty("user.home");
		return homeDirectory + "/was/";
	}

	private static void createDirectoryIfNotExists(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private static String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if (lastIndexOfDot == -1) {
			return ""; // 파일에 확장자가 없는 경우
		}
		return fileName.substring(lastIndexOfDot);
	}

	public static FileData getFile(String fileName) {
		String directoryPath = getDirectoryPath();
		String filePath = directoryPath + fileName;
		File file = new File(filePath);

		logger.info("get file : {}", filePath);

		if (!file.exists()) {
			return null;
		}

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] data = new byte[(int)file.length()];
			fis.read(data);
			return new FileData(file.getName(), getMimeType(file), data);
		} catch (IOException e) {
			return null;
		}
	}

	private static String getMimeType(File file) {
		String fileName = file.getName();
		return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	}
}

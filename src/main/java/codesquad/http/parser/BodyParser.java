package codesquad.http.parser;

import static codesquad.utils.StringConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codesquad.dto.FileData;

public class BodyParser {

	public static Map<String, List<Object>> parseRequestBody(InputStream inputStream,
		Map<String, List<String>> headers) throws IOException {
		int contentLength = getContentLength(headers);
		if (contentLength == -1) {
			return Map.of();  // 빈 JSON 객체를 반환합니다.
		}

		String contentType = headers.getOrDefault("Content-Type", List.of("")).get(0);
		byte[] bodyBytes = InputStreamUtils.readBytes(inputStream, contentLength);

		if (contentType.equals("application/x-www-form-urlencoded")) {
			return parseFormUrlEncodedBody(new String(bodyBytes));
		}
		if (contentType.equals("application/json")) {
			return parseJsonBody(new String(bodyBytes));
		}
		if (contentType.equals("multipart/form-data")) {
			String boundary = getBoundary(contentType);
			if (boundary == null) {
				throw new IOException("Multipart boundary not found in Content-Type header");
			}
			return parseMultipartBody(bodyBytes, boundary);
		}

		return Map.of();  // 빈 JSON 객체를 반환합니다.
	}

	private static int getContentLength(Map<String, List<String>> headers) {
		List<String> contentLengthHeaders = headers.get("Content-Length");
		if (contentLengthHeaders == null || contentLengthHeaders.isEmpty()) {
			return -1;  // Content-Length 헤더가 없는 경우
		}

		try {
			return Integer.parseInt(contentLengthHeaders.get(0));
		} catch (NumberFormatException e) {
			return -1;  // Content-Length 헤더 값이 유효하지 않은 경우
		}
	}

	private static Map<String, List<Object>> parseFormUrlEncodedBody(String body) throws IOException {
		Map<String, List<Object>> result = new HashMap<>();
		String[] pairs = body.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = URLDecoder.decode(keyValue[0], UTF8);
			String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF8) : "";
			result.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
		}

		return result;
	}

	private static Map<String, List<Object>> parseJsonBody(String body) throws IOException {
		Map<String, List<Object>> result = new HashMap<>();

		body = body.trim();
		if (body.startsWith("{") && body.endsWith("}")) {
			body = body.substring(1, body.length() - 1).trim();
		} else {
			throw new IOException("Invalid JSON format");
		}

		String[] pairs = body.split(",");
		for (String pair : pairs) {
			String[] keyValue = pair.split(":", 2);
			if (keyValue.length == 2) {
				String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
				String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
				result.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
			}
		}

		return result;
	}

	private static String getBoundary(String contentType) {
		String[] parts = contentType.split(";");
		for (String part : parts) {
			if (part.trim().startsWith("boundary=")) {
				return part.split("=")[1];
			}
		}
		return null;
	}

	private static Map<String, List<Object>> parseMultipartBody(byte[] bodyBytes, String boundary) throws IOException {
		Map<String, List<Object>> formData = new HashMap<>();
		String delimiter = "--" + boundary;
		String endDelimiter = "--" + boundary + "--";
		int startPos = 0;

		while (true) {
			int boundaryPos = indexOf(bodyBytes, delimiter.getBytes(), startPos);
			if (boundaryPos == -1) break;

			int nextPos = indexOf(bodyBytes, "\r\n\r\n".getBytes(), boundaryPos);
			if (nextPos == -1) break;

			int partEndPos = indexOf(bodyBytes, delimiter.getBytes(), nextPos + 4);
			if (partEndPos == -1) break;

			byte[] partHeadersBytes = Arrays.copyOfRange(bodyBytes, boundaryPos + delimiter.length(), nextPos);
			String partHeaders = new String(partHeadersBytes).trim();

			int bodyStartPos = nextPos + 4;
			int bodyEndPos = partEndPos - 2;
			byte[] partBodyBytes = Arrays.copyOfRange(bodyBytes, bodyStartPos, bodyEndPos);

			// 파트 헤더를 파싱하고 formData에 추가하는 로직
			String[] headers = partHeaders.split("\r\n");
			String name = null;
			String fileName = null;
			String contentType = null;
			for (String header : headers) {
				if (header.startsWith("Content-Disposition: form-data;")) {
					name = extractName(header);
					fileName = extractFileName(header);
				}
				if (header.startsWith("Content-Type:")) {
					contentType = header.split(":")[1].trim();
				}
			}

			if (name != null) {
				if (fileName != null) {
					// 파일 데이터로 처리
					formData.putIfAbsent(name, new ArrayList<>());
					formData.get(name).add(new FileData(fileName, contentType, partBodyBytes));
				} else {
					// 텍스트 데이터로 처리
					formData.putIfAbsent(name, new ArrayList<>());
					formData.get(name).add(new String(partBodyBytes));
				}
			}

			startPos = partEndPos;
		}

		return formData;
	}

	private static int indexOf(byte[] array, byte[] target, int start) {
		outer: for (int i = start; i <= array.length - target.length; i++) {
			for (int j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	private static String extractName(String contentDisposition) {
		String[] parts = contentDisposition.split(";");
		for (String part : parts) {
			if (part.trim().startsWith("name=")) {
				return part.split("=")[1].replace("\"", "").trim();
			}
		}
		return null;
	}

	private static String extractFileName(String contentDisposition) {
		String[] parts = contentDisposition.split(";");
		for (String part : parts) {
			if (part.trim().startsWith("filename=")) {
				return part.split("=")[1].replace("\"", "").trim();
			}
		}
		return null;
	}
}

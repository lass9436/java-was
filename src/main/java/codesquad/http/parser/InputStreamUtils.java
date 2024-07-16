package codesquad.http.parser;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtils {

	public static String readLine(InputStream inputStream) throws IOException {
		StringBuilder line = new StringBuilder();
		int nextChar;
		while ((nextChar = inputStream.read()) != -1) {
			if (nextChar == '\r') {
				nextChar = inputStream.read(); // Skip '\n'
				break;
			}
			if (nextChar == '\n') {
				break;
			}
			line.append((char)nextChar);
		}
		return line.toString();
	}

	public static byte[] readBytes(InputStream inputStream, int length) throws IOException {
		byte[] buffer = new byte[length];
		int bytesRead = 0;
		while (bytesRead < length) {
			int result = inputStream.read(buffer, bytesRead, length - bytesRead);
			if (result == -1)
				break;
			bytesRead += result;
		}
		return buffer;
	}
}

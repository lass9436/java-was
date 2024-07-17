package codesquad.dto;

public class FileData {
	private String fileName;
	private String contentType;
	private byte[] data;

	public FileData(String fileName, String contentType, byte[] data) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return "FileData{" +
			"fileName='" + fileName + '\'' +
			", contentType='" + contentType + '\'' +
			", dataSize=" + data.length +
			'}';
	}
}

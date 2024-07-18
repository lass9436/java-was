package codesquad.model.post;

public class Post {
	private int id;
	private String username;
	private String title;
	private String content;
	private String imageUrl; // 이미지 링크를 저장할 필드 추가

	public Post(String username, String title, String content) {
		this.username = username;
		this.title = title;
		this.content = content;
	}

	// 새로운 생성자 추가 - 이미지 URL을 포함
	public Post(String username, String title, String content, String imageUrl) {
		this.username = username;
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "{" +
			"\"id\": \"" + id + "\"," +
			"\"userId\": \"" + username + "\"," +
			"\"title\": \"" + title + "\"," +
			"\"content\": \"" + content + "\"," +
			"\"imageUrl\": \"" + imageUrl + "\"" +
			'}';
	}
}

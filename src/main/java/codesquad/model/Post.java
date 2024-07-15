package codesquad.model;

public class Post {
	private int id;
	private String userId;
	private String username;
	private String title;
	private String content;
	private String createdAt;

	public Post(String userId, String username, String title, String content) {
		this.userId = userId;
		this.username = username;
		this.title = title;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
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

	public String getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "{" +
			"\"userId\": \"" + userId + "\"," +
			"\"username\": \"" + username + "\"," +
			"\"title\": \"" + title + "\"," +
			"\"content\": \"" + content + "\"," +
			"\"createdAt\": \"" + createdAt + "\"" +
			'}';
	}
}

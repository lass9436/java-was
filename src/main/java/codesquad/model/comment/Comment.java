package codesquad.model.comment;

public class Comment {
	private int id;
	private int postId;
	private String username;
	private String content;

	public Comment(int postId, String username, String content) {
		this.postId = postId;
		this.username = username;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "{" +
			"\"id\": \"" + id + "\"," +
			"\"postId\": \"" + postId + "\"," +
			"\"username\": \"" + username + "\"," +
			"\"content\": \"" + content + "\"," +
			'}';
	}
}

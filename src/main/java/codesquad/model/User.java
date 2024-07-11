package codesquad.model;

public record User(String userId, String password, String name, String email) {

	@Override
	public String toString() {
		return "{" +
			"userId='" + userId + '\'' +
			", name='" + name + '\'' +
			", email='" + email + '\'' +
			'}';
	}
}

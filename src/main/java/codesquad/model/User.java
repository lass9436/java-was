package codesquad.model;

public record User(String userId, String password, String name, String email) {

	@Override
	public String toString() {
		return "User{" +
			"userId='" + userId + '\'' +
			", password='" + password + '\'' +
			", name='" + name + '\'' +
			", email='" + email + '\'' +
			'}';
	}
}

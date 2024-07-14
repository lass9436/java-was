package codesquad.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserRepository {

	private static final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

	public User create(User user) {
		if (users.containsKey(user.getUserId())) {
			throw new IllegalArgumentException("User with id " + user.getUserId() + " already exists");
		}
		users.put(user.getUserId(), user);
		return user;
	}

	public User findById(String userId) {
		if (!users.containsKey(userId)) {
			throw new IllegalArgumentException("User with id " + userId + " does not exist");
		}
		return users.get(userId);
	}

	public User update(User user) {
		if (!users.containsKey(user.getUserId())) {
			throw new IllegalArgumentException("User with id " + user.getUserId() + " does not exist");
		}
		users.put(user.getUserId(), user);
		return user;
	}

	public void delete(String userId) {
		if (!users.containsKey(userId)) {
			throw new IllegalArgumentException("User with id " + userId + " does not exist");
		}
		users.remove(userId);
	}

	public List<User> findAll() {
		return new ArrayList<>(users.values());
	}
}

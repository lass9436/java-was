package codesquad.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import codesquad.http.status.HttpStatus;
import codesquad.http.status.HttpStatusException;

public class UserRepository {

	private static final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

	public User create(User user) {
		if(users.containsKey(user.userId())){
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User with id " + user.userId() + " already exists");
		}
		users.put(user.userId(), user);
		return user;
	}

	public User findById(String userId) {
		if(!users.containsKey(userId)){
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " does not exist");
		}
		return users.get(userId);
	}

	public User update(User user) {
		if(!users.containsKey(user.userId())){
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "User with id " + user.userId() + " does not exist");
		}
		users.put(user.userId(), user);
		return user;
	}

	public void delete(String userId) {
		if(!users.containsKey(userId)){
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " does not exist");
		}
		users.remove(userId);
	}

	public List<User> findAll() {
		return new ArrayList<>(users.values());
	}

}

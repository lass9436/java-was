package codesquad.model;

import java.util.List;

import codesquad.annotation.Repository;

@Repository
public interface UserRepository {

	User create(User user);

	User findById(String userId);

	User update(User user);

	void delete(String userId);

	List<User> findAll();
}

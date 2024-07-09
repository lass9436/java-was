package codesquad.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import codesquad.http.status.HttpStatusException;

class UserRepositoryTest {

	private static UserRepository userRepository;

	@BeforeAll
	static void setUp() {
		userRepository = new UserRepository();
	}

	@Test
	void 유저_생성_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		User createdUser = userRepository.create(user);
		assertEquals(user, createdUser);
		assertEquals(user, userRepository.findById("user1"));
	}

	@Test
	void 유저_중복_생성_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepository.create(user);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userRepository.create(user));
		assertEquals("User with id user1 already exists", exception.getMessage());
	}

	@Test
	void 유저_ID_로_조회_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepository.create(user);
		User foundUser = userRepository.findById("user1");
		assertEquals(user, foundUser);
	}

	@Test
	void 존재하지_않는_유저_조회_테스트() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepository.findById("nonexistent"));
		assertEquals("User with id nonexistent does not exist", exception.getMessage());
	}

	@Test
	void 유저_업데이트_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepository.create(user);
		User updatedUser = new User("user1", "password2", "User One Updated", "user1updated@example.com");
		User returnedUser = userRepository.update(updatedUser);
		assertEquals(updatedUser, returnedUser);
		assertEquals(updatedUser, userRepository.findById("user1"));
	}

	@Test
	void 존재하지_않는_유저_업데이트_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userRepository.update(user));
		assertEquals("User with id user1 does not exist", exception.getMessage());
	}

	@Test
	void 유저_삭제_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepository.create(user);
		userRepository.delete("user1");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userRepository.findById("user1"));
		assertEquals("User with id user1 does not exist", exception.getMessage());
	}

	@Test
	void 존재하지_않는_유저_삭제_테스트() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepository.delete("nonexistent"));
		assertEquals("User with id nonexistent does not exist", exception.getMessage());
	}

	@Test
	void 여러_인스턴스_공유_저장소_테스트() {
		UserRepository repo1 = new UserRepository();
		UserRepository repo2 = new UserRepository();

		User user1 = new User("user1", "password1", "User One", "user1@example.com");
		User user2 = new User("user2", "password2", "User Two", "user2@example.com");

		repo1.create(user1);
		assertEquals(user1, repo2.findById("user1"));

		repo2.create(user2);
		assertEquals(user2, repo1.findById("user2"));
	}

	@AfterEach
	void tearDown() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			userRepository.delete(user.userId());
		}
	}
}

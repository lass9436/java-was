package codesquad.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import codesquad.model.user.User;
import codesquad.model.user.UserRepositoryImpl;

class UserRepositoryImplTest {

	private static UserRepositoryImpl userRepositoryImpl;

	@BeforeAll
	static void setUp() {
		userRepositoryImpl = new UserRepositoryImpl();
	}

	@Test
	void 유저_생성_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		User createdUser = userRepositoryImpl.create(user);
		assertEquals(user, createdUser);
		assertEquals(user, userRepositoryImpl.findById("user1"));
	}

	@Test
	void 유저_중복_생성_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepositoryImpl.create(user);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepositoryImpl.create(user));
		assertEquals("User with id user1 already exists", exception.getMessage());
	}

	@Test
	void 유저_ID_로_조회_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepositoryImpl.create(user);
		User foundUser = userRepositoryImpl.findById("user1");
		assertEquals(user, foundUser);
	}

	@Test
	void 존재하지_않는_유저_조회_테스트() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepositoryImpl.findById("nonexistent"));
		assertEquals("User with id nonexistent does not exist", exception.getMessage());
	}

	@Test
	void 유저_업데이트_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepositoryImpl.create(user);
		User updatedUser = new User("user1", "password2", "User One Updated", "user1updated@example.com");
		User returnedUser = userRepositoryImpl.update(updatedUser);
		assertEquals(updatedUser, returnedUser);
		assertEquals(updatedUser, userRepositoryImpl.findById("user1"));
	}

	@Test
	void 존재하지_않는_유저_업데이트_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepositoryImpl.update(user));
		assertEquals("User with id user1 does not exist", exception.getMessage());
	}

	@Test
	void 유저_삭제_테스트() {
		User user = new User("user1", "password1", "User One", "user1@example.com");
		userRepositoryImpl.create(user);
		userRepositoryImpl.delete("user1");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepositoryImpl.findById("user1"));
		assertEquals("User with id user1 does not exist", exception.getMessage());
	}

	@Test
	void 존재하지_않는_유저_삭제_테스트() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> userRepositoryImpl.delete("nonexistent"));
		assertEquals("User with id nonexistent does not exist", exception.getMessage());
	}

	@Test
	void 여러_인스턴스_공유_저장소_테스트() {
		UserRepositoryImpl repo1 = new UserRepositoryImpl();
		UserRepositoryImpl repo2 = new UserRepositoryImpl();

		User user1 = new User("user1", "password1", "User One", "user1@example.com");
		User user2 = new User("user2", "password2", "User Two", "user2@example.com");

		repo1.create(user1);
		assertEquals(user1, repo2.findById("user1"));

		repo2.create(user2);
		assertEquals(user2, repo1.findById("user2"));
	}

	@AfterEach
	void tearDown() {
		List<User> users = userRepositoryImpl.findAll();
		for (User user : users) {
			userRepositoryImpl.delete(user.getUserId());
		}
	}
}

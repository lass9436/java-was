package codesquad.model.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import codesquad.annotation.Primary;
import codesquad.annotation.RepositoryImpl;
import codesquad.database.Database;

@Primary
@RepositoryImpl
public class UserRepositoryH2Impl implements UserRepository {

	@Override
	public User create(User user) {
		String query = "INSERT INTO users (username, password, name, email) VALUES (?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, user.getUserId());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getEmail());

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public User findById(String userId) {
		String query = "SELECT * FROM users WHERE username = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new User(
						resultSet.getString("username"),
						resultSet.getString("password"),
						resultSet.getString("name"),
						resultSet.getString("email")
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public User update(User user) {
		String query = "UPDATE users SET password = ?, name = ?, email = ? WHERE username = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, user.getPassword());
			statement.setString(2, user.getName());
			statement.setString(3, user.getEmail());
			statement.setString(4, user.getUserId());

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public void delete(String userId) {
		String query = "DELETE FROM users WHERE username = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, userId);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<User> findAll() {
		String query = "SELECT * FROM users";
		List<User> users = new ArrayList<>();

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				User user = new User(
					resultSet.getString("username"),
					resultSet.getString("password"),
					resultSet.getString("name"),
					resultSet.getString("email")
				);
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
}

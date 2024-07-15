package codesquad.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import codesquad.annotation.RepositoryImpl;
import codesquad.database.Database;

@RepositoryImpl
public class PostRepositoryH2Impl implements PostRepository {

	@Override
	public Post create(Post post) {
		String query = "INSERT INTO posts (user_id, username, title, content) VALUES (?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query,
				 PreparedStatement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, Integer.parseInt(post.getUserId()));
			statement.setString(2, post.getUsername());
			statement.setString(3, post.getTitle());
			statement.setString(4, post.getContent());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating post failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					post.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Creating post failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return post;
	}

	@Override
	public Post findById(int postId) {
		String query = "SELECT * FROM posts WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, postId);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Post(
						resultSet.getString("user_id"),
						resultSet.getString("username"),
						resultSet.getString("title"),
						resultSet.getString("content")
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Post update(Post post) {
		String query = "UPDATE posts SET title = ?, content = ? WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, post.getTitle());
			statement.setString(2, post.getContent());
			statement.setInt(3, post.getId());

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return post;
	}

	@Override
	public void delete(int postId) {
		String query = "DELETE FROM posts WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, postId);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Post> findAll() {
		String query = "SELECT * FROM posts";
		List<Post> posts = new ArrayList<>();

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Post post = new Post(
					resultSet.getString("user_id"),
					resultSet.getString("username"),
					resultSet.getString("title"),
					resultSet.getString("content")
				);
				post.setId(resultSet.getInt("id"));
				posts.add(post);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return posts;
	}
}

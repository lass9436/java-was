package codesquad.model.post;

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
		String query = "INSERT INTO posts (username, title, content, image_url) VALUES (?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, post.getUsername());
			statement.setString(2, post.getTitle());
			statement.setString(3, post.getContent());
			statement.setString(4, post.getImageUrl());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating post failed, no rows affected.");
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
					Post post = new Post(
						resultSet.getString("username"),
						resultSet.getString("title"),
						resultSet.getString("content"),
						resultSet.getString("image_url")
					);
					post.setId(resultSet.getInt("id"));
					return post;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Post update(Post post) {
		String query = "UPDATE posts SET title = ?, content = ?, image_url = ? WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, post.getTitle());
			statement.setString(2, post.getContent());
			statement.setString(3, post.getImageUrl());
			statement.setInt(4, post.getId());

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
					resultSet.getString("username"),
					resultSet.getString("title"),
					resultSet.getString("content"),
					resultSet.getString("image_url")
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

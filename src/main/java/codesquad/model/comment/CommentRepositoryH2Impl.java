package codesquad.model.comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.annotation.RepositoryImpl;
import codesquad.database.Database;

@RepositoryImpl
public class CommentRepositoryH2Impl implements CommentRepository {

	private static final Logger logger = LoggerFactory.getLogger(CommentRepositoryH2Impl.class);

	@Override
	public Comment create(Comment comment) {
		String query = "INSERT INTO comments (post_id, username, content) VALUES (?, ?, ?)";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, comment.getPostId());
			statement.setString(2, comment.getUsername());
			statement.setString(3, comment.getContent());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating comment failed, no rows affected.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comment;
	}

	@Override
	public Comment findById(int commentId) {
		String query = "SELECT * FROM comments WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, commentId);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Comment comment = new Comment(
						resultSet.getInt("post_id"),
						resultSet.getString("username"),
						resultSet.getString("content")
					);
					comment.setId(resultSet.getInt("id"));
					return comment;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Comment update(Comment comment) {
		String query = "UPDATE comments SET content = ? WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, comment.getContent());
			statement.setInt(2, comment.getId());

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comment;
	}

	@Override
	public void delete(int commentId) {
		String query = "DELETE FROM comments WHERE id = ?";

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, commentId);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Comment> findAll() {
		String query = "SELECT * FROM comments";
		List<Comment> comments = new ArrayList<>();

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Comment comment = new Comment(
					resultSet.getInt("post_id"),
					resultSet.getString("username"),
					resultSet.getString("content")
				);
				comment.setId(resultSet.getInt("id"));
				comments.add(comment);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return comments;
	}

	@Override
	public List<Comment> findByPostId(int postId) {
		String query = "SELECT * FROM comments WHERE post_id = ?";
		List<Comment> comments = new ArrayList<>();

		try (Connection connection = Database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, postId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Comment comment = new Comment(
						resultSet.getInt("post_id"),
						resultSet.getString("username"),
						resultSet.getString("content")
					);
					comment.setId(resultSet.getInt("id"));
					comments.add(comment);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return comments;
	}
}

package codesquad.model.comment;

import java.util.List;

import codesquad.annotation.Repository;

@Repository
public interface CommentRepository {

	Comment create(Comment comment);

	Comment findById(int commentId);

	Comment update(Comment comment);

	void delete(int commentId);

	List<Comment> findAll();

	List<Comment> findByPostId(int postId);
}

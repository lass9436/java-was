package codesquad.model;

import java.util.List;

import codesquad.annotation.Repository;

@Repository
public interface PostRepository {

	Post create(Post post);

	Post findById(int postId);

	Post update(Post post);

	void delete(int postId);

	List<Post> findAll();
}

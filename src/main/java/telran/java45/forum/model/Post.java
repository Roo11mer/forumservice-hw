package telran.java45.forum.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Post {

	String id;
	@Setter
	String title;
	@Setter
	String content;
	@Setter
	String author;
	LocalDateTime dateCreated;
	Set<String> tags;
	int likes;
	List<Comment> comments;
	
	public Post(String title, String content, String author, Set<String> tags) {
		this();
		this.title = title;
		this.content = content;
		this.author = author;
		this.tags = tags;
	}

	public boolean addTag(String tag) {
		return tags.add(tag);
	}
	
	public boolean removeTag(String tag) {
		return tags.remove(tag);
	}
	
	public void addLike() {
		likes++;
	}
	
	public boolean addComment(Comment comment) {
		return comments.add(comment);
	}
	
	public boolean removeComment(Comment comment) {
		return comments.remove(comment);
	}
	
}
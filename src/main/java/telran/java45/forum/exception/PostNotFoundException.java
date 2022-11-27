package telran.java45.forum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6952550028444204207L;

	public PostNotFoundException(String id) {
		super("Post with id = " + id + " not found");

	}

}

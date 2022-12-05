package telran.java45.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java45.accounting.dao.UserAccountRepository;
import telran.java45.accounting.model.UserAccount;
import telran.java45.forum.dao.PostRepository;
import telran.java45.forum.model.Post;

@Component
@RequiredArgsConstructor
@Order(70)
public class AuthorOrModeratorValidationFilter implements Filter {
	
	final PostRepository postRepository;
	final UserAccountRepository userAccountRepository;

	@SuppressWarnings("unlikely-arg-type")
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (checkEndPoint(path, method)) {
			UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
			String postId = path.split("/")[3];
			Post post = postRepository.findById(postId).orElse(null);
			if (post == null) {
				response.sendError(404, "post id = " + postId + " not found");
				return;
			}
			String author = post.getAuthor();
			if (!userAccount.equals(author) || !userAccount.getRoles().contains("Moderator".toUpperCase())) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String path, String method) {
		boolean res = path.matches("/forum/post/\\w+/?")
				&& ("PUT".equalsIgnoreCase(method) || ("DELETE".equalsIgnoreCase(method)));
		return res;
	}

}

package telran.java45.security.filter;

import java.io.IOException;
import java.util.Arrays;

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
@Order
public class PostOwnerFilter implements Filter {
	
	final PostRepository postRepository;
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if(checkEndPoint(request.getMethod(),request.getServletPath())) {
			String id = parseIdFromPath(request.getRequestURI());
			Post post = postRepository.findById(id).orElse(null);
			if (post == null) {
				response.sendError(404);
				return;
			}
			UserAccount userAccount = userAccountRepository.findById(post.getAuthor()).get();
			if (!request.getUserPrincipal().getName().equalsIgnoreCase(userAccount.getLogin())) {
				response.sendError(403);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	private String parseIdFromPath(String requestURI) {
		String[] parts = requestURI.split("/");
		String res = Arrays.asList(parts).get(parts.length - 1);
		return res;
	}
	
	private boolean checkEndPoint(String method, String path) {
		return "PUT".equalsIgnoreCase(method) && path.matches("/forum/post/\\w+/?");
	}

}

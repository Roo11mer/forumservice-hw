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

@Component
@RequiredArgsConstructor
@Order(30)
public class AccountFilter implements Filter {
	
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if(checkEndPoint(request.getMethod(),request.getServletPath())) {
			String loginFromPath = parseLoginFromPath(request.getRequestURI());
			if (!request.getUserPrincipal().getName().equals(loginFromPath)){
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private String parseLoginFromPath(String requestURI) {
		String[] parts = requestURI.split("/");
		String res = Arrays.asList(parts).get(Arrays.binarySearch(parts, "user") + 1);
		return res;
	}

	private boolean checkEndPoint(String method, String path) {
		return "PUT".equalsIgnoreCase(method) && path.matches("/account/user/\\w+/?");
	}

}

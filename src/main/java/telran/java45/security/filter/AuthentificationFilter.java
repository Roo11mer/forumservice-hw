package telran.java45.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java45.accounting.dao.UserAccountRepository;
import telran.java45.accounting.model.UserAccount;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthentificationFilter implements Filter {
	
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if(checkEndPoint(request.getMethod(),request.getServletPath())) {
			String token = request.getHeader("Authorization");
			if (token == null) {
				response.sendError(401);
				return;
			}
		String[] credentials;
		try {
			credentials = getCredentials(request.getHeader("Authorization"));
		} catch (Exception e) {
			response.sendError(401, "Login is not valid");
		    return;
		}
		UserAccount userAccount = userAccountRepository.findById(credentials[1]).orElse(null);
		if(userAccount == null || !BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
			response.sendError(401, "Login or password incorrect");
			return;
		}
		request = new WrappedRequest(request,credentials[0]);
	}
		chain.doFilter(request, response);

	}

    private boolean checkEndPoint(String method, String path) {
		return !(("POST".equalsIgnoreCase(method) && path.matches("/account/register/?")));
	}

	private String[] getCredentials(String token) {
		String[] basicAuth = token.split(" ");
		String decode = new String(Base64.getDecoder().decode(basicAuth[1]));
		return decode.split(":");
	}

	private class WrappedRequest extends HttpServletRequestWrapper{
		String login;
		public WrappedRequest(HttpServletRequest request,String login) {
			super(request);
			this.login = login;
		}
		
		@Override
		public Principal getUserPrincipal() {
			return ()-> login;
		}
	}

}

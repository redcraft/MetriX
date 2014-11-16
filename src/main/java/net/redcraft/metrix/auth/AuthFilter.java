package net.redcraft.metrix.auth;

import com.dropbox.core.DbxClient;
import org.codehaus.jackson.map.ObjectMapper;
import net.redcraft.metrix.GenericExceptionMapper;

import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Authentication filter.
 * Protects all APIs except auth one
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Singleton
public class AuthFilter implements Filter {

	public static final String API_AUTH_URL = "/api/auth.*";
	public static final String DROPBOX_CLIENT = DbxClient.class.getName();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		Object dbxClient = request.getSession().getAttribute(DROPBOX_CLIENT);
		if (!request.getRequestURI().matches(API_AUTH_URL) && dbxClient == null) {
			HttpServletResponse response = (HttpServletResponse) servletResponse;
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON);
			PrintWriter writer = response.getWriter();
			writer.write(new ObjectMapper().writeValueAsString(new GenericExceptionMapper.ErrorEntity("")));
			writer.flush();
			writer.close();
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}

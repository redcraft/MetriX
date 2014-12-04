package net.redcraft.metrix.auth;

import com.dropbox.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Locale;


/**
 * Provides authentication mechanism using Dropbox OAuth2.
 * This is the only API which is not processed by {@link AuthFilter}
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Singleton
@Path("/auth")
public class AuthAPI {

	private static final Logger log = LoggerFactory.getLogger(AuthAPI.class);

	private static final String SESSION_KEY = "dropbox-auth-csrf-token";
	private static final String DROPBOX_AUTH = "dropbox-auth";

	private static final String CLIENT_IDENTIFIER = "MetriX";
	private static final DbxRequestConfig REQUEST_CONFIG = new DbxRequestConfig(CLIENT_IDENTIFIER, Locale.getDefault().toString());

	private final String apiKey;
	private final String apiSecret;
	private final boolean httpsOnly;

	@Inject
	public AuthAPI(@Named("API_KEY") String apiKey, @Named("API_SECRET") String apiSecret, @Named("HTTPS_ONLY") boolean httpsOnly) {
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.httpsOnly = httpsOnly;
	}

	private enum ErrorType {
		AUTH_ERROR, NOT_APPROVED
	}

	@Nonnull
	private String getRequestUrl(HttpServletRequest request) {
		return (httpsOnly ? "https" : request.getScheme()) + "://" +
				request.getServerName() +
				("http".equals(request.getScheme()) && request.getServerPort() == 80 ||
						"https".equals(request.getScheme()) && request.getServerPort() == 443 ?
								"" : ":" + request.getServerPort() ) +
				request.getRequestURI() +
				(request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}

	@Nonnull
	private String getURL(HttpServletRequest request) {
		return getRequestUrl(request).replaceAll(AuthFilter.API_AUTH_URL, "/");
	}

	@Nonnull
	private Response createSuccessRedirect(HttpServletRequest request, String path) {
		return Response.temporaryRedirect(URI.create(getURL(request) + path)).build();
	}

	@Nonnull
	private Response createErrorRedirect(HttpServletRequest request, ErrorType type) {
		request.getSession().invalidate();
		return Response.temporaryRedirect(URI.create(getURL(request) + "#/error?type=" + type.name())).build();
	}

	/**
	 * Starts authentication process by creation authentication request and redirect to Dropbox OAuth2 authentication API
	 *
	 * @return redirect to Dropbox OAuth2 authentication API
	 */
	@GET
	public Response getAuth(@Context HttpServletRequest request) {
		log.debug("Processing authentication request");

		HttpSession session = request.getSession(true);
		DbxAppInfo appInfo = new DbxAppInfo(apiKey, apiSecret);
		DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session, SESSION_KEY);
		DbxWebAuth auth = new DbxWebAuth(REQUEST_CONFIG, appInfo, getRequestUrl(request) + "/validate", csrfTokenStore);
		session.setAttribute(DROPBOX_AUTH, auth);

		return Response.temporaryRedirect(URI.create(auth.start())).build();
	}

	/**
	 * Validates authentication callback from Dropbox OAuth2 authentication APIs
	 *
	 * @return if validation was successful redirects to page with stats otherwise redirects to error page
	 */
	@GET
	@Path("validate")
	public Response validateAuthToken(@Context HttpServletRequest request) {
		log.debug("Starting auth token validation");

		HttpSession session = request.getSession();
		DbxWebAuth auth = (DbxWebAuth) session.getAttribute(DROPBOX_AUTH);
		if (auth != null) {
			DbxAuthFinish authFinish;
			try {
				authFinish = auth.finish(request.getParameterMap());
			} catch (DbxWebAuth.NotApprovedException e) {
				log.error("Auth validation error", e);
				return createErrorRedirect(request, ErrorType.NOT_APPROVED);
			} catch (DbxWebAuth.BadRequestException | DbxWebAuth.CsrfException | DbxWebAuth.ProviderException |
					DbxException | DbxWebAuth.BadStateException e) {
				log.error("Auth validation error", e);
				return createErrorRedirect(request, ErrorType.AUTH_ERROR);
			}
			DbxClient client = new DbxClient(REQUEST_CONFIG, authFinish.accessToken);
			session.setAttribute(AuthFilter.DROPBOX_CLIENT, client);
			return createSuccessRedirect(request, "#/");
		} else {
			log.error("Auth validation error: auth service not found");
			return createErrorRedirect(request, ErrorType.AUTH_ERROR);
		}
	}

	/**
	 * Logout user by destroying HTTP session
	 *
	 * @return redirects to login page
	 */
	@GET
	@Path("logout")
	public Response logout(@Context HttpServletRequest request) {
		request.getSession().invalidate();
		return createSuccessRedirect(request, "#/login");
	}
}

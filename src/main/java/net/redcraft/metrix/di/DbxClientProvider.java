package net.redcraft.metrix.di;

import com.dropbox.core.DbxClient;
import net.redcraft.metrix.auth.AuthFilter;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides {@link com.dropbox.core.DbxClient} instance for DI from HTTP session context
 * Throws {@link java.lang.RuntimeException} if HTTP session context doesn't contain {@link com.dropbox.core.DbxClient} instance
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
class DbxClientProvider implements Provider<DbxClient> {

	private final HttpServletRequest request;

	@Inject
	public DbxClientProvider(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public DbxClient get() {
		DbxClient client = (DbxClient) request.getSession().getAttribute(AuthFilter.DROPBOX_CLIENT);
		if (client == null) {
			throw new RuntimeException("Can't find DbxClient instance for session");
		}
		return client;
	}
}

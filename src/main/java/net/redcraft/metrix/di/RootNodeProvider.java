package net.redcraft.metrix.di;

import com.dropbox.core.DbxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.redcraft.metrix.node.RunnableNode;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.Executor;

/**
 * Provides root Dropbox {@link net.redcraft.metrix.node.RunnableNode} (/) which is already passed to thread pool executor
 * Stores node in HTTP session context. Creates new node if not exist
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
public class RootNodeProvider implements Provider<RunnableNode> {

	private static final Logger log = LoggerFactory.getLogger(RootNodeProvider.class);

	private final HttpServletRequest request;
	private final Executor executor;
	private final DbxClient dbxClient;

	@Inject
	public RootNodeProvider(HttpServletRequest request, Executor executor, DbxClient dbxClient) {
		this.request = request;
		this.executor = executor;
		this.dbxClient = dbxClient;
	}

	@Override
	public RunnableNode get() {
		HttpSession session = request.getSession();
		RunnableNode runnableNode = (RunnableNode) session.getAttribute(RunnableNode.class.getName());
		if (runnableNode == null) {
			runnableNode = new RunnableNode(null, "/", dbxClient, executor);
			executor.execute(runnableNode);
			session.setAttribute(RunnableNode.class.getName(), runnableNode);
			log.debug("Root node created");
		}
		return runnableNode;
	}

	public void reset() {
		request.getSession().setAttribute(RunnableNode.class.getName(), null);
	}
}

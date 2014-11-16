package net.redcraft.metrix;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import net.redcraft.metrix.di.RootNodeProvider;
import net.redcraft.metrix.node.RunnableNode;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Basic REST API interface.
 * Requires authentication
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Path("/user")
public class AccountAPI {

	private final DbxClient dbxClient;
	private final RootNodeProvider rootNodeProvider;

	class Stats {
		private final long total;
		private final RunnableNode node;

		public Stats(long total, RunnableNode node) {
			this.total = total;
			this.node = node;
		}

		public long getTotal() {
			return total;
		}

		public RunnableNode getNode() {
			return node;
		}
	}

	@Inject
	public AccountAPI(DbxClient dbxClient, RootNodeProvider rootNodeProvider) {
		this.dbxClient = dbxClient;
		this.rootNodeProvider = rootNodeProvider;
	}

	/**
	 * Returns information about authenticated Dropbox account
	 *
	 * @return JSON representation of {@link com.dropbox.core.DbxAccountInfo}
	 * @throws DbxException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DbxAccountInfo getInfo() throws DbxException {
		return dbxClient.getAccountInfo();
	}

	/**
	 * Returns statistic information for provided folder
	 *
	 * @param path path to folder which statistics should be returned
	 * @return JSON representation of {@link net.redcraft.metrix.node.RunnableNode}
	 */
	@GET
	@Path("stats")
	@Produces(MediaType.APPLICATION_JSON)
	public Stats getStats(@QueryParam("path") String path) {
		RunnableNode node = rootNodeProvider.get();
		return new Stats(node.getSize(), node.getNodeForPath(path));
	}

	/**
	 * Reset statistic collector of {@link net.redcraft.metrix.di.RootNodeProvider}
	 */
	@DELETE
	@Path("stats")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStats() {
		rootNodeProvider.reset();
		return Response.ok("{}").build();
	}

}

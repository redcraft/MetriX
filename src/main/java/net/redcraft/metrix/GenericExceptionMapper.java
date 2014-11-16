package net.redcraft.metrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

/**
 * Exception serializer into HTTP responses with JSON payload
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Provider
@Singleton
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private final static Logger log = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable throwable) {
		String traceId = UUID.randomUUID().toString();
		log.error(traceId, throwable);
		return Response.
				status(Response.Status.INTERNAL_SERVER_ERROR).
				entity(new ErrorEntity(traceId)).
				type(MediaType.APPLICATION_JSON_TYPE).
				build();
	}

	/**
	 * Helper class for serialisation exceptions into JSON objects with UUID reference to log
	 *
	 * @author Maxim Gurkin <redmax3d@gmail.com>
	 */
	public static class ErrorEntity {
		private final String status = "error";
		private final String traceId;

		public ErrorEntity(String traceId) {
			this.traceId = traceId;
		}

		public String getStatus() {
			return status;
		}

		public String getTraceId() {
			return traceId;
		}
	}
}

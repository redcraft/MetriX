package net.redcraft.metrix.di;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides thread pool for {@link net.redcraft.metrix.node.RunnableNode} execution
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Singleton
class FixedThreadPoolProvider implements Provider<Executor> {

	private final ExecutorService executor;

	@Inject
	public FixedThreadPoolProvider(@Named("FIXED_THREAD_POOL_SIZE") int threadPoolSize) {
		executor = Executors.newFixedThreadPool(threadPoolSize);
	}

	@PreDestroy
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public Executor get() {
		return executor;
	}
}

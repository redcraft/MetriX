package net.redcraft.metrix.di;

import net.redcraft.metrix.node.RunnableNode;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.concurrent.*;

/**
 * Provides thread pool for {@link net.redcraft.metrix.node.RunnableNode} execution
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
@Singleton
class FixedThreadPoolProvider implements Provider<Executor> {

	private final ExecutorService executor;

	private static class ComparePriority<T extends RunnableNode> implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			return o1.getPriority() - o2.getPriority();
		}
	}

	@Inject
	public FixedThreadPoolProvider(@Named("FIXED_THREAD_POOL_SIZE") int threadPoolSize) {
		executor = new ThreadPoolExecutor(1, threadPoolSize, 10, TimeUnit.SECONDS,
				new PriorityBlockingQueue<>(20, new ComparePriority()));
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

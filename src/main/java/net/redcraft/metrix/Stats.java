package net.redcraft.metrix;

import net.redcraft.metrix.node.RunnableNode;

/**
 * Created by RED on 10/20/14.
 */
public class Stats {
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

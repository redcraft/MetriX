package net.redcraft.metrix.node;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import net.redcraft.metrix.ColorGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by RED on 9/30/14.
 */
public class RunnableNode implements Runnable {

	private final RunnableNode parent;
	private final String path;
	private final AtomicLong size = new AtomicLong();
	private final List<RunnableNode> children;
	private List<Node> files = null;
	private final DbxClient client;
	private final Executor executor;
	private boolean complete = false;
	private final int priority;

	public RunnableNode(RunnableNode parent, String path, int priority, DbxClient client, Executor executor) {
		this.path = path;
		this.parent = parent;
		this.client = client;
		this.executor = executor;
		this.children = new ArrayList<>();
		this.priority = priority;
	}

	private void addChildrenSize(long size) {
		this.size.addAndGet(size);
		if(parent != null) {
			parent.addChildrenSize(size);
		}
	}


	public String getPath() {
		return path;
	}

	private void childComplete() {
		complete = children.stream().allMatch((child) -> child.isComplete());
		if (complete && parent != null) {
			parent.childComplete();
		}
	}

	public List<Node> getChildren() {
		ColorGenerator colorGenerator = new ColorGenerator();
		List<Node> nodes = children.stream()
				.map((child) -> new Node(
						child.path, child.size.get(),
						Node.NodeType.DIR, child.isComplete()))
				.collect(Collectors.toList());
		nodes.addAll(getFiles());
		Collections.sort(nodes);
		nodes.stream()
				.forEach((node) -> node.setColor(colorGenerator.getColor()));
		return nodes;
	}

	private DbxEntry.WithChildren getEntryWithChildren(String path) {
		DbxEntry.WithChildren entity;
		try {
			entity = client.getMetadataWithChildren(path);
		} catch (DbxException e) {
			throw new RuntimeException(e);
		}
		return entity;
	}

	private List<Node> getFiles() {
		if (files == null) {
			files = getEntryWithChildren(path).children.stream()
					.filter(child -> child.isFile())
					.map(file -> new Node(file.path, file.asFile().numBytes, Node.NodeType.FILE, true))
					.collect(Collectors.toList());
		}
		return files;
	}

	@Override
	public void run() {
		long size = 0;
		for(DbxEntry file : getEntryWithChildren(path).children) {
			if(file.isFile()) {
				size += file.asFile().numBytes;
			}
			else {
				RunnableNode child = new RunnableNode(this, file.path, priority + 1, client, executor);
				children.add(child);
				executor.execute(child);
			}
		}
		addChildrenSize(size);
		if (children.size() == 0 && parent != null) {
			complete = true;
			parent.childComplete();
		}
	}

	public RunnableNode getNodeForPath(String path) {
		RunnableNode runnableNode = null;
		if(this.path.equals(path)) {
			runnableNode = this;
		}
		else {
			for(RunnableNode child : children) {
				if (path.startsWith(child.getPath())) {
					runnableNode = child.getNodeForPath(path);
					if (runnableNode != null) {
						break;
					}
				}
			}
		}
		return runnableNode;
	}

	public long getSize() {
		return size.get();
	}

	public boolean isComplete() {
		return complete;
	}

	public int getPriority() {
		return priority;
	}
}

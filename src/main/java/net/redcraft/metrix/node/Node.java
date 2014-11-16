package net.redcraft.metrix.node;

/**
* Created by RED on 9/29/14.
*/
public class Node implements Comparable<Node> {

	public enum NodeType {
		DIR, FILE
	};

	private final String path;
	private final long size;
	private final NodeType type;
	private final boolean complete;
	private String color;

	public Node(String path, long size, NodeType type, boolean complete) {
		this.path = path;
		this.size = size;
		this.type = type;
		this.complete = complete;
	}

	public String getPath() {
		return path;
	}

	public long getSize() {
		return size;
	}

	public NodeType getType() {
		return type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isComplete() {
		return complete;
	}

	@Override
	public int compareTo(Node o) {
		return (int) Math.signum(o.size - size);
	}
}

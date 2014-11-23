package net.redcraft.metrix;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Color generator for folder list
 * First 13 colors are fixed and always the same. Starting from 14th colors generated randomly
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
public class ColorGenerator {
	private static final List<String> DEFAULT_COLORS = ImmutableList.of(
			"#73bffc", "#7FDBFF", "#39CCCC", "#3D9970", "#2ECC40", "#01FF70",
			"#FFDC00", "#FF851B", "#FF4136", "#85144B", "#F012BE", "#B10DC9"
	);
	private static final String[] LATTERS = "0123456789ABCDEF".split("");
	private final Queue<String> colorQueue = new LinkedList<>(DEFAULT_COLORS);

	/**
	 * Returns hexadecimal color
	 *
	 * @return hexadecimal representation of color suitable for CSS
	 */
	public String getColor() {
		return colorQueue.isEmpty() ? getRandomColor() : colorQueue.poll();
	}

	private String getRandomColor() {
		StringBuffer color = new StringBuffer("#");
		for (int i = 0; i < 6; i++) {
			color.append(LATTERS[(int) Math.floor(Math.random() * 16)]);
		}
		return color.toString();
	}
}

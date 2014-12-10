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
			"#73bffc", "#7FDBFF", "#39CCCC", "#165ad9", "#203980",
			"#01FF70", "#2ECC40", "#3D9970", "#575931", "#06403c",
			"#FFDC00", "#FF851B", "#FF4136", "#F012BE", "#B10DC9",
			"#85144B", "#ccad99", "#ffa280", "#99614d", "#a64200",
			"#4c2900", "#f29d3d", "#bfa330", "#a9bf8f", "#434f59",
			"#fbbfff", "#8d5ba6", "#c7f218",  "#b2d916", "#12b395"
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

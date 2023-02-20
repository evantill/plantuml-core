package net.sourceforge.plantuml.klimt.sprite;

import java.awt.Color;
import java.util.Objects;

import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorSimple;
import net.sourceforge.plantuml.klimt.color.HColors;

public class ColorPalette {

	private static final String colorValue = "!#$%&*+-:;<=>?@^_~GHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public char getCharFor(Color dest) {
		return getCharFor(HColors.simple(dest));
	}

	public char getCharFor(HColor dest) {
		char result = 0;
		int resultDist = Integer.MAX_VALUE;
		for (int i = 0; i < colorValue.length(); i++) {
			final char c = colorValue.charAt(i);
			final int dist = ((HColorSimple) dest).distanceTo((HColorSimple) getHtmlColorSimpleFor(c));
			if (dist < resultDist) {
				result = c;
				resultDist = dist;
			}
		}
		assert result != 0;
		return result;
	}

	private HColor getHtmlColorSimpleFor(char c) {
		final Color color = Objects.requireNonNull(getColorFor(c));
		return HColors.simple(color);
	}

	public Color getColorFor(char c) {
		final int col = colorValue.indexOf(c);
		if (col == -1) {
			return null;
		}
		final int blue = (col % 4) * 85;
		final int green = ((col / 4) % 4) * 85;
		final int red = ((col / 16) % 4) * 85;
		return new Color(red, green, blue);
	}

}

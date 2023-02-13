package net.sourceforge.plantuml.timingdiagram;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;

public class ChangeState implements Comparable<ChangeState> {

	private final TimeTick when;
	private final String[] states;
	private final String comment;
	private final Colors colors;

	public ChangeState(TimeTick when, String comment, Colors colors, String... states) {
		if (states.length == 0)
			throw new IllegalArgumentException();

		this.when = when;
		this.states = states;
		this.comment = comment;
		this.colors = colors;
	}

	public int compareTo(ChangeState other) {
		return this.when.compareTo(other.when);
	}

	public final TimeTick getWhen() {
		return when;
	}

	public final String getState() {
		return states[0];
	}

	public final List<String> getStates() {
		return Arrays.asList(states);
	}

	public String getComment() {
		return comment;
	}

	public final HColor getBackColor(ISkinParam skinParam, Style style) {
		if (colors == null || colors.getColor(ColorType.BACK) == null)
			return style.value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());

		return colors.getColor(ColorType.BACK);
	}

	private final HColor getLineColor(ISkinParam skinParam, Style style) {
		if (colors == null || colors.getColor(ColorType.LINE) == null)
			return style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());

		return colors.getColor(ColorType.LINE);
	}

	private UStroke getStroke(Style style) {
		return style.getStroke();
	}

	public SymbolContext getContext(ISkinParam skinParam, Style style) {
		return new SymbolContext(getBackColor(skinParam, style), getLineColor(skinParam, style))
				.withStroke(getStroke(style));
	}

	public final boolean isBlank() {
		return states[0].equals("{...}");
	}

	public final boolean isCompletelyHidden() {
		return states[0].equals("{hidden}");
	}

	public final boolean isFlat() {
		return states[0].equals("{-}");
	}

	// public final boolean isUnknown() {
	// return states[0].equals("{?}");
	// }

}

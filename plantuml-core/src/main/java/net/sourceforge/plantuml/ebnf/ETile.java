package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.ULine;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.CopyForegroundColorToBackgroundColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public abstract class ETile extends AbstractTextBlock {

	protected final boolean TRACE = false;

	public static StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.ebnf);
	}

	public abstract void push(ETile tile);

	@Override
	final public XDimension2D calculateDimension(StringBounder stringBounder) {
		final double width = getWidth(stringBounder);
		final double h1 = getH1(stringBounder);
		final double h2 = getH2(stringBounder);
		return new XDimension2D(width, h1 + h2);
	}

	public abstract double getWidth(StringBounder stringBounder);

	public abstract double getH1(StringBounder stringBounder);

	public abstract double getH2(StringBounder stringBounder);

	protected final void drawHline(UGraphic ug, double y, double x1, double x2) {
		ug.apply(new UTranslate(x1, y)).draw(ULine.hline(x2 - x1));
	}

	protected final void drawHlineDirected(UGraphic ug, double y, double x1, double x2, double coef) {
		ug.apply(new UTranslate(x1, y)).draw(ULine.hline(x2 - x1));
		if (x2 > x1 + 25)
			ug.apply(new CopyForegroundColorToBackgroundColor())
					.apply(new UTranslate(x1 * (1 - coef) + x2 * coef - 2, y)).draw(getArrowToRight());

	}

	protected final void drawHlineAntiDirected(UGraphic ug, double y, double x1, double x2, double coef) {
		ug.apply(new UTranslate(x1, y)).draw(ULine.hline(x2 - x1));
		ug.apply(new CopyForegroundColorToBackgroundColor()).apply(new UTranslate(x1 * (1 - coef) + x2 * coef - 2, y))
				.draw(getArrowToLeft());

	}

	protected final void drawVline(UGraphic ug, double x, double y1, double y2) {
		ug.apply(new UTranslate(x, y1)).draw(ULine.vline(y2 - y1));
	}

	protected final void drawVlineDirected(UGraphic ug, double x, double y1, double y2) {
		ug.apply(new UTranslate(x, y1)).draw(ULine.vline(y2 - y1));
		ug.apply(new CopyForegroundColorToBackgroundColor()).apply(new UTranslate(x, y1 * .5 + y2 * .5 - 2))
				.draw(getArrowToBottom());

	}

	protected final void drawVlineAntiDirected(UGraphic ug, double x, double y1, double y2) {
		ug.apply(new UTranslate(x, y1)).draw(ULine.vline(y2 - y1));
		ug.apply(new CopyForegroundColorToBackgroundColor()).apply(new UTranslate(x, y1 * .5 + y2 * .5 + 2))
				.draw(getArrowToTop());

	}

	static UPath getArrowToLeft() {
		final UPath arrow = new UPath();
		arrow.moveTo(0, 0);
		arrow.lineTo(0, -3);
		arrow.lineTo(-6, 0);
		arrow.lineTo(0, 3);
		arrow.lineTo(0, 0);
		arrow.closePath();
		return arrow;
	}

	static UPath getArrowToRight() {
		final UPath arrow = new UPath();
		arrow.moveTo(0, 0);
		arrow.lineTo(0, -3);
		arrow.lineTo(6, 0);
		arrow.lineTo(0, 3);
		arrow.lineTo(0, 0);
		arrow.closePath();
		return arrow;
	}

	static UPath getArrowToBottom() {
		final UPath arrow = new UPath();
		arrow.moveTo(0, 0);
		arrow.lineTo(3, 0);
		arrow.lineTo(0, 6);
		arrow.lineTo(-3, 0);
		arrow.lineTo(0, 0);
		arrow.closePath();
		return arrow;
	}

	static UPath getArrowToTop() {
		final UPath arrow = new UPath();
		arrow.moveTo(0, 0);
		arrow.lineTo(3, 0);
		arrow.lineTo(0, -6);
		arrow.lineTo(-3, 0);
		arrow.lineTo(0, 0);
		arrow.closePath();
		return arrow;
	}

	protected void addCommentBelow(String comment) {
		System.err.println("Ignoring below comment " + comment + " " + getClass());
	}

	protected void addCommentAbove(String comment) {
		System.err.println("Ignoring above comment " + comment + " " + getClass());
	}

	protected String getRepetitionLabel() {
		return "?";
	}

}

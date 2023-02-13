package net.sourceforge.plantuml.cucadiagram;

import net.atmp.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public abstract class BodyEnhancedAbstract extends AbstractTextBlock implements TextBlock {

	protected final HorizontalAlignment align;
	protected final FontConfiguration titleConfig;
	protected TextBlock area;
	private final Style style;

	BodyEnhancedAbstract(HorizontalAlignment align, FontConfiguration titleConfig, Style style) {
		this.align = align;
		this.titleConfig = titleConfig;
		this.style = style;
	}

	public static boolean isBlockSeparator(CharSequence cs) {
		final String s = cs.toString();
		if (s.startsWith("--") && s.endsWith("--"))
			return true;

		if (s.startsWith("==") && s.endsWith("=="))
			return true;

		if (s.startsWith("..") && s.endsWith("..") && s.equals("...") == false)
			return true;

		if (s.startsWith("__") && s.endsWith("__"))
			return true;

		return false;
	}

	public final XDimension2D calculateDimension(StringBounder stringBounder) {
		return getArea(stringBounder).calculateDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		getArea(ug.getStringBounder()).drawU(ug);
	}

	final protected TextBlock getTitle(String s, ISkinSimple spriteContainer) {
		if (s.length() <= 4)
			return null;

		s = StringUtils.trin(s.substring(2, s.length() - 2));
		return Display.getWithNewlines(s).create(titleConfig, HorizontalAlignment.LEFT, spriteContainer);
	}

	abstract protected TextBlock getArea(StringBounder stringBounder);

	abstract protected double getMarginX();

	final protected TextBlock decorate(TextBlock block, char separator, TextBlock title, StringBounder stringBounder) {
		final double marginX = getMarginX();
		if (separator == 0)
			return TextBlockUtils.withMargin(block, marginX, 0);

		if (title == null)
			return new TextBlockLineBefore(getDefaultThickness(), TextBlockUtils.withMargin(block, marginX, 4), separator);

		final XDimension2D dimTitle = title.calculateDimension(stringBounder);
		final TextBlock raw = new TextBlockLineBefore(getDefaultThickness(),
				TextBlockUtils.withMargin(block, marginX, 6, dimTitle.getHeight() / 2, 4), separator, title);
		return TextBlockUtils.withMargin(raw, 0, 0, dimTitle.getHeight() / 2, 0);
	}

	final protected double getDefaultThickness() {
		return style.value(PName.LineThickness).asDouble();
	}

	public final Style getStyle() {
		return style;
	}

}

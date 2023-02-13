package net.sourceforge.plantuml.skin.rose;

import net.atmp.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.URectangle;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class ComponentRoseParticipant extends AbstractTextualComponent {

	private final HColor back;
	private final HColor foregroundColor;
	private final double deltaShadow;
	private final double roundCorner;
	private final double diagonalCorner;
	private final UStroke stroke;
	private final double minWidth;
	private final boolean collections;
	private final double padding;

	public ComponentRoseParticipant(Style style, Style stereo, Display stringsToDisplay, ISkinSimple spriteContainer,
			double minWidth, boolean collections, double padding) {
		super(style, stereo, LineBreakStrategy.NONE, 7, 7, 7, spriteContainer, stringsToDisplay, false);

		this.roundCorner = style.value(PName.RoundCorner).asInt(false);
		this.diagonalCorner = style.value(PName.DiagonalCorner).asInt(false);
		final SymbolContext biColor = style.getSymbolContext(getIHtmlColorSet());
		this.stroke = style.getStroke();

		this.padding = padding;
		this.minWidth = minWidth;
		this.collections = collections;
		this.back = biColor.getBackColor();
		this.deltaShadow = biColor.getDeltaShadow();
		this.foregroundColor = biColor.getForeColor();
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(UTranslate.dx(padding));
		if (foregroundColor != null)
			ug = ug.apply(foregroundColor);
		if (back != null)
			ug = ug.apply(back.bg());
		ug = ug.apply(stroke);
		final Shadowable rect = new URectangle(getTextWidth(stringBounder), getTextHeight(stringBounder))
				.rounded(roundCorner).diagonalCorner(diagonalCorner);
		rect.setDeltaShadow(deltaShadow);
		if (collections) {
			ug.apply(UTranslate.dx(getDeltaCollection())).draw(rect);
			ug = ug.apply(UTranslate.dy(getDeltaCollection()));
		}
		ug.draw(rect);
		ug = ug.apply(new UStroke());
		final TextBlock textBlock = getTextBlock();
		textBlock.drawU(ug.apply(new UTranslate(getMarginX1() + suppWidth(stringBounder) / 2, getMarginY())));
	}

	private double getDeltaCollection() {
		if (collections)
			return 4;

		return 0;
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + deltaShadow + 1 + getDeltaCollection();
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + deltaShadow + getDeltaCollection() + 2 * padding;
	}

	@Override
	protected double getPureTextWidth(StringBounder stringBounder) {
		return Math.max(super.getPureTextWidth(stringBounder), minWidth);
	}

	private final double suppWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) - super.getPureTextWidth(stringBounder);
	}

}

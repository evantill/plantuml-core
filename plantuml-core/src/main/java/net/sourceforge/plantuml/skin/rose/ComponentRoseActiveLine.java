package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.klimt.Fashion;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColorSet;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.Style;

public class ComponentRoseActiveLine extends AbstractComponent {

	private final Fashion symbolContext;
	private final boolean closeUp;
	private final boolean closeDown;

	public ComponentRoseActiveLine(Style style, boolean closeUp, boolean closeDown, HColorSet set) {
		super(style);
		this.symbolContext = style.getSymbolContext(set);
		this.closeUp = closeUp;
		this.closeDown = closeDown;
	}

	protected void drawInternalU(UGraphic ug, Area area) {
		final XDimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		final int x = (int) (dimensionToUse.getWidth() - getPreferredWidth(stringBounder)) / 2;

		if (dimensionToUse.getHeight() == 0) {
			return;
		}

		final URectangle rect = new URectangle(getPreferredWidth(stringBounder), dimensionToUse.getHeight());
		if (symbolContext.isShadowing()) {
			rect.setDeltaShadow(1);
		}
		ug = ug.apply(symbolContext.getForeColor());
		if (closeUp && closeDown) {
			ug.apply(symbolContext.getBackColor().bg()).apply(UTranslate.dx(x)).draw(rect);
			return;
		}
		ug.apply(symbolContext.getBackColor().bg()).apply(symbolContext.getBackColor()).apply(UTranslate.dx(x))
				.draw(rect);

		final ULine vline = ULine.vline(dimensionToUse.getHeight());
		ug.apply(UTranslate.dx(x)).draw(vline);
		ug.apply(UTranslate.dx(x + getPreferredWidth(stringBounder))).draw(vline);

		final ULine hline = ULine.hline(getPreferredWidth(stringBounder));
		if (closeUp) {
			ug.apply(UTranslate.dx(x)).draw(hline);
		}
		if (closeDown) {
			ug.apply(new UTranslate(x, dimensionToUse.getHeight())).draw(hline);
		}
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 10;
	}
}

package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.ULine;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class ActorStickMan extends AbstractTextBlock implements TextBlock {

	private final double armsY = 8;
	private final double armsLenght = 13;
	private final double bodyLenght = 27;
	private final double legsX = 13;
	private final double legsY = 15;
	private final double headDiam = 16;

	private final SymbolContext symbolContext;
	private final boolean actorBusiness;

	ActorStickMan(SymbolContext symbolContext, boolean actorBusiness) {
		this.symbolContext = symbolContext;
		this.actorBusiness = actorBusiness;
	}

	public void drawU(UGraphic ug) {

		final double startX = Math.max(armsLenght, legsX) - headDiam / 2.0 + thickness();

		final UEllipse head = new UEllipse(headDiam, headDiam);
		final double centerX = startX + headDiam / 2;

		final UPath path = new UPath();
		path.moveTo(0, 0);
		path.lineTo(0, bodyLenght);
		path.moveTo(-armsLenght, armsY);
		path.lineTo(armsLenght, armsY);
		path.moveTo(0, bodyLenght);
		path.lineTo(-legsX, bodyLenght + legsY);
		path.moveTo(0, bodyLenght);
		path.lineTo(legsX, bodyLenght + legsY);
		if (symbolContext.getDeltaShadow() != 0) {
			head.setDeltaShadow(symbolContext.getDeltaShadow());
			path.setDeltaShadow(symbolContext.getDeltaShadow());
		}

		ug = symbolContext.apply(ug);
		ug.apply(new UTranslate(startX, thickness())).draw(head);
		if (actorBusiness) {
			specialBusiness(ug.apply(new UTranslate(startX + headDiam / 2, thickness() + headDiam / 2)));
		}
		ug.apply(new UTranslate(centerX, headDiam + thickness())).apply(HColors.none().bg()).draw(path);
	}

	private void specialBusiness(UGraphic ug) {
		final double alpha = 21 * Math.PI / 64;
		final XPoint2D p1 = getOnCircle(Math.PI / 4 + alpha);
		final XPoint2D p2 = getOnCircle(Math.PI / 4 - alpha);
		ug = ug.apply(new UTranslate(p1));
		ug.draw(new ULine(p2.getX() - p1.getX(), p2.getY() - p1.getY()));
	}

	private XPoint2D getOnCircle(double alpha) {
		final double x = headDiam / 2 * Math.cos(alpha);
		final double y = headDiam / 2 * Math.sin(alpha);
		return new XPoint2D(x, y);
	}

	private double thickness() {
		return symbolContext.getStroke().getThickness();
	}

	public double getPreferredWidth() {
		return Math.max(armsLenght, legsX) * 2 + 2 * thickness();
	}

	public double getPreferredHeight() {
		return headDiam + bodyLenght + legsY + 2 * thickness() + symbolContext.getDeltaShadow() + 1;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(getPreferredWidth(), getPreferredHeight());
	}
}

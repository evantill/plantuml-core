package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class SymbolContext {

	private final HColor backColor;
	private final HColor foreColor;
	private final UStroke stroke;
	private final double deltaShadow;
	private final double roundCorner;
	private final double diagonalCorner;

	private SymbolContext(HColor backColor, HColor foreColor, UStroke stroke, double deltaShadow, double roundCorner,
			double diagonalCorner) {
		this.backColor = backColor;
		this.foreColor = foreColor;
		this.stroke = stroke;
		this.deltaShadow = deltaShadow;
		this.roundCorner = roundCorner;
		this.diagonalCorner = diagonalCorner;
	}

	@Override
	public String toString() {
		return super.toString() + " backColor=" + backColor + " foreColor=" + foreColor;
	}

	final public UGraphic apply(UGraphic ug) {
		ug = applyColors(ug);
		ug = applyStroke(ug);
		return ug;
	}

	public UGraphic applyColors(UGraphic ug) {
		if (foreColor == null)
			ug = ug.apply(HColors.none());
		else
			ug = ug.apply(foreColor);
		if (backColor == null)
			ug = ug.apply(HColors.none().bg());
		else
			ug = ug.apply(backColor.bg());

		return ug;
	}

	public UGraphic applyStroke(UGraphic ug) {
		return ug.apply(stroke);
	}

	public SymbolContext(HColor backColor, HColor foreColor) {
		this(backColor, foreColor, new UStroke(), 0, 0, 0);
	}

	public SymbolContext withShadow(double deltaShadow2) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow2, roundCorner, diagonalCorner);
	}

	public SymbolContext withDeltaShadow(double deltaShadow2) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow2, roundCorner, diagonalCorner);
	}

	public SymbolContext withStroke(UStroke newStroke) {
		return new SymbolContext(backColor, foreColor, newStroke, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withBackColor(HColor backColor) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withForeColor(HColor foreColor) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withCorner(double roundCorner, double diagonalCorner) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow, roundCorner, diagonalCorner);
	}

	public HColor getBackColor() {
		return backColor;
	}

	public HColor getForeColor() {
		return foreColor;
	}

	public UStroke getStroke() {
		return stroke;
	}

	public boolean isShadowing() {
		return deltaShadow > 0;
	}

	public double getDeltaShadow() {
		return deltaShadow;
	}

	public double getRoundCorner() {
		return roundCorner;
	}

	public double getDiagonalCorner() {
		return diagonalCorner;
	}

}

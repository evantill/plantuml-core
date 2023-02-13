package net.sourceforge.plantuml.nwdiag.next;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class GridTextBlockSimple implements TextBlock {

	public static final double MINIMUM_WIDTH = 70;

	protected final NwArray data;
	private final ISkinParam skinParam;

	public GridTextBlockSimple(int lines, int cols, ISkinParam skinParam) {
		this.skinParam = skinParam;
		this.data = new NwArray(lines, cols);
	}

	protected void drawGrid(UGraphic ug) {
	}

	public void drawU(UGraphic ug) {
		drawGrid(ug);
		final StringBounder stringBounder = ug.getStringBounder();
		double y = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data.getNbCols(); j++) {
				final double colWidth = colWidth(stringBounder, j);
				if (data.get(i, j) != null)
					data.get(i, j).drawMe(ug.apply(new UTranslate(x, y)), colWidth, lineHeight);

				x += colWidth;
			}
			y += lineHeight;
		}
	}

	protected double colWidth(StringBounder stringBounder, final int j) {
		double width = 0;
		for (int i = 0; i < data.getNbLines(); i++)
			if (data.get(i, j) != null)
				width = Math.max(width, data.get(i, j).naturalDimension(stringBounder).getWidth());

		return width;
	}

	public double lineHeight(StringBounder stringBounder, final int i) {
		double height = 50;
		for (int j = 0; j < data.getNbCols(); j++)
			if (data.get(i, j) != null)
				height = Math.max(height, data.get(i, j).naturalDimension(stringBounder).getHeight());

		return height;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		if (data.getNbLines() == 0)
			return new XDimension2D(0, 0);

		double height = 0;
		for (int i = 0; i < data.getNbLines(); i++)
			height += lineHeight(stringBounder, i);

		double width = 0;
		for (int j = 0; j < data.getNbCols(); j++)
			width += colWidth(stringBounder, j);

		return new XDimension2D(Math.max(MINIMUM_WIDTH, width), height);
	}

	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		throw new UnsupportedOperationException("member=" + member + " " + getClass().toString());
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		throw new UnsupportedOperationException(getClass().toString());
	}

	public void add(int i, int j, NServerDraw value) {
		data.set(i, j, value);
	}

	protected final ISkinParam getSkinParam() {
		return skinParam;
	}

}

package net.sourceforge.plantuml.salt.element;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.ULine;
import net.sourceforge.plantuml.klimt.URectangle;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.salt.Cell;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Grid {

	private final double[] rowsStart;
	private final double[] colsStart;
	private final TableStrategy strategy;
	private final TextBlock title;

	private final Set<Segment> horizontals = new HashSet<>();
	private final Set<Segment> verticals = new HashSet<>();

	public Grid(double[] rowsStart, double[] colsStart, TableStrategy strategy, TextBlock title) {
		this.title = title;
		this.rowsStart = rowsStart;
		this.colsStart = colsStart;
		this.strategy = strategy;
		if (strategy == TableStrategy.DRAW_OUTSIDE || strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE
				|| strategy == TableStrategy.DRAW_ALL) {
			addOutside();
		}
	}

	private void addOutside() {
		final int nbRow = rowsStart.length;
		final int nbCol = colsStart.length;
		for (int c = 0; c < nbCol - 1; c++) {
			horizontals.add(new Segment(0, c));
			horizontals.add(new Segment(nbRow - 1, c));
		}
		for (int r = 0; r < nbRow - 1; r++) {
			verticals.add(new Segment(r, 0));
			verticals.add(new Segment(r, nbCol - 1));
		}

	}

	public void drawU(UGraphic ug, double x, double y, HColor white) {
		// Hlines
		for (Segment seg : horizontals) {
			final int row1 = seg.getRow();
			final int col1 = seg.getCol();
			final double width = colsStart[col1 + 1] - colsStart[col1];
			ug.apply(new UTranslate(x + colsStart[col1], y + rowsStart[row1])).draw(ULine.hline(width));
		}
		// Vlines
		for (Segment seg : verticals) {
			final int row1 = seg.getRow();
			final int col1 = seg.getCol();
			final double height = rowsStart[row1 + 1] - rowsStart[row1];
			ug.apply(new UTranslate(x + colsStart[col1], y + rowsStart[row1])).draw(ULine.vline(height));
		}

		final XDimension2D dim = title.calculateDimension(ug.getStringBounder());

		if (dim.getWidth() > 0 && dim.getHeight() > 0) {
			final UGraphic ug2 = ug.apply(new UTranslate(x + 6, y - dim.getHeight() * 0));
			ug2.apply(white.bg()).apply(white).draw(new URectangle(dim));
			title.drawU(ug2);
		}

	}

	public void addCell(Cell cell) {

		if (strategy == TableStrategy.DRAW_NONE || strategy == TableStrategy.DRAW_OUTSIDE
				|| strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE) {
			return;
		}

		if (strategy == TableStrategy.DRAW_HORIZONTAL || strategy == TableStrategy.DRAW_ALL) {
			// Hlines
			for (int c = cell.getMinCol(); c <= cell.getMaxCol(); c++) {
				horizontals.add(new Segment(cell.getMinRow(), c));
				horizontals.add(new Segment(cell.getMaxRow() + 1, c));
			}
		}
		if (strategy == TableStrategy.DRAW_VERTICAL || strategy == TableStrategy.DRAW_ALL) {
			// Vlines
			for (int r = cell.getMinRow(); r <= cell.getMaxRow(); r++) {
				verticals.add(new Segment(r, cell.getMinCol()));
				verticals.add(new Segment(r, cell.getMaxCol() + 1));
			}
		}
	}
}

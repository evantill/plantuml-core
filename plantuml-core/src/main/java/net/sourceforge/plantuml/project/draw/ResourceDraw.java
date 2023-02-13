package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.klimt.ULine;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.Resource;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class ResourceDraw implements UDrawable {

	private final Resource res;
	private final TimeScale timeScale;
	private final double y;
	private final Day min;
	private final Day max;
	private final GanttDiagram gantt;

	public ResourceDraw(GanttDiagram gantt, Resource res, TimeScale timeScale, double y, Day min, Day max) {
		this.res = res;
		this.timeScale = timeScale;
		this.y = y;
		this.min = min;
		this.max = max;
		this.gantt = gantt;
	}

	public void drawU(UGraphic ug) {
		final TextBlock title = Display.getWithNewlines(res.getName()).create(getFontConfiguration(13),
				HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		title.drawU(ug);
		final ULine line = ULine.hline(timeScale.getEndingPosition(max) - timeScale.getStartingPosition(min));
		ug.apply(HColors.BLACK).apply(UTranslate.dy(title.calculateDimension(ug.getStringBounder()).getHeight()))
				.draw(line);

		double startingPosition = -1;
		int totalLoad = 0;
		int totalLimit = 0;
		for (Day i = min; i.compareTo(max) <= 0; i = i.increment()) {
			final boolean isBreaking = timeScale.isBreaking(i);
			totalLoad += gantt.getLoadForResource(res, i);
			totalLimit += 100;
			if (isBreaking) {
				if (totalLoad > 0) {
					final boolean over = totalLoad > totalLimit;
					final FontConfiguration fontConfiguration = getFontConfiguration(9,
							over ? HColors.RED : HColors.BLACK);
					final TextBlock value = Display.getWithNewlines("" + totalLoad).create(fontConfiguration,
							HorizontalAlignment.LEFT, new SpriteContainerEmpty());
					if (startingPosition == -1)
						startingPosition = timeScale.getStartingPosition(i);
					final double endingPosition = timeScale.getEndingPosition(i);
					final double start = (startingPosition + endingPosition) / 2
							- value.calculateDimension(ug.getStringBounder()).getWidth() / 2;
					value.drawU(ug.apply(new UTranslate(start, 16)));
				}
				startingPosition = -1;
				totalLoad = 0;
				totalLimit = 0;
			} else {
				if (startingPosition == -1)
					startingPosition = timeScale.getStartingPosition(i);
			}
		}

	}

	private FontConfiguration getFontConfiguration(int size) {
		return getFontConfiguration(size, HColors.BLACK);
	}

	private FontConfiguration getFontConfiguration(int size, HColor color) {
		final UFont font = UFont.serif(size);
		return FontConfiguration.create(font, color, color, null);
	}

	public double getHeight() {
		return 16 * 2;
	}

	public final double getY() {
		return y;
	}

}

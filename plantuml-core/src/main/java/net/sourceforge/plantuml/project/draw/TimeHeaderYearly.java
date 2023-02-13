package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.project.TimeHeaderParameters;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.MonthYear;
import net.sourceforge.plantuml.project.timescale.TimeScaleCompressed;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class TimeHeaderYearly extends TimeHeaderCalendar {

	public double getTimeHeaderHeight() {
		return 20;
	}

	public double getTimeFooterHeight() {
		return 20 - 1;
	}

	public TimeHeaderYearly(TimeHeaderParameters thParam) {
		super(thParam, new TimeScaleCompressed(thParam.getStartingDay(), thParam.getScale()));
	}

	@Override
	public void drawTimeHeader(final UGraphic ug, double totalHeightWithoutFooter) {
		drawTextsBackground(ug, totalHeightWithoutFooter);
		drawYears(ug);
		drawHline(ug, 0);
		drawHline(ug, getFullHeaderHeight());
	}

	@Override
	public void drawTimeFooter(UGraphic ug) {
		ug = ug.apply(UTranslate.dy(3));
		drawYears(ug);
		drawHline(ug, 0);
		drawHline(ug, getTimeFooterHeight());
	}

	private void drawYears(final UGraphic ug) {
		MonthYear last = null;
		double lastChange = -1;
		for (Day wink = min; wink.compareTo(max) < 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			if (last == null || wink.monthYear().year() != last.year()) {
				drawVbar(ug, x1, 0, 19, false);
				if (last != null) {
					printYear(ug, last, lastChange, x1);
				}
				lastChange = x1;
				last = wink.monthYear();
			}
		}
		final double x1 = getTimeScale().getStartingPosition(max.increment());
		if (x1 > lastChange) {
			printYear(ug, last, lastChange, x1);
		}
		drawVbar(ug, getTimeScale().getEndingPosition(max), 0, 19, false);
	}

	private void printYear(UGraphic ug, MonthYear monthYear, double start, double end) {
		final TextBlock small = getTextBlock("" + monthYear.year(), 14, true, openFontColor());
		printCentered(ug, true, start, end, small);
	}

	@Override
	public double getFullHeaderHeight() {
		return getTimeHeaderHeight();
	}

}

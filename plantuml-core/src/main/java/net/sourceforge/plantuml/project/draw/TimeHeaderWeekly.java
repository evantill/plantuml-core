package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.project.TimeHeaderParameters;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.MonthYear;
import net.sourceforge.plantuml.project.time.WeekNumberStrategy;
import net.sourceforge.plantuml.project.timescale.TimeScaleCompressed;

public class TimeHeaderWeekly extends TimeHeaderCalendar {

	private final WeekNumberStrategy weekNumberStrategy;
	private final boolean withCalendarDate;

	public double getTimeHeaderHeight() {
		return 16 + 13;
	}

	public double getTimeFooterHeight() {
		return 16;
	}

	public TimeHeaderWeekly(TimeHeaderParameters thParam, WeekNumberStrategy weekNumberStrategy,
			boolean withCalendarDate) {
		super(thParam, new TimeScaleCompressed(thParam.getStartingDay(), thParam.getScale()));
		this.weekNumberStrategy = weekNumberStrategy;
		this.withCalendarDate = withCalendarDate;
	}

	@Override
	public void drawTimeHeader(final UGraphic ug, double totalHeightWithoutFooter) {
		drawTextsBackground(ug, totalHeightWithoutFooter);
		drawCalendar(ug, totalHeightWithoutFooter);
		drawHline(ug, 0);
		drawHline(ug, Y_POS_ROW16());
		drawHline(ug, getFullHeaderHeight());
	}

	@Override
	public void drawTimeFooter(UGraphic ug) {
		drawHline(ug, 0);
		printMonths(ug);
		drawHline(ug, getTimeFooterHeight());
	}

	private void drawCalendar(final UGraphic ug, double totalHeightWithoutFooter) {
		printDaysOfMonth(ug);
		printSmallVbars(ug, totalHeightWithoutFooter);
		printMonths(ug);
	}

	private void printMonths(final UGraphic ug) {
		MonthYear last = null;
		double lastChangeMonth = -1;
		for (Day wink = min; wink.compareTo(max) < 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			if (wink.monthYear().equals(last) == false) {
				drawVbar(ug, x1, 0, Y_POS_ROW16(), false);
				if (last != null) {
					printMonth(ug, last, lastChangeMonth, x1);
				}
				lastChangeMonth = x1;
				last = wink.monthYear();
			}
		}
		drawVbar(ug, getTimeScale().getEndingPosition(max), 0, Y_POS_ROW16(), false);
		final double x1 = getTimeScale().getStartingPosition(max.increment());
		if (x1 > lastChangeMonth) {
			printMonth(ug, last, lastChangeMonth, x1);
		}
	}

	private void printSmallVbars(final UGraphic ug, double totalHeightWithoutFooter) {
		for (Day wink = min; wink.compareTo(max) <= 0; wink = wink.increment()) {
			if (wink.getDayOfWeek() == weekNumberStrategy.getFirstDayOfWeek()) {
				drawVbar(ug, getTimeScale().getStartingPosition(wink), Y_POS_ROW16(), totalHeightWithoutFooter, false);
			}
		}
		drawVbar(ug, getTimeScale().getEndingPosition(max), Y_POS_ROW16(), totalHeightWithoutFooter, false);
	}

	private void printDaysOfMonth(final UGraphic ug) {
		for (Day wink = min; wink.compareTo(max) < 0; wink = wink.increment()) {
			if (wink.getDayOfWeek() == weekNumberStrategy.getFirstDayOfWeek()) {
				final String num;
				if (withCalendarDate)
					num = "" + wink.getDayOfMonth();
				else
					num = "" + wink.getWeekOfYear(weekNumberStrategy);
				final TextBlock textBlock = getTextBlock(num, 10, false, openFontColor());
				printLeft(ug.apply(UTranslate.dy(Y_POS_ROW16())), textBlock,
						getTimeScale().getStartingPosition(wink) + 5);
			}
		}
	}

	private void printMonth(UGraphic ug, MonthYear monthYear, double start, double end) {
		final TextBlock small = getTextBlock(monthYear.shortName(locale()), 12, true, openFontColor());
		final TextBlock big = getTextBlock(monthYear.shortNameYYYY(locale()), 12, true, openFontColor());
		printCentered(ug, false, start, end, small, big);
	}

	private void printLeft(UGraphic ug, TextBlock text, double start) {
		text.drawU(ug.apply(UTranslate.dx(start)));
	}

	@Override
	public double getFullHeaderHeight() {
		return getTimeHeaderHeight();
	}

}

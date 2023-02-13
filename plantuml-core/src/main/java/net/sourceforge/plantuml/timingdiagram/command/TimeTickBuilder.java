package net.sourceforge.plantuml.timingdiagram.command;

import java.math.BigDecimal;

import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.timingdiagram.Clocks;
import net.sourceforge.plantuml.timingdiagram.TimeTick;
import net.sourceforge.plantuml.timingdiagram.TimingFormat;

public class TimeTickBuilder {

	public static IRegex expressionAtWithoutArobase(String name) {
		return new RegexOr( //
				new RegexLeaf(name + "CODE", ":([%pLN_.]+)([-+]\\d+)?"), //
				new RegexLeaf(name + "DATE", "(\\d+)/(\\d+)/(\\d+)"), //
				new RegexLeaf(name + "HOUR", "(\\d+):(\\d+):(\\d+)"), //
				new RegexLeaf(name + "DIGIT", "(\\+?)(-?\\d+\\.?\\d*)"), //
				new RegexLeaf(name + "CLOCK", "([%pLN_.@]+)\\*(\\d+)"));
	}

	public static IRegex expressionAtWithArobase(String name) {
		return new RegexConcat( //
				new RegexLeaf("@"), //
				expressionAtWithoutArobase(name));
	}

	public static IRegex optionalExpressionAtWithArobase(String name) {
		return new RegexOptional(expressionAtWithArobase(name));
	}

	public static TimeTick parseTimeTick(String name, RegexResult arg, Clocks clock) {
		final String code = arg.get(name + "CODE", 0);
		if (code != null) {
			final String delta = arg.get(name + "CODE", 1);
			TimeTick result = clock.getCodeValue(code);
			if (delta == null)
				return result;

			final BigDecimal value = result.getTime().add(new BigDecimal(delta));
			return new TimeTick(value, TimingFormat.DECIMAL);
		}
		final String clockName = arg.get(name + "CLOCK", 0);
		if (clockName != null) {
			final int number = Integer.parseInt(arg.get(name + "CLOCK", 1));
			return clock.getClockValue(clockName, number);
		}
		final String hour = arg.get(name + "HOUR", 0);
		if (hour != null) {
			final int h = Integer.parseInt(arg.get(name + "HOUR", 0));
			final int m = Integer.parseInt(arg.get(name + "HOUR", 1));
			final int s = Integer.parseInt(arg.get(name + "HOUR", 2));
			final BigDecimal value = new BigDecimal(3600 * h + 60 * m + s);
			return new TimeTick(value, TimingFormat.HOUR);
		}
		final String date = arg.get(name + "DATE", 0);
		if (date != null) {
			final int yy = Integer.parseInt(arg.get(name + "DATE", 0));
			final int mm = Integer.parseInt(arg.get(name + "DATE", 1));
			final int dd = Integer.parseInt(arg.get(name + "DATE", 2));

			return TimingFormat.createDate(yy, mm, dd, clock.getTimingFormatDate());
		}
		final String number = arg.get(name + "DIGIT", 1);
		if (number == null)
			return clock.getNow();

		final boolean isRelative = "+".equals(arg.get(name + "DIGIT", 0));
		BigDecimal value = new BigDecimal(number);
		if (isRelative && clock.getNow() != null)
			value = clock.getNow().getTime().add(value);

		return new TimeTick(value, TimingFormat.DECIMAL);
	}

}

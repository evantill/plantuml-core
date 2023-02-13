package net.sourceforge.plantuml.sequencediagram.command;

import java.text.DecimalFormat;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.sequencediagram.DottedNumber;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandAutonumber extends SingleLineCommand2<SequenceDiagram> {

	public CommandAutonumber() {
		super(getConcat());
	}

	private static RegexConcat getConcat() {
		return RegexConcat.build(CommandAutonumber.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("autonumber"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("START", "(\\d(?:(?:[^%pLN%s]+|\\d+)*\\d)?)?"), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("STEP", "(\\d+)") //
						)), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("FORMAT", "[%g]([^%g]+)[%g]") //
						)), //
				RegexLeaf.spaceZeroOrMore(), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, LineLocation location, RegexResult arg) {
		DottedNumber start = DottedNumber.create("1");
		final String arg0 = arg.get("START", 0);
		// System.err.println("arg0=" + arg0);
		if (arg0 != null) {
			start = DottedNumber.create(arg0);
		}
		// System.err.println("start=" + start);
		int inc = 1;
		final String arg1 = arg.get("STEP", 0);
		if (arg1 != null) {
			inc = Integer.parseInt(arg1);
		}

		final String arg2 = arg.get("FORMAT", 0);
		final String df = arg2 == null ? "<b>0</b>" : arg2;
		final DecimalFormat decimalFormat;
		try {
			decimalFormat = new DecimalFormat(df);
		} catch (IllegalArgumentException e) {
			return CommandExecutionResult.error("Error in pattern : " + df);
		}

		diagram.autonumberGo(start, inc, decimalFormat);
		return CommandExecutionResult.ok();
	}

}

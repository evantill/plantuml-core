package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandCircleSpot3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandCircleSpot3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCircleSpot3.class.getName(), RegexLeaf.start(), //
				ColorParser.exp4(), //
				new RegexLeaf("SPOT", "\\((\\S)\\)"), //
				new RegexLeaf(";?"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		String s = arg.get("COLOR", 0);
		final HColor color = s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(s);
		diagram.addSpot(arg.get("SPOT", 0), color);
		return CommandExecutionResult.ok();
	}

}

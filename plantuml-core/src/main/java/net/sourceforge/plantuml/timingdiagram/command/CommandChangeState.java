package net.sourceforge.plantuml.timingdiagram.command;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.timingdiagram.Player;
import net.sourceforge.plantuml.timingdiagram.TimeTick;
import net.sourceforge.plantuml.timingdiagram.TimingDiagram;

abstract class CommandChangeState extends SingleLineCommand2<TimingDiagram> {

	CommandChangeState(IRegex pattern) {
		super(pattern);
	}

	static final String STATE_CODE = "([-%pLN_][-%pLN_.]*)";

	static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	protected CommandExecutionResult addState(TimingDiagram diagram, RegexResult arg, final Player player,
			final TimeTick now) throws NoSuchColorException {
		final String comment = arg.get("COMMENT", 0);
		final Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		player.setState(now, comment, colors, getStates(arg));
		return CommandExecutionResult.ok();
	}

	private String[] getStates(RegexResult arg) {
		if (arg.get("STATE7", 0) != null) {
			final String state1 = arg.get("STATE7", 0);
			final String state2 = arg.get("STATE7", 1);
			return new String[] { state1, state2 };
		}
		return new String[] { arg.getLazzy("STATE", 0) };
	}

	static IRegex getStateOrHidden() {
		return new RegexOr(//
				new RegexLeaf("STATE1", "[%g]([^%g]*)[%g]"), //
				new RegexLeaf("STATE2", STATE_CODE), //
				new RegexLeaf("STATE3", "(\\{hidden\\})"), //
				new RegexLeaf("STATE4", "(\\{\\.\\.\\.\\})"), //
				new RegexLeaf("STATE5", "(\\{-\\})"), //
				new RegexLeaf("STATE6", "(\\{\\?\\})"), //
				new RegexLeaf("STATE7", "(?:\\{" + STATE_CODE + "," + STATE_CODE + "\\})") //
		);
	}

}

package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandNote3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandNote3() {
		super(getRegexConcat());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandNote3.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(note|floating note)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("POSITION", "(left|right)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		final Display note = Display.getWithNewlines(arg.get("NOTE", 0));
		final NotePosition position = NotePosition.defaultLeft(arg.get("POSITION", 0));
		final NoteType type = NoteType.defaultType(arg.get("TYPE", 0));
		return diagram.addNote(note, position, type, colors);
	}

}

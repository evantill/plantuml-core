package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
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
import net.sourceforge.plantuml.utils.BlocLines;

public class CommandNoteLong3 extends CommandMultilines2<ActivityDiagram3> {

	public CommandNoteLong3() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	public String getPatternEnd() {
		return "^end[%s]?note$";
	}

	@Override
	protected CommandExecutionResult executeNow(final ActivityDiagram3 diagram, BlocLines lines)
			throws NoSuchColorException {
		// final List<? extends CharSequence> in =
		// StringUtils.removeEmptyColumns2(lines.subList(1, lines.size() - 1));
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		lines = lines.subExtract(1, 1);
		lines = lines.removeEmptyColumns();
		final NotePosition position = NotePosition.defaultLeft(line0.get("POSITION", 0));
		final NoteType type = NoteType.defaultType(line0.get("TYPE", 0));
		final Display note = lines.toDisplay();
		final Colors colors = color().getColor(line0, diagram.getSkinParam().getIHtmlColorSet());
		return diagram.addNote(note, position, type, colors);
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandNoteLong3.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(note|floating note)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("POSITION", "(left|right)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.end());
	}
}

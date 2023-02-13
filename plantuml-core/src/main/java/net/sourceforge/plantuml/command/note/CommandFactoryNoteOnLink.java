package net.sourceforge.plantuml.command.note;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.baraye.CucaDiagram;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.cucadiagram.CucaNote;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.url.UrlBuilder;
import net.sourceforge.plantuml.url.UrlMode;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.LineLocation;

public final class CommandFactoryNoteOnLink implements SingleMultiFactoryCommand<CucaDiagram> {

	private IRegex getRegexConcatSingleLine() {
		return RegexConcat.build(CommandFactoryNoteOnLink.class.getName() + "single", RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("(on|of)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("link"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), RegexLeaf.end());
	}

	private IRegex getRegexConcatMultiLine() {
		return RegexConcat.build(CommandFactoryNoteOnLink.class.getName() + "multi", RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("(on|of)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("link"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	public Command<CucaDiagram> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<CucaDiagram>(getRegexConcatMultiLine(), MultilinesStrategy.KEEP_STARTING_QUOTE, Trim.BOTH) {

			@Override
			public String getPatternEnd() {
				return "^end[%s]?note$";
			}

			protected CommandExecutionResult executeNow(final CucaDiagram system, BlocLines lines)
					throws NoSuchColorException {
				final String line0 = lines.getFirst().getTrimmed().getString();
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();
				if (lines.size() > 0) {
					final RegexResult arg = getStartingPattern().matcher(line0);
					return executeInternal(system, lines, arg);
				}
				return CommandExecutionResult.error("No note defined");
			}

		};
	}

	public Command<CucaDiagram> createSingleLine() {
		return new SingleLineCommand2<CucaDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final CucaDiagram system, LineLocation location,
					RegexResult arg) throws NoSuchColorException {
				final BlocLines note = BlocLines.getWithNewlines(arg.get("NOTE", 0));
				return executeInternal(system, note, arg);
			}
		};
	}

	private CommandExecutionResult executeInternal(CucaDiagram diagram, BlocLines note, final RegexResult arg)
			throws NoSuchColorException {
		final Link link = diagram.getLastLink();
		if (link == null)
			return CommandExecutionResult.error("No link defined");

		Position position = Position.BOTTOM;
		if (arg.get("POSITION", 0) != null)
			position = Position.valueOf(StringUtils.goUpperCase(arg.get("POSITION", 0)));

		Url url = null;
		if (arg.get("URL", 0) != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			url = urlBuilder.getUrl(arg.get("URL", 0));
		}
		final Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		link.addNote(CucaNote.build(note.toDisplay(), position, colors));
		return CommandExecutionResult.ok();
	}

}

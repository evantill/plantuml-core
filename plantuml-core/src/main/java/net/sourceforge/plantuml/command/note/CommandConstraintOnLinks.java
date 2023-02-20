package net.sourceforge.plantuml.command.note;

import java.util.List;

import net.atmp.Link;
import net.sourceforge.plantuml.baraye.CucaDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.LineLocation;

public final class CommandConstraintOnLinks extends SingleLineCommand2<CucaDiagram> {

	public CommandConstraintOnLinks() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandConstraintOnLinks.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("constraint"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("on"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("links"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeArg(CucaDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final List<Link> links = diagram.getTwoLastLinks();
		if (links == null) {
			return CommandExecutionResult.error("Cannot put constraint on two last links");
		}
		final BlocLines note = BlocLines.getWithNewlines(arg.get("NOTE", 0));
		return diagram.constraintOnLinks(links.get(0), links.get(1), note.toDisplay());
	}

}

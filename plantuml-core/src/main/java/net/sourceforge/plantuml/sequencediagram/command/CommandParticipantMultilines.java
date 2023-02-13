package net.sourceforge.plantuml.sequencediagram.command;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantType;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.url.UrlBuilder;
import net.sourceforge.plantuml.url.UrlMode;
import net.sourceforge.plantuml.utils.BlocLines;

public class CommandParticipantMultilines extends CommandMultilines2<SequenceDiagram> {

	public CommandParticipantMultilines() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	@Override
	public String getPatternEnd() {
		return "^([^\\[\\]]*)\\]$";
	}

	private static RegexConcat getRegexConcat() {
		return RegexConcat.build(CommandParticipantMultilines.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(participant)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", "([%pLN_.@]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				CommandParticipant.getOrderRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				UrlBuilder.OPTIONAL, //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\["), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeNow(SequenceDiagram diagram, BlocLines lines) throws NoSuchColorException {

		final RegexResult arg = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());

		final String code = arg.get("CODE", 0);
		if (diagram.participantsContainsKey(code)) {
			diagram.putParticipantInLast(code);
			return CommandExecutionResult.ok();
		}

		lines = lines.subExtract(1, 1);
		lines = lines.removeEmptyColumns();
		final Display strings = lines.toDisplay();

		final ParticipantType type = ParticipantType.PARTICIPANT;
		final boolean create = false;
		final String orderString = arg.get("ORDER", 0);
		final int order = orderString == null ? 0 : Integer.parseInt(orderString);

		final Participant participant = diagram.createNewParticipant(type, code, strings, order);

		final String stereotype = arg.get("STEREO", 0);

		if (stereotype != null) {
			final ISkinParam skinParam = diagram.getSkinParam();
			final boolean stereotypePositionTop = skinParam.stereotypePositionTop();
			final UFont font = skinParam.getFont(null, false, FontParam.CIRCLED_CHARACTER);
			participant.setStereotype(Stereotype.build(stereotype, skinParam.getCircledCharacterRadius(), font,
					diagram.getSkinParam().getIHtmlColorSet()), stereotypePositionTop);
		}

		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			participant.setUrl(url);
		}

		return CommandExecutionResult.ok();
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

}

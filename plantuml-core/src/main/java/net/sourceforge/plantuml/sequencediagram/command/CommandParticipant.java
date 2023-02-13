package net.sourceforge.plantuml.sequencediagram.command;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantType;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.url.UrlBuilder;
import net.sourceforge.plantuml.url.UrlMode;
import net.sourceforge.plantuml.utils.LineLocation;

public abstract class CommandParticipant extends SingleLineCommand2<SequenceDiagram> {

	public CommandParticipant(IRegex pattern) {
		super(pattern);
	}

	static IRegex getOrderRegex() {
		return new RegexOptional( //
				new RegexConcat( //
						new RegexLeaf("order"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("ORDER", "(-?\\d{1,7})") //
				));
	}

	static IRegex getRegexType() {
		return new RegexOr(//
				new RegexLeaf("TYPE", "(participant|actor|create|boundary|control|entity|queue|database|collections)"), //
				new RegexLeaf("CREATE",
						"create[%s](participant|actor|boundary|control|entity|queue|database|collections)"));
	}

	@Override
	final protected CommandExecutionResult executeArg(SequenceDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String code = arg.get("CODE", 0);
		if (diagram.participantsContainsKey(code)) {
			diagram.putParticipantInLast(code);
			return CommandExecutionResult.ok();
		}

		Display strings = Display.NULL;
		if (arg.get("FULL", 0) != null)
			strings = Display.getWithNewlines(arg.get("FULL", 0));

		final String typeString1 = arg.get("TYPE", 0);
		final String typeCreate1 = arg.get("CREATE", 0);
		final ParticipantType type;
		final boolean create;
		if (typeCreate1 != null) {
			type = ParticipantType.valueOf(StringUtils.goUpperCase(typeCreate1));
			create = true;
		} else if (typeString1.equalsIgnoreCase("CREATE")) {
			type = ParticipantType.PARTICIPANT;
			create = true;
		} else {
			type = ParticipantType.valueOf(StringUtils.goUpperCase(typeString1));
			create = false;
		}
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
		final String s = arg.get("COLOR", 0);
		participant.setSpecificColorTOBEREMOVED(ColorType.BACK, s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(s));

		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			participant.setUrl(url);
		}

		if (create) {
			final String error = diagram.activate(participant, LifeEventType.CREATE, null);
			if (error != null)
				return CommandExecutionResult.error(error);

		}

		return CommandExecutionResult.ok();
	}

}

package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandBackward3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandBackward3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandBackward3.class.getName(), RegexLeaf.start(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("\\("), //
						new RegexOptional(new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("INCOMING_COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
						new RegexLeaf("INCOMING", "(.*?)"), //
						new RegexLeaf("\\)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("backward"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				new RegexLeaf("LABEL", "(.*?)"), //
				new RegexLeaf("STYLE", CommandActivity3.endingGroup()), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("\\("), //
						new RegexOptional(new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("OUTCOMING_COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
						new RegexLeaf("OUTCOMING", "(.*?)"), //
						new RegexLeaf("\\)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final BoxStyle boxStyle;
		final String styleString = arg.get("STYLE", 0);

		if (styleString == null)
			boxStyle = BoxStyle.PLAIN;
		else
			boxStyle = BoxStyle.fromString(styleString);

		final Display label = Display.getWithNewlines(arg.get("LABEL", 0));

		final LinkRendering in = getBackRendering(diagram, arg, "INCOMING");
		final LinkRendering out = getBackRendering(diagram, arg, "OUTCOMING");

		return diagram.backward(label, boxStyle, in, out);
	}

	static public LinkRendering getBackRendering(ActivityDiagram3 diagram, RegexResult arg, String name)
			throws NoSuchColorException {
		final LinkRendering in;
		final Rainbow incomingColor = getRainbow(name + "_COLOR", diagram, arg);
		if (incomingColor == null)
			in = LinkRendering.none();
		else
			in = LinkRendering.create(incomingColor);
		final String label = arg.get(name, 0);
		return in.withDisplay(Display.getWithNewlines(label));
	}

	static private Rainbow getRainbow(String key, ActivityDiagram3 diagram, RegexResult arg)
			throws NoSuchColorException {
		final String colorString = arg.get(key, 0);
		if (colorString == null) {
			return null;
		}
		return Rainbow.build(diagram.getSkinParam(), colorString, diagram.getSkinParam().colorArrowSeparationSpace());
	}

}

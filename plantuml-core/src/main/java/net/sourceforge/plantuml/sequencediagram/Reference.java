package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.url.Url;

public class Reference extends AbstractEvent implements Event {

	private final List<Participant> participants;
	private final Url url;
	private final HColor backColorGeneral;
	private final HColor backColorElement;

	private final Display strings;

	final private Style style;
	final private Style styleHeader;

	public StyleSignatureBasic getDefaultStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.reference);
	}

	private StyleSignatureBasic getHeaderStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.referenceHeader);
	}

	public Style[] getUsedStyles() {
		return new Style[] { style, styleHeader == null ? styleHeader
				: styleHeader.eventuallyOverride(PName.BackGroundColor, backColorElement) };
	}

	public Reference(List<Participant> participants, Url url, Display strings, HColor backColorGeneral,
			HColor backColorElement, StyleBuilder styleBuilder) {
		this.participants = uniq(participants);
		this.url = url;
		this.strings = strings;
		this.backColorGeneral = backColorGeneral;
		this.backColorElement = backColorElement;
		this.style = getDefaultStyleDefinition().getMergedStyle(styleBuilder);
		this.styleHeader = getHeaderStyleDefinition().getMergedStyle(styleBuilder);
	}

	static private List<Participant> uniq(List<Participant> all) {
		final List<Participant> result = new ArrayList<Participant>();
		for (Participant p : all)
			if (result.contains(p) == false)
				result.add(p);
		return Collections.unmodifiableList(result);
	}

	public List<Participant> getParticipant() {
		return participants;
	}

	public Display getStrings() {
		return strings;
	}

	public boolean dealWith(Participant someone) {
		return participants.contains(someone);
	}

	public final Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return url != null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<Participant> it = participants.iterator(); it.hasNext();) {
			sb.append(it.next().getCode());
			if (it.hasNext())
				sb.append("-");

		}
		return sb.toString();
	}

	public final HColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public final HColor getBackColorElement() {
		return backColorElement;
	}
}

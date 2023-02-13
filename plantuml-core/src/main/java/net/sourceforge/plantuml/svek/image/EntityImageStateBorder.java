package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.baraye.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class EntityImageStateBorder extends AbstractEntityImageBorder {

	private final SName sname;

	public EntityImageStateBorder(Entity leaf, ISkinParam skinParam, Cluster stateParent, final Bibliotekon bibliotekon,
			SName sname) {
		super(leaf, skinParam, stateParent, bibliotekon, FontParam.STATE);
		this.sname = sname;
	}

	@Override
	protected StyleSignatureBasic getSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname);
	}

	private boolean upPosition() {
		if (parent == null)
			return false;

		final XPoint2D clusterCenter = parent.getClusterPosition().getPointCenter();
		final SvekNode node = bibliotekon.getNode(getEntity());
		return node.getMinY() < clusterCenter.getY();
	}

	final public void drawU(UGraphic ug) {
		final TextBlock desc = getDesc();
		double y = 0;
		final XDimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double x = 0 - (dimDesc.getWidth() - 2 * EntityPosition.RADIUS) / 2;
		if (upPosition())
			y -= 2 * EntityPosition.RADIUS + dimDesc.getHeight();
		else
			y += 2 * EntityPosition.RADIUS;

		desc.drawU(ug.apply(new UTranslate(x, y)));

		final Style style = getStyle();
		
		final HColor borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		if (backcolor == null)
			backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());

		ug = ug.apply(getUStroke()).apply(borderColor);
		ug = ug.apply(backcolor.bg());

		entityPosition.drawSymbol(ug, rankdir);
	}

	private UStroke getUStroke() {
		return new UStroke(1.5);
	}

	public double getMaxWidthFromLabelForEntryExit(StringBounder stringBounder) {
		final TextBlock desc = getDesc();
		final XDimension2D dimDesc = desc.calculateDimension(stringBounder);
		return dimDesc.getWidth();
	}

}

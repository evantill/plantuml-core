package net.sourceforge.plantuml.svek.image;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;
import net.sourceforge.plantuml.baraye.Entity;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.UComment;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.URectangle;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Kal;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.utils.Direction;

public class EntityImageClass extends AbstractEntityImage implements Stencil, WithPorts {

	final private TextBlock body;

	final private EntityImageClassHeader header;
	final private Url url;
	final private double roundCorner;
	final private LeafType leafType;

	final private LineConfigurable lineConfig;

	public EntityImageClass(Entity entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, entity.getColors().mute(skinParam));
		this.leafType = entity.getLeafType();
		this.lineConfig = entity;

		this.roundCorner = getStyle().value(PName.RoundCorner).asDouble();

		final boolean showMethods = portionShower.showPortion(EntityPortion.METHOD, entity);
		final boolean showFields = portionShower.showPortion(EntityPortion.FIELD, entity);
		this.body = entity.getBodier().getBody(getSkinParam(), showMethods, showFields, entity.getStereotype(),
				getStyle(), null);

		this.header = new EntityImageClassHeader(entity, getSkinParam(), portionShower);
		this.url = entity.getUrl99();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dimHeader = header.calculateDimension(stringBounder);
		final XDimension2D dimBody = body == null ? new XDimension2D(0, 0) : body.calculateDimension(stringBounder);
		double width = Math.max(dimBody.getWidth(), dimHeader.getWidth());
		final double minClassWidth = getStyle().value(PName.MinimumWidth).asDouble();
		if (width < minClassWidth)
			width = minClassWidth;

		final double paramSameClassWidth = getSkinParam().getParamSameClassWidth();
		if (width < paramSameClassWidth)
			width = paramSameClassWidth;

		final double height = dimBody.getHeight() + dimHeader.getHeight();
		return new XDimension2D(Math.max(width, getKalWidth() * 1.3), height);
		// return new Dimension2DDouble(width + getKalWidth(), height);
	}

	private double getKalWidth() {
		double widthUp = 0;
		double widthDown = 0;
		for (Kal kal : ((Entity) getEntity()).getKals(Direction.UP))
			widthUp += kal.getDimension().getWidth();

		for (Kal kal : ((Entity) getEntity()).getKals(Direction.DOWN))
			widthDown += kal.getDimension().getWidth();

		return Math.max(widthUp, widthDown);

	}

	@Override
	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final XRectangle2D result = body.getInnerPosition(member, stringBounder, strategy);
		if (result == null)
			return result;

		final XDimension2D dimHeader = header.calculateDimension(stringBounder);
		final UTranslate translate = UTranslate.dy(dimHeader.getHeight());
		return translate.apply(result);
	}

	final public void drawU(UGraphic ug) {
		ug.draw(new UComment("class " + getEntity().getName()));
		if (url != null)
			ug.startUrl(url);

		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getName() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getName());
		ug.startGroup(typeIDent);
		drawInternal(ug);
		ug.closeGroup();

		if (url != null)
			ug.closeUrl();

	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.classDiagram, SName.class_) //
				.withTOBECHANGED(getEntity().getStereotype()) //
				.with(getEntity().getStereostyles()) //
				.getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private Style getStyleHeader() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.classDiagram, SName.class_, SName.header) //
				.withTOBECHANGED(getEntity().getStereotype()) //
				.with(getEntity().getStereostyles()) //
				.getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private void drawInternal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimTotal = calculateDimension(stringBounder);
		final XDimension2D dimHeader = header.calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal).rounded(roundCorner)
				.withCommentAndCodeLine(getEntity().getName(), getEntity().getCodeLine());

		double shadow = 0;

		HColor borderColor = lineConfig.getColors().getColor(ColorType.LINE);
		HColor headerBackcolor = getEntity().getColors().getColor(ColorType.HEADER);
		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);

		shadow = getStyle().value(PName.Shadowing).asDouble();

		if (borderColor == null)
			borderColor = getStyle().value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());

		if (headerBackcolor == null)
			headerBackcolor = backcolor == null
					? getStyleHeader().value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet())
					: backcolor;

		if (backcolor == null)
			backcolor = getStyle().value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());

		rect.setDeltaShadow(shadow);

		ug = ug.apply(borderColor);
		ug = ug.apply(backcolor.bg());

		final UStroke stroke = getStyle().getStroke(lineConfig.getColors());

		UGraphic ugHeader = ug;
		if (roundCorner == 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			ug.apply(stroke).draw(rect);
			final Shadowable rect2 = new URectangle(widthTotal, dimHeader.getHeight());
			rect2.setDeltaShadow(0);
			ugHeader = ugHeader.apply(headerBackcolor.bg());
			ugHeader.apply(stroke).draw(rect2);
		} else if (roundCorner != 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			ug.apply(stroke).draw(rect);
			final Shadowable rect2 = new URectangle(widthTotal, dimHeader.getHeight()).rounded(roundCorner);
			final URectangle rect3 = new URectangle(widthTotal, roundCorner / 2);
			rect2.setDeltaShadow(0);
			rect3.setDeltaShadow(0);
			ugHeader = ugHeader.apply(headerBackcolor.bg()).apply(headerBackcolor);
			ugHeader.apply(stroke).draw(rect2);
			ugHeader.apply(stroke).apply(UTranslate.dy(dimHeader.getHeight() - rect3.getHeight())).draw(rect3);
			rect.setDeltaShadow(0);
			ug.apply(stroke).apply(HColors.none().bg()).draw(rect);
		} else {
			ug.apply(stroke).draw(rect);
		}
		header.drawU(ugHeader, dimTotal.getWidth(), dimHeader.getHeight());

		if (body != null) {
			final UGraphic ug2 = UGraphicStencil.create(ug, this, stroke);
			final UTranslate translate = UTranslate.dy(dimHeader.getHeight());
			body.drawU(ug2.apply(translate));
		}
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final XDimension2D dimHeader = header.calculateDimension(stringBounder);
		if (body instanceof WithPorts)
			return ((WithPorts) body).getPorts(stringBounder).translateY(dimHeader.getHeight());
		return new Ports();
	}

	public ShapeType getShapeType() {
		if (((Entity) getEntity()).getPortShortNames().size() > 0)
			return ShapeType.RECTANGLE_HTML_FOR_PORTS;

		return ShapeType.RECTANGLE;
	}

	@Override
	public Margins getShield(StringBounder stringBounder) {
		return ((Entity) getEntity()).getMargins();
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

}

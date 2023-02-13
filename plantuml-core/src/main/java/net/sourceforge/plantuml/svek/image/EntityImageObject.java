package net.sourceforge.plantuml.svek.image;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.CornerParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;
import net.sourceforge.plantuml.baraye.Entity;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.URectangle;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.text.Guillemet;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;
import net.sourceforge.plantuml.url.Url;

public class EntityImageObject extends AbstractEntityImage implements Stencil, WithPorts {

	final private TextBlock name;
	final private TextBlock stereo;
	final private TextBlock fields;
	final private Url url;
	final private double roundCorner;

	final private LineConfigurable lineConfig;

	public EntityImageObject(Entity entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, skinParam);
		this.lineConfig = entity;
		final Stereotype stereotype = entity.getStereotype();
		this.roundCorner = skinParam.getRoundCorner(CornerParam.DEFAULT, null);

		final FontConfiguration fcHeader = getStyleHeader().getFontConfiguration(getSkinParam().getIHtmlColorSet());

		final TextBlock tmp = getUnderlinedName(entity).create(fcHeader, HorizontalAlignment.CENTER, skinParam);
		this.name = TextBlockUtils.withMargin(tmp, 2, 2);
		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
				|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false)
			this.stereo = null;
		else
			this.stereo = Display.create(stereotype.getLabels(skinParam.guillemet())).create(
					FontConfiguration.create(getSkinParam(), FontParam.OBJECT_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam);

		final boolean showFields = portionShower.showPortion(EntityPortion.FIELD, entity);

		if (entity.getBodier().getFieldsToDisplay().size() == 0)
			this.fields = new TextBlockLineBefore(getStyle().value(PName.LineThickness).asDouble(),
					new TextBlockEmpty(10, 16));
		else
			this.fields = entity.getBodier().getBody(skinParam, false, showFields, entity.getStereotype(), getStyle(),
					null);

		this.url = entity.getUrl99();

	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.objectDiagram, SName.object)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private Style getStyleHeader() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.objectDiagram, SName.object, SName.header)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private Display getUnderlinedName(Entity entity) {
		if (getSkinParam().strictUmlStyle())
			return entity.getDisplay().underlinedName();

		return entity.getDisplay();
	}

	private int marginEmptyFieldsOrMethod = 13;

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dimTitle = getTitleDimension(stringBounder);
		final XDimension2D dimFields = fields.calculateDimension(stringBounder);
		double width = Math.max(dimFields.getWidth(), dimTitle.getWidth() + 2 * xMarginCircle);
		final double minClassWidth = getStyle().value(PName.MinimumWidth).asDouble();
		if (width < minClassWidth)
			width = minClassWidth;

		final double height = getMethodOrFieldHeight(dimFields) + dimTitle.getHeight();
		return new XDimension2D(width, height);
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimTotal = calculateDimension(stringBounder);
		final XDimension2D dimTitle = getTitleDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal).rounded(roundCorner);

		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		HColor headerBackcolor = getEntity().getColors().getColor(ColorType.HEADER);

		final Style style = getStyle();
		final HColor borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());

		if (headerBackcolor == null)
			headerBackcolor = backcolor == null
					? getStyleHeader().value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet())
					: backcolor;

		if (backcolor == null)
			backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());

		rect.setDeltaShadow(style.value(PName.Shadowing).asDouble());
		final UStroke stroke = style.getStroke();

		ug = ug.apply(borderColor).apply(backcolor.bg());

		if (url != null)
			ug.startUrl(url);

		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getName() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getName());
		ug.startGroup(typeIDent);
		ug.apply(stroke).draw(rect);

		if (roundCorner == 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			final Shadowable rect2 = new URectangle(widthTotal, dimTitle.getHeight());
			final UGraphic ugHeader = ug.apply(headerBackcolor.bg());
			ugHeader.apply(stroke).draw(rect2);
		}

		final ULayoutGroup header = getLayout(stringBounder);
		header.drawU(ug, dimTotal.getWidth(), dimTitle.getHeight());

		final UGraphic ug2 = UGraphicStencil.create(ug, this, stroke);
		fields.drawU(ug2.apply(UTranslate.dy(dimTitle.getHeight())));

		if (url != null)
			ug.closeUrl();

		ug.closeGroup();
	}

	private ULayoutGroup getLayout(final StringBounder stringBounder) {
		final ULayoutGroup header = new ULayoutGroup(new PlacementStrategyY1Y2(stringBounder));
		if (stereo != null)
			header.add(stereo);

		header.add(name);
		return header;
	}

	private double getMethodOrFieldHeight(final XDimension2D dim) {
		final double fieldsHeight = dim.getHeight();
		if (fieldsHeight == 0)
			return marginEmptyFieldsOrMethod;

		return fieldsHeight;
	}

	private int xMarginCircle = 5;

	private XDimension2D getTitleDimension(StringBounder stringBounder) {
		return getNameAndSteretypeDimension(stringBounder);
	}

	private XDimension2D getNameAndSteretypeDimension(StringBounder stringBounder) {
		final XDimension2D nameDim = name.calculateDimension(stringBounder);
		final XDimension2D stereoDim = stereo == null ? new XDimension2D(0, 0)
				: stereo.calculateDimension(stringBounder);
		final XDimension2D nameAndStereo = new XDimension2D(Math.max(nameDim.getWidth(), stereoDim.getWidth()),
				nameDim.getHeight() + stereoDim.getHeight());
		return nameAndStereo;
	}

	public ShapeType getShapeType() {
		if (((Entity) getEntity()).getPortShortNames().size() > 0)
			return ShapeType.RECTANGLE_HTML_FOR_PORTS;

		return ShapeType.RECTANGLE;
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final XDimension2D dimHeader = getNameAndSteretypeDimension(stringBounder);
		if (fields instanceof WithPorts)
			return ((WithPorts) fields).getPorts(stringBounder).translateY(dimHeader.getHeight());
		return new Ports();
	}

	@Override
	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final XDimension2D dimTitle = getTitleDimension(stringBounder);
		final UTranslate translate = UTranslate.dy(dimTitle.getHeight());
		return translate.apply(fields.getInnerPosition(member, stringBounder, strategy));
	}

}

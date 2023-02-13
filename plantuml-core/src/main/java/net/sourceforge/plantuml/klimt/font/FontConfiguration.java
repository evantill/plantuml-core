package net.sourceforge.plantuml.klimt.font;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontPosition;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.graphic.SvgAttributes;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;

public class FontConfiguration {

	private final EnumSet<FontStyle> styles;
	private final UFont currentFont;
	private final UFont motherFont;
	private final HColor motherColor;
	private final HColor currentColor;
	private final HColor extendedColor;
	private final FontPosition fontPosition;
	private final SvgAttributes svgAttributes;

	private final UStroke hyperlinkUnderlineStroke;
	private final HColor hyperlinkColor;

	private final int tabSize;

	public String toStringDebug() {
		return getFont().toStringDebug() + " " + styles.toString();
//		return "" + currentFont + " " + styles.toString() + currentColor + extendedColor + hyperlinkColor
//				+ hyperlinkUnderlineStroke + fontPosition + tabSize;
	}

	@Override
	public int hashCode() {
		return currentFont.hashCode()//
				+ styles.hashCode() //
				+ currentColor.hashCode()//
				+ hashCode(extendedColor)//
				+ hyperlinkColor.hashCode()//
				+ hashCode(hyperlinkUnderlineStroke)//
				+ fontPosition.hashCode() //
				+ tabSize;
	}

	private int hashCode(Object obj) {
		if (obj == null)
			return 43;
		return obj.hashCode();
	}

	private boolean same(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 != null && obj2 != null)
			return obj1.equals(obj2);
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		final FontConfiguration other = (FontConfiguration) obj;
		return currentFont.equals(other.currentFont) && styles.equals(other.styles)
				&& currentColor.equals(other.currentColor) && same(extendedColor, other.extendedColor)
				&& hyperlinkColor.equals(other.hyperlinkColor)
				&& same(hyperlinkUnderlineStroke, other.hyperlinkUnderlineStroke)
				&& fontPosition.equals(other.fontPosition) && tabSize == other.tabSize;
	}

	public static FontConfiguration create(UFont font, HColor color, HColor hyperlinkColor,
			UStroke hyperlinkUnderlineStroke) {
		return create(font, color, hyperlinkColor, hyperlinkUnderlineStroke, 8);
	}

	public static FontConfiguration create(UFont font, HColor color, HColor hyperlinkColor,
			UStroke hyperlinkUnderlineStroke, int tabSize) {
		return new FontConfiguration(getStyles(font), font, color, font, color, null, FontPosition.NORMAL,
				new SvgAttributes(), hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public static FontConfiguration blackBlueTrue(UFont font) {
		return create(font, HColors.BLACK.withDark(HColors.WHITE), HColors.BLUE, new UStroke(), 8);
	}

	public static FontConfiguration create(ISkinParam skinParam, FontParam fontParam, Stereotype stereo) {
		return create(SkinParamUtils.getFont(skinParam, fontParam, stereo),
				SkinParamUtils.getFontColor(skinParam, fontParam, stereo), skinParam.getHyperlinkColor(),
				skinParam.useUnderlineForHyperlink(), skinParam.getTabSize());
	}

	public static FontConfiguration create(ISkinParam skinParam, Style style) {
		return create(skinParam, style, null);
	}

	public static FontConfiguration create(ISkinParam skinParam, Style style, Colors colors) {
		final HColor hyperlinkColor = style.value(PName.HyperLinkColor).asColor(skinParam.getIHtmlColorSet());
		final UStroke hyperlinkUnderlineStroke = skinParam.useUnderlineForHyperlink();
		HColor color = colors == null ? null : colors.getColor(ColorType.TEXT);
		if (color == null)
			color = style.value(PName.FontColor).asColor(skinParam.getIHtmlColorSet());
		return create(style.getUFont(), color, hyperlinkColor, hyperlinkUnderlineStroke, skinParam.getTabSize());
	}

	// ---

	private static EnumSet<FontStyle> getStyles(UFont font) {
		final boolean bold = font.isBold();
		final boolean italic = font.isItalic();
		if (bold && italic)
			return EnumSet.of(FontStyle.ITALIC, FontStyle.BOLD);

		if (bold)
			return EnumSet.of(FontStyle.BOLD);

		if (italic)
			return EnumSet.of(FontStyle.ITALIC);

		return EnumSet.noneOf(FontStyle.class);
	}

	@Override
	public String toString() {
		return styles.toString() + " " + currentColor;
	}

	private FontConfiguration(EnumSet<FontStyle> styles, UFont motherFont, HColor motherColor, UFont currentFont,
			HColor currentColor, HColor extendedColor, FontPosition fontPosition, SvgAttributes svgAttributes,
			HColor hyperlinkColor, UStroke hyperlinkUnderlineStroke, int tabSize) {
		this.styles = styles;
		this.currentFont = currentFont;
		this.motherFont = motherFont;
		this.currentColor = currentColor;
		this.motherColor = motherColor;
		this.extendedColor = extendedColor;
		this.fontPosition = fontPosition;
		this.svgAttributes = svgAttributes;
		this.hyperlinkColor = hyperlinkColor;
		this.hyperlinkUnderlineStroke = hyperlinkUnderlineStroke;
		this.tabSize = tabSize;
	}

	public FontConfiguration forceFont(UFont newFont, HColor htmlColorForStereotype) {
		if (newFont == null)
			return add(FontStyle.ITALIC);

		FontConfiguration result = new FontConfiguration(styles, newFont, motherColor, newFont, currentColor,
				extendedColor, fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
		if (htmlColorForStereotype != null)
			result = result.changeColor(htmlColorForStereotype);

		return result;
	}

	public FontConfiguration changeAttributes(SvgAttributes toBeAdded) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, extendedColor,
				fontPosition, svgAttributes.add(toBeAdded), hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	private FontConfiguration withHyperlink() {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, hyperlinkColor, extendedColor,
				fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration changeColor(HColor newHtmlColor) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, newHtmlColor, extendedColor,
				fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration mute(Colors colors) {
		final HColor color = Objects.requireNonNull(colors).getColor(ColorType.TEXT);
		if (color == null)
			return this;

		return changeColor(color);
	}

	public FontConfiguration changeExtendedColor(HColor newExtendedColor) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, newExtendedColor,
				fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration changeSize(float size) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont.withSize(size), currentColor,
				extendedColor, fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration bigger(double delta) {
		return changeSize((float) (currentFont.getSize() + delta));
	}

	public FontConfiguration changeFontPosition(FontPosition fontPosition) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, extendedColor,
				fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration changeFamily(String family) {
		return new FontConfiguration(styles, motherFont, motherColor,
				new UFont(family, currentFont.getStyle(), currentFont.getSize()), currentColor, extendedColor,
				fontPosition, svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration resetFont() {
		return new FontConfiguration(styles, motherFont, motherColor, motherFont, motherColor, null,
				FontPosition.NORMAL, new SvgAttributes(), hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration add(FontStyle style) {
		final EnumSet<FontStyle> r = styles.clone();
		if (style == FontStyle.PLAIN)
			r.clear();

		r.add(style);
		return new FontConfiguration(r, motherFont, motherColor, currentFont, currentColor, extendedColor, fontPosition,
				svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public FontConfiguration italic() {
		return add(FontStyle.ITALIC);
	}

	public FontConfiguration bold() {
		return add(FontStyle.BOLD);
	}

	public FontConfiguration unbold() {
		return remove(FontStyle.BOLD);
	}

	public FontConfiguration unitalic() {
		return remove(FontStyle.ITALIC);
	}

	public FontConfiguration underline() {
		return add(FontStyle.UNDERLINE);
	}

	public FontConfiguration wave(HColor color) {
		return add(FontStyle.WAVE).changeExtendedColor(color);
	}

	public FontConfiguration hyperlink() {
		if (hyperlinkUnderlineStroke != null)
			return add(FontStyle.UNDERLINE).withHyperlink();

		return withHyperlink();
	}

	public FontConfiguration remove(FontStyle style) {
		final EnumSet<FontStyle> r = styles.clone();
		r.remove(style);
		return new FontConfiguration(r, motherFont, motherColor, currentFont, currentColor, extendedColor, fontPosition,
				svgAttributes, hyperlinkColor, hyperlinkUnderlineStroke, tabSize);
	}

	public UFont getFont() {
		UFont result = currentFont;
		for (FontStyle style : styles) {
			result = style.mutateFont(result);
		}
		return fontPosition.mute(result);
	}

	public HColor getColor() {
		return currentColor;
	}

	public HColor getExtendedColor() {
		return extendedColor;
	}

	public boolean containsStyle(FontStyle style) {
		return styles.contains(style);
	}

	public int getSpace() {
		return fontPosition.getSpace();
	}

	public Map<String, String> getAttributes() {
		return svgAttributes.attributes();
	}

	public double getSize2D() {
		return currentFont.getSize2D();
	}

	public int getTabSize() {
		return tabSize;
	}

	public UStroke getUnderlineStroke() {
		return hyperlinkUnderlineStroke;
		// return new UStroke();
		// return new UStroke(3, 5, 2);
	}

}

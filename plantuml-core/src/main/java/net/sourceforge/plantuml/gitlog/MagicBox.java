// THIS FILE HAS BEEN GENERATED BY A PREPROCESSOR.
package net.sourceforge.plantuml.gitlog;

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

public class MagicBox {

	private final ISkinParam skinParam;
	private final GNode node;
	private final HColor fontColor;

	public MagicBox(ISkinParam skinParam, GNode node) {
		this.skinParam = skinParam;
		this.node = node;
		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.gitDiagram)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		this.fontColor = style.value(PName.FontColor).asColor(skinParam.getIHtmlColorSet());
	}

	private TextBlock getSmallBlock() {
		final FontConfiguration fc = FontConfiguration.create(UFont.monospaced(15).bold(), fontColor, fontColor, null);
		return node.getDisplay().create(fc, HorizontalAlignment.CENTER, skinParam);
	}

	private TextBlock getCommentBlock() {
		if (node.getComment() != null && node.isTop()) {
			final FontConfiguration tag = FontConfiguration.create(UFont.sansSerif(13), fontColor, fontColor, null);
			return Display.create(node.getComment()).create(tag, HorizontalAlignment.CENTER, skinParam);
		}
		return TextBlockUtils.empty(0, 0);

	}

	public XDimension2D getBigDim(StringBounder stringBounder) {
		final XDimension2D dimComment = getCommentBlock().calculateDimension(stringBounder);
		final XDimension2D dimSmall = getSmallBlock().calculateDimension(stringBounder);
		final XDimension2D mergeTB = dimComment.mergeTB(dimSmall);
		return mergeTB.delta(8, 2);
	}

	public void drawBorder(UGraphic ug, XDimension2D sizeInDot) {

		final TextBlock comment = getCommentBlock();
		final TextBlock small = getSmallBlock();

		final double moveY = comment.calculateDimension(ug.getStringBounder()).getHeight();

		final URectangle rect = URectangle.build(sizeInDot.getWidth(), sizeInDot.getHeight() - moveY).rounded(8);
		ug.apply(UStroke.withThickness(1.5)).apply(UTranslate.dy(moveY)).draw(rect);

		comment.drawU(ug);

		final double deltaWidth = rect.getWidth() - small.calculateDimension(ug.getStringBounder()).getWidth();
		final double deltaHeight = rect.getHeight() - small.calculateDimension(ug.getStringBounder()).getHeight();

		small.drawU(ug.apply(new UTranslate(deltaWidth / 2, moveY + deltaHeight / 2)));

	}

}

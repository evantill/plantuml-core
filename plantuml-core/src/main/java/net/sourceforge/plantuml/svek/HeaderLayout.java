package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.utils.MathUtils;

public class HeaderLayout {

	final private TextBlock name;
	final private TextBlock stereo;
	final private TextBlock generic;
	final private TextBlock circledCharacter;

	public HeaderLayout(TextBlock circledCharacter, TextBlock stereo, TextBlock name, TextBlock generic) {
		this.circledCharacter = protectAgaintNull(circledCharacter);
		this.stereo = protectAgaintNull(stereo);
		this.name = protectAgaintNull(name);
		this.generic = protectAgaintNull(generic);
	}

	private static TextBlock protectAgaintNull(TextBlock block) {
		if (block == null) {
			return new TextBlockEmpty();
		}
		return block;
	}

	public XDimension2D getDimension(StringBounder stringBounder) {
		final XDimension2D nameDim = name.calculateDimension(stringBounder);
		final XDimension2D genericDim = generic.calculateDimension(stringBounder);
		final XDimension2D stereoDim = stereo.calculateDimension(stringBounder);
		final XDimension2D circleDim = circledCharacter.calculateDimension(stringBounder);

		final double width = circleDim.getWidth() + Math.max(stereoDim.getWidth(), nameDim.getWidth())
				+ genericDim.getWidth();
		final double height = MathUtils.max(circleDim.getHeight(), stereoDim.getHeight() + nameDim.getHeight() + 10,
				genericDim.getHeight());
		return new XDimension2D(width, height);
	}

	public void drawU(UGraphic ug, double width, double height) {

		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D nameDim = name.calculateDimension(stringBounder);
		final XDimension2D genericDim = generic.calculateDimension(stringBounder);
		final XDimension2D stereoDim = stereo.calculateDimension(stringBounder);
		final XDimension2D circleDim = circledCharacter.calculateDimension(stringBounder);

		final double widthStereoAndName = Math.max(stereoDim.getWidth(), nameDim.getWidth());
		final double suppWith = Math.max(0, width - circleDim.getWidth() - widthStereoAndName - genericDim.getWidth());
		assert suppWith >= 0;

		final double h2 = Math.min(circleDim.getWidth() / 4, suppWith * 0.1);
		final double h1 = (suppWith - h2) / 2;
		assert h1 >= 0;
		assert h2 >= 0;

		final double xCircle = h1;
		final double yCircle = (height - circleDim.getHeight()) / 2;
		circledCharacter.drawU(ug.apply(new UTranslate(xCircle, yCircle)));

		final double diffHeight = height - stereoDim.getHeight() - nameDim.getHeight();
		final double xStereo = circleDim.getWidth() + (widthStereoAndName - stereoDim.getWidth()) / 2 + h1 + h2;
		final double yStereo = diffHeight / 2;
		stereo.drawU(ug.apply(new UTranslate(xStereo, yStereo)));

		final double xName = circleDim.getWidth() + (widthStereoAndName - nameDim.getWidth()) / 2 + h1 + h2;
		final double yName = diffHeight / 2 + stereoDim.getHeight();
		name.drawU(ug.apply(new UTranslate(xName, yName)));

		if (genericDim.getWidth() > 0) {
			final double delta = 4;
			final double xGeneric = width - genericDim.getWidth() + delta;
			final double yGeneric = -delta;
			generic.drawU(ug.apply(new UTranslate(xGeneric, yGeneric)));
		}
	}

}

package net.sourceforge.plantuml.descdiagram;

import java.util.Map;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.baraye.Entity;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;

public class DescriptionDiagram extends AbstractEntityDiagram {

	public DescriptionDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.DESCRIPTION, skinParam);
	}

	@Override
	public String cleanId(String id) {
		if (id == null)
			return null;
		if (id.startsWith("()"))
			id = StringUtils.trin(id.substring(2));
		return super.cleanId(id);
	}

	private boolean isUsecase() {
		for (Entity leaf : getEntityFactory().leafs()) {
			final LeafType type = leaf.getLeafType();
			final USymbol usymbol = leaf.getUSymbol();
			if (type == LeafType.USECASE || usymbol == getSkinParam().actorStyle().toUSymbol()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void makeDiagramReady() {
		super.makeDiagramReady();
		final LeafType defaultType = LeafType.DESCRIPTION;
		final USymbol defaultSymbol = isUsecase() ? getSkinParam().actorStyle().toUSymbol() : USymbols.INTERFACE;
		for (Entity leaf : getEntityFactory().leafs()) {
			if (leaf.getLeafType() == LeafType.STILL_UNKNOWN) {
				leaf.muteToType(defaultType, defaultSymbol);
			}
		}
	}

	@Override
	public String checkFinalError() {
		this.applySingleStrategy();
		return super.checkFinalError();
	}

}

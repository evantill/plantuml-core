// THIS FILE HAS BEEN GENERATED BY A PREPROCESSOR.
package net.sourceforge.plantuml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.BlocLines;

public class NewpagedDiagram extends AbstractPSystem {

	private final List<Diagram> diagrams = new ArrayList<>();

	public NewpagedDiagram(UmlSource source, AbstractPSystem diag1, AbstractPSystem diag2) {
		super(source);
		if (diag1 instanceof NewpagedDiagram) {
			throw new IllegalArgumentException();
		}
		if (diag2 instanceof NewpagedDiagram) {
			throw new IllegalArgumentException();
		}
		this.diagrams.add(diag1);
		this.diagrams.add(diag2);
	}

	@Override
	public String toString() {
		return super.toString() + " SIZE=" + diagrams.size() + " " + diagrams;
	}

	public Diagram getLastDiagram() {
		return diagrams.get(diagrams.size() - 1);
	}

	public CommandExecutionResult executeCommand(Command cmd, BlocLines lines) {
		final int nb = diagrams.size();
		try {
			final CommandExecutionResult tmp = cmd.execute(diagrams.get(nb - 1), lines);
			if (tmp.getNewDiagram() instanceof NewpagedDiagram) {
				final NewpagedDiagram new1 = (NewpagedDiagram) tmp.getNewDiagram();
				// System.err.println("this=" + this);
				// System.err.println("new1=" + new1);
				if (new1.size() != 2) {
					throw new IllegalStateException();
				}
				if (new1.diagrams.get(0) != this.diagrams.get(nb - 1)) {
					throw new IllegalStateException();
				}
				this.diagrams.add(new1.diagrams.get(1));
				return tmp.withDiagram(this);

			}
			return tmp;
		} catch (NoSuchColorException e) {
			return CommandExecutionResult.badColor();
		}
	}

	private int size() {
		return diagrams.size();
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat)
			throws IOException {
		return diagrams.get(num).exportDiagram(os, 0, fileFormat);
	}

	public int getNbImages() {
		int nb = 0;
		for (Diagram d : diagrams) {
			nb += d.getNbImages();
		}
		return nb;
	}

	public DiagramDescription getDescription() {
		final StringBuilder sb = new StringBuilder();
		for (Diagram d : diagrams) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(d.getDescription());
		}
		return new DiagramDescription(sb.toString());
	}

	public String getWarningOrError() {
		final StringBuilder sb = new StringBuilder();
		for (Diagram d : diagrams) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			if (d.getWarningOrError() != null) {
				sb.append(d.getWarningOrError());
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}

	@Override
	public void makeDiagramReady() {
		super.makeDiagramReady();
		for (Diagram diagram : diagrams) {
			((AbstractPSystem) diagram).makeDiagramReady();
		}
	}

	@Override
	public String checkFinalError() {
		for (Diagram p : getDiagrams()) {
			final String check = ((AbstractPSystem) p).checkFinalError();
			if (check != null) {
				return check;
			}
		}
		return super.checkFinalError();
	}

	public final List<Diagram> getDiagrams() {
		return Collections.unmodifiableList(diagrams);
	}

}

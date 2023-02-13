package net.sourceforge.plantuml.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.Stereotype;

public class Highlighted {

	public static final String HIGHLIGHTED = "#highlight ";
	private final static Pattern pattern = Pattern.compile("^([^<>]+)(\\<\\<.*\\>\\>)?$");

	private final List<String> paths;
	private final Stereotype stereotype;

	private Highlighted(List<String> paths, Stereotype stereotype) {
		this.paths = paths;
		this.stereotype = stereotype;
	}

	public static boolean matchesDefinition(String line) {
		return line.startsWith(Highlighted.HIGHLIGHTED);
	}

	public static Highlighted build(String line) {
		if (matchesDefinition(line) == false)
			throw new IllegalStateException();
		line = line.substring(HIGHLIGHTED.length()).trim();

		final Matcher matcher = pattern.matcher(line);
		if (matcher.matches() == false)
			throw new IllegalStateException();
		final String paths = matcher.group(1).trim();
		final Stereotype stereotype = matcher.group(2) == null ? null : Stereotype.build(matcher.group(2));

		return new Highlighted(toList(paths), stereotype);
	}

	private static List<String> toList(String paths) {
		final List<String> result = new ArrayList<>();
		for (String s : paths.split("/"))
			result.add(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(s.trim(), "\""));

		return result;
	}

	public Highlighted upOneLevel(String key) {
		if (paths.size() <= 1)
			return null;
		final String first = paths.get(0);
		if ("**".equals(first))
			return new Highlighted(paths, stereotype);
		if ("*".equals(first) || first.equals(key))
			return new Highlighted(paths.subList(1, paths.size()), stereotype);
		return null;

	}

	public boolean isKeyHighlight(String key) {
		if (paths.size() == 2 && paths.get(0).equals("**") && paths.get(1).equals(key))
			return true;
		return paths.size() == 1 && paths.get(0).equals(key);
	}

	public final Stereotype getStereotype() {
		return stereotype;
	}

}

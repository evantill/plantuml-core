package net.sourceforge.plantuml.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.text.StringLocated;

public class IteratorCounter2Impl implements IteratorCounter2 {

	private List<StringLocated> data;
	private List<StringLocated> trace;
	private int nb;

	public void copyStateFrom(IteratorCounter2 other) {
		final IteratorCounter2Impl source = (IteratorCounter2Impl) other;
		this.nb = source.nb;
		this.data = source.data;
		this.trace = source.trace;
	}

	public IteratorCounter2Impl(List<StringLocated> data) {
		this(data, 0, new ArrayList<StringLocated>());
	}

	private IteratorCounter2Impl(List<StringLocated> data, int nb, List<StringLocated> trace) {
		this.data = data;
		this.nb = nb;
		this.trace = trace;
	}

	public IteratorCounter2 cloneMe() {
		return new IteratorCounter2Impl(data, nb, new ArrayList<>(trace));
	}

	public int currentNum() {
		return nb;
	}

	public boolean hasNext() {
		return nb < data.size();
	}

	public StringLocated next() {
		final StringLocated result = data.get(nb);
		nb++;
		trace.add(result);
		return result;
	}

	public StringLocated peek() {
		return data.get(nb);
	}

	public StringLocated peekPrevious() {
		if (nb == 0) {
			return null;
		}
		return data.get(nb - 1);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public final List<StringLocated> getTrace() {
		return Collections.unmodifiableList(trace);
	}

}

package com.chinese_checkers.Utils;

/**
 * A simple class to represent a pair of objects.
 *
 * @param <F> the type of the first object
 * @param <S> the type of the second object
 */
public class Pair<F, S> {

	public F first;
	public S second;

	public Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}

package com.chinese_checkers.Utils;

// WTF java doesn't have a Pair class ????? (another big java L)
public class Pair<F, S> {
	public F first;
	public S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}

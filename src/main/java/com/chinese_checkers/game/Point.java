package com.chinese_checkers.game;

/**
 * Represents a point on the cube-coordinate system
 */
public class Point
{
	// you can't stop me from making this
	public int s, q, r; // expose it because we hate java and its stupid getters, vive la c++
	// ^ C# aka Microsoft Java has properties, but Java somehow still doesn't have them (lame)
	// NOTE: DO NOT, under any circumstances, add
	// getS, getQ, getR, setS, setQ, setR methods
	// it's an abomination, and you should feel bad for even thinking about it, long live C++

	public Point()
	{
		this(0, 0, 0);
	}

	public Point(int s, int q, int r)
	{
		this.s = s;
		this.q = q;
		this.r = r;
	}

	public static int distance(Point a, Point b)
	{
		return (Math.abs(a.s - b.s) + Math.abs(a.q - b.q) + Math.abs(a.r - b.r)) / 2;
	}

	public void add(Point p)
	{
		s += p.s;
		q += p.q;
		r += p.r;
	}

	public void subtract(Point p)
	{
		s -= p.s;
		q -= p.q;
		r -= p.r;
	}

	public void multiply(int k)
	{
		s *= k;
		q *= k;
		r *= k;
	}

	public void divide(int k)
	{
		if (k == 0)
			throw new ArithmeticException("Division by zero");

		s /= k;
		q /= k;
		r /= k;
	}

	public boolean equals(Point p)
	{
		return s == p.s && q == p.q && r == p.r;
	}

	@Override
	public String toString()
	{
		return "(" + s + ", " + q + ", " + r + ")";
	}
}

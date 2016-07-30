/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ConnectedComponent.java is PROPRIETARY/CONFIDENTIAL built in 6:51:27 PM, Jul
 * 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.geom;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The connected component class.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ConnectedComponent implements Iterable<Point>
{
	/**
	 * Hash a two-dimensional coordinates to hash code.
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the hash code
	 */
	public static int hash(int x, int y)
	{
		return y << 16 | x;
	}

	/**
	 * Recovery a two-dimensional point from its hash code.
	 * 
	 * @param code the hash code
	 * @param p the point to restore the result, cannot be <code>null</code>
	 * @return the point
	 */
	protected static Point unhash(int code, Point p)
	{
		p.x = code & 0xffff;
		p.y = code >> 16;
		return p;
	}

	/**
	 * Recovery a two-dimensional point from its hash code.
	 * 
	 * @param code the hash code
	 * @return the point
	 */
	public static Point unhash(int code)
	{
		Point p = new Point();
		return unhash(code, p);
	}

	/**
	 * The point set.
	 */
	protected HashSet<Integer>	points;

	/**
	 * The boundary.
	 */
	private int					minX, minY, maxX, maxY;

	/**
	 * Construct an instance of <tt>ConnectedComponent</tt>.
	 */
	public ConnectedComponent()
	{
		points = new HashSet<>();
		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
	}

	/**
	 * Returns the size of the connected component.
	 * 
	 * @return the size of the connected component
	 */
	public int size()
	{
		return points.size();
	}

	/**
	 * Add a point to this connected component
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	public void addPoint(int x, int y)
	{
		if (x < minX)
			minX = x;
		if (x > maxX)
			maxX = x;
		if (y < minY)
			minY = y;
		if (y > maxY)
			maxY = y;
		points.add(hash(x, y));
	}

	/**
	 * Add a point to this connected component.
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	public void addPoint(Point p)
	{
		addPoint(p.x, p.y);
	}

	/**
	 * Add all the points in the specified collection to this connected
	 * component.
	 * 
	 * @param points the point collection
	 */
	public void addPoints(Collection<Point> points)
	{
		for (Point p : points)
			addPoint(p.x, p.y);
	}

	/**
	 * Returns <code>true</code> if the connected component contains the
	 * specified point.
	 * 
	 * @param p the specified point
	 * @return <code>true</code> if contains; otherwise, <code>false</code>
	 */
	public boolean contains(Point p)
	{
		return points.contains(hash(p.x, p.y));
	}

	/**
	 * Returns <code>true</code> if the connected component contains the
	 * specified point.
	 * 
	 * @param p the specified point
	 * @return <code>true</code> if contains; otherwise, <code>false</code>
	 */
	public boolean contains(int x, int y)
	{
		return points.contains(hash(x, y));
	}

	/**
	 * Returns true if this connected component intersects with the specified
	 * shape.
	 * 
	 * @param shape the specified shape
	 * @return <code>true</code> if intersects; otherwise, <code>false</code>
	 */
	public boolean intersects(Shape shape)
	{
		Point p = new Point();
		for (Integer point : points)
		{
			unhash(point, p);
			if (shape.contains(p.x, p.y))
				return true;
		}
		return false;
	}

	/**
	 * Returns the bound box of the connected component.
	 * 
	 * @return the bound box of the connected component
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Returns the intersection size.
	 * 
	 * @param shape the shape to intersect
	 * @return the intersection size
	 */
	public int intersection(Shape shape)
	{
		int size = 0;
		Point p = new Point();
		for (Integer point : points)
		{
			unhash(point, p);
			if (shape.contains(p.x, p.y))
				size++;
		}
		return size;
	}

	/**
	 * Returns the union size and the size of the shape.
	 * 
	 * @param shape the specified shape to be measured
	 * @return [the union size, size of shape]
	 */
	public int[] union(Shape shape)
	{
		HashSet<Integer> set = new HashSet<>(points);
		Rectangle r = shape.getBounds();
		int size = 0;
		for (int y = 0; y < r.height; y++)
			for (int x = 0; x < r.width; x++)
				if (shape.contains(x + r.x, y + r.y))
				{
					set.add(hash(x + r.x, y + r.y));
					size++;
				}
		return new int[] { set.size(), size };
	}

	private static class CompIter implements Iterator<Point>
	{
		private LinkedList<Iterator<Integer>> iterators;

		public CompIter(Iterator<Integer>... iterators)
		{
			this.iterators = new LinkedList<>();
			for (Iterator<Integer> it : iterators)
				this.iterators.add(it);
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext()
		{
			return iterators.getFirst().hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Point next()
		{
			Integer next = null;
			if (!iterators.isEmpty())
			{
				while (!iterators.isEmpty() && !iterators.getFirst().hasNext())
					iterators.removeFirst();
				if (!iterators.isEmpty())
					next = iterators.getFirst().next();
			}
			if (next == null)
				throw new NoSuchElementException();
			return unhash(next);
		}
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Point> iterator()
	{
		return new CompIter(points.iterator());
	}
}

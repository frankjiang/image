/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Quadrangle.java is PROPRIETARY/CONFIDENTIAL built in 12:10:18 PM, Jun 28,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * The class of a quadrangle.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Quadrangle extends Polygon
{

	/**
	 * serialVersionUID.
	 */
	private static final long	serialVersionUID	= 6596323750716779893L;

	/**
	 * The top left point.
	 */
	protected Point				tl;

	/**
	 * The top right point.
	 */
	protected Point				tr;
	/**
	 * The bottom left point.
	 */
	protected Point				bl;
	/**
	 * The bottom right point.
	 */
	protected Point				br;

	public Quadrangle(int x00, int y00, int x01, int y01, int x11, int y11, int x10, int y10)
	{
		this(new Point(x00, y00), new Point(x01, y01), new Point(x10, y10), new Point(x11, y11));
	}

	/**
	 * Construct an instance of <tt>Quadrangle</tt>.
	 *
	 * @param tl the top left point
	 * @param tr the top right point
	 * @param bl the bottom left point
	 * @param br the bottom right point
	 */
	public Quadrangle(Point tl, Point tr, Point bl, Point br)
	{
		this.tl = tl;
		this.tr = tr;
		this.bl = bl;
		this.br = br;
		Point[] ps = { tl, tr, br, bl };
		for (int i = 0; i < ps.length; i++)
		{
			this.xpoints[i] = ps[i].x;
			this.ypoints[i] = ps[i].y;
		}
		this.npoints = 4;
	}

	public Quadrangle(Rectangle rect)
	{
		this(new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y),
				new Point(rect.x, rect.y + rect.height),
				new Point(rect.x + rect.width, rect.y + rect.height));
	}

	/**
	 * @deprecated A quadrangle cannot add points
	 * @see java.awt.Polygon#addPoint(int, int)
	 */
	@Deprecated
	@Override
	public void addPoint(int x, int y)
	{
		throw new UnsupportedOperationException("A quadrangle cannot add points.");
	}

	/**
	 * Returns the bottom left point.
	 *
	 * @return the bottom left point.
	 */
	public Point getBottomLeftPoint()
	{
		return this.bl;
	}

	/**
	 * Returns the bottom right point.
	 *
	 * @return the bottom right point.
	 */
	public Point getBottomRightPoint()
	{
		return this.br;
	}

	/**
	 * Returns the top left point.
	 *
	 * @return the top left point.
	 */
	public Point getTopLeftPoint()
	{
		return this.tl;
	}

	/**
	 * Returns the top right point.
	 *
	 * @return the top right point.
	 */
	public Point getTopRightPoint()
	{
		return this.tr;
	}

	/**
	 * Set the bottom left point.
	 *
	 * @param bl the value of the bottom left point
	 */
	public void setBottomLeftPoint(Point bl)
	{
		this.bl = bl;
	}

	/**
	 * Set the bottom right point.
	 *
	 * @param br the value of the bottom right point
	 */
	public void setBottomRightPoint(Point br)
	{
		this.br = br;
	}

	/**
	 * Set the top left point.
	 *
	 * @param tl the value of the top left point
	 */
	public void setTopLeftPoint(Point tl)
	{
		this.tl = tl;
	}

	/**
	 * Set the top right point.
	 *
	 * @param tr the value of the top right point
	 */
	public void setTopRightPoint(Point tr)
	{
		this.tr = tr;
	}

}

/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * LinearChangeMask.java is PROPRIETARY/CONFIDENTIAL built in 3:26:04 PM, Jul 5,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math.mask;

import java.awt.geom.Point2D;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * The linear change mask.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class LinearChangeMask implements Mask
{
	protected Vector2D	start, end;
	protected double	length;
	protected Line		line;
	protected float		v0, vt, body;

	public LinearChangeMask(float x0, float y0, float xt, float yt, float v0, float vt)
	{
		this(new Point2D.Float(x0, x0), new Point2D.Float(xt, yt), v0, vt);
	}

	public LinearChangeMask(Point2D start, Point2D end, float v0, float vt)
	{
		this.start = this.toPoint(start);
		this.end = this.toPoint(end);
		this.length = this.start.distance(this.end);
		this.line = new Line(this.start, this.end, 1e-10);
		this.v0 = v0;
		this.vt = vt;
		this.body = vt - v0;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.mask.Mask#mask(int, int)
	 */
	@Override
	public float mask(int x, int y)
	{
		Point<Euclidean2D> p = this.line.project(new Vector2D(x, y));
		double d0 = p.distance(this.start);
		if (d0 > this.length)
			return this.vt;
		double dt = p.distance(this.end);
		if (dt > this.length)
			return this.v0;
		return (float) (d0 / this.length * (this.vt - this.v0) + this.v0);
	}

	protected Vector2D toPoint(Point2D p)
	{
		return new Vector2D(p.getX(), p.getY());
	}

}

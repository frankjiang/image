/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * OvalChangeMask.java is PROPRIETARY/CONFIDENTIAL built in 5:26:44 PM, Jul 5,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math.mask;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

/**
 * The
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class OvalChangeMask implements Mask
{
	protected double	x, y, a, b;
	protected float		v0, vt;

	/**
	 * Construct an instance of <tt>OvalChangeMask</tt>.
	 *
	 * @param center
	 * @param a
	 * @param b
	 * @param v0 the coefficient in the center
	 * @param vt the coefficient on the border
	 */
	public OvalChangeMask(double x, double y, double a, double b, float v0, float vt)
	{
		this.x = x;
		this.y = y;
		this.a = a;
		this.b = b;
		this.v0 = v0;
		this.vt = vt;
	}

	/**
	 * Construct an instance of <tt>OvalChangeMask</tt>.
	 *
	 * @param center
	 * @param a
	 * @param b
	 * @param v0 the coefficient in the center
	 * @param vt the coefficient on the border
	 */
	public OvalChangeMask(Point2D center, double a, double b, float v0, float vt)
	{
		this(center.getX(), center.getY(), a, b, v0, vt);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.mask.Mask#mask(int, int)
	 */
	@Override
	public float mask(int x, int y)
	{
		double px = (x - this.x) / this.a;
		double py = (y - this.y) / this.b;
		px *= px;
		py *= py;
		if (px + py > 1)
			return this.vt;
		else
			return (float) ((this.vt - this.v0) * FastMath.sqrt(px + py) + this.v0);
	}

}

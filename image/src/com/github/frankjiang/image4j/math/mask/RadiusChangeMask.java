/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * RadiusChangeMask.java is PROPRIETARY/CONFIDENTIAL built in 5:26:00 PM, Jul 5,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math.mask;

import java.awt.geom.Point2D;

/**
 * The radius change mask.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class RadiusChangeMask extends OvalChangeMask
{

	/**
	 * Construct an instance of <tt>RadiusChangeMask</tt>.
	 * 
	 * @param x
	 * @param y
	 * @param a
	 * @param b
	 * @param v0 the coefficient in the center
	 * @param vt the coefficient on the border
	 */
	public RadiusChangeMask(double x, double y, double r, float v0, float vt)
	{
		super(x, y, r, r, v0, vt);
	}

	/**
	 * Construct an instance of <tt>RadiusChangeMask</tt>.
	 * 
	 * @param center
	 * @param a
	 * @param b
	 * @param v0 the coefficient in the center
	 * @param vt the coefficient on the border
	 */
	public RadiusChangeMask(Point2D center, double r, float v0, float vt)
	{
		this(center.getX(), center.getY(), r, v0, vt);
	}

}

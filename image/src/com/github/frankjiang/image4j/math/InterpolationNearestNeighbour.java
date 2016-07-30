/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * InterpolationNearestNeighbour.java is PROPRIETARY/CONFIDENTIAL built in
 * 11:02:07 AM, Jun 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math;

/**
 * The interpolation for nearset neighbour.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class InterpolationNearestNeighbour implements Interpolation
{

	/**
	 * @see com.github.frankjiang.image4j.math.Interpolation#interpolate(float,
	 *      float, int, int, int, int)
	 */
	@Override
	public int interpolate(float ex, float ey, int p00, int p01, int p10, int p11)
	{
		if (ex < 0.5f)
			return ey < 0.5f ? p00 : p01;
		else
			return ey < 0.5f ? p10 : p11;
	}

}

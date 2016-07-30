/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * InterpolationBicubic.java is PROPRIETARY/CONFIDENTIAL built in 11:04:21 AM,
 * Jun 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math;

/**
 * The bicubic interpolation.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class InterpolationBicubic implements Interpolation
{

	/**
	 * @see com.github.frankjiang.image4j.math.Interpolation#interpolate(float,
	 *      float, int, int, int, int)
	 */
	@Override
	public int interpolate(float ex, float ey, int p00, int p01, int p10, int p11)
	{
		int p = Math.round((1 - ex) * (1 - ey) * p00 + (1 - ex) * ey * p01 + ex * (1 - ey) * p10
				+ ex * ey * p11);
		return p > 255 ? 255 : p < 0 ? 0 : p;
	}

}

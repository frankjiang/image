/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Interpolation.java is PROPRIETARY/CONFIDENTIAL built in 10:58:16 AM, Jun 28,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math;

/**
 * The interpolation interface.
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface Interpolation
{
	/**
	 * Calculate the interpolation of the specified position.
	 *
	 * @param ex the distance of X coordinate between new position and original
	 *            position
	 * @param ey the distance of Y coordinate between new position and original
	 *            position
	 * @param p00 the top-left value
	 * @param p01 the top-right value
	 * @param p10 the bottom-left value
	 * @param p11 the bottom-right value
	 * @return the interpolation value
	 */
	int interpolate(float ex, float ey, int p00, int p01, int p10, int p11);
}

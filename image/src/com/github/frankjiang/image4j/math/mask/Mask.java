/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Mask.java is PROPRIETARY/CONFIDENTIAL built in 3:22:05 PM, Jul 5, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math.mask;

/**
 * The mask interface.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface Mask
{
	/**
	 * Returns the mask coefficient of the specified position.
	 * <p>
	 * The mask coefficient is used to enhance (&gt;1.0) or reduce (&lt;1.0) the
	 * effect.
	 * </p>
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the mask coefficient
	 */
	public float mask(int x, int y);
}

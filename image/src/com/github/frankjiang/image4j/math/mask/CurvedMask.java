/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CurvedMask.java is PROPRIETARY/CONFIDENTIAL built in 5:27:39 PM, Jul 5, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.math.mask;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * T
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CurvedMask implements Mask
{
	protected UnivariateFunction	curve;
	protected Mask					mask;

	public CurvedMask(UnivariateFunction curve, Mask mask)
	{
		this.curve = curve;
		this.mask = mask;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.mask.Mask#mask(int, int)
	 */
	@Override
	public float mask(int x, int y)
	{
		return (float) this.curve.value(this.mask.mask(x, y));
	}

}

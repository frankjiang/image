/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * FuncBasedEnhanceOp.java is PROPRIETARY/CONFIDENTIAL built in 4:34:37 PM, Jul
 * 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.color;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * An function based enhancement class.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class FuncBasedEnhanceOp extends EnhanceOp
{
	/**
	 * The inner function for transformation.
	 */
	protected UnivariateFunction function;

	/**
	 * Construct an instance of <tt>FuncBasedEnhanceOp</tt>.
	 *
	 * @param channels the channels flag
	 * @param function the inner function
	 * @see EnhanceOp#channels
	 */
	public FuncBasedEnhanceOp(int channels, UnivariateFunction function)
	{
		super(channels);
		this.function = function;
	}

	/**
	 * Construct an instance of <tt>FuncBasedEnhanceOp</tt>.
	 *
	 * @param function the inner function
	 */
	public FuncBasedEnhanceOp(UnivariateFunction function)
	{
		super(EnhanceOp.TYPE_RGB);
		this.function = function;
	}

	/**
	 * @see com.github.frankjiang.image4j.color.EnhanceOp#perform(int)
	 */
	@Override
	protected int perform(int value)
	{
		return (int) FastMath.round(this.function.value(value));
	}

}

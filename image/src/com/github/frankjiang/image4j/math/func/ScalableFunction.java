/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ScalableFunction.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

/**
 * The scalabel function.
 * <p>
 * A scalable function is a function whose output value is scalable.<br>
 * SF(x) = A * F(x)<br< A: scale parameter
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface ScalableFunction extends Function
{
	/**
	 * Returns the scale parameter.
	 * <p>
	 * The scale parameter scales the output value of the original function.
	 * </p>
	 *
	 * @return the scale parameter
	 */
	public double getScale();

	/**
	 * Returns the scaled output value.
	 * <p>
	 * SF(x) = A * F(x)
	 * </p>
	 *
	 * @param r
	 *            the input value
	 * @return the scaled output value
	 */
	public double scaledFunction(double r);

	/**
	 * Set the scale parameter.
	 * <p>
	 * The scale parameter scales the output value of the original function.
	 * </p>
	 *
	 * @param scale
	 *            the scale parameter
	 */
	public void setScale(double scale);
}

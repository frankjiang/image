/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. InvertTransform.java is PROPRIETARY/CONFIDENTIAL built in
 * 2013. Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

/**
 * Image inversion.
 * <p>
 * In this class defines the inverse action of image. Inverse transform obeys
 * the formula:
 *
 * <pre>
 * {@code s} = {@code L} - 1 - {@code r}
 * {@code L}: the scale level
 * {@code r}: the pixel input
 * {@code s}: the pixel output
 * </pre>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @param <T>
 *            image type
 * @version 1.0.0
 */
public class InvertTransform implements Transform
{

	/**
	 * @see com.frank.dip.enhance.Transformation#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return "s = L - 1 - r";//$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Transform#getInverseTransform()
	 */
	@Override
	public Function getInverseTransform() throws UnsupportedOperationException
	{
		return new InvertTransform();
	}

	/**
	 * Returns an empty properties instance of current inversion transform, due
	 * to this transform needs no parameter.
	 *
	 * @see com.frank.dip.enhance.Transformation#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		return new Properties();
	}

	/**
	 * Do nothing, due to this transform needs no parameter.
	 *
	 * @see com.frank.dip.enhance.Transformation#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("s = %d - r", 255);//$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.enhance.time.Transform#toString()
	 */
	@Override
	public String toString()
	{
		return this.toFunction();
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		return 255 - r;
	}
}

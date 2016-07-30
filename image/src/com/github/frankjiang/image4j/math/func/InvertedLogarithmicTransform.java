/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. LogarithmicTransform.java is PROPRIETARY/CONFIDENTIAL built in
 * 2013. Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

import org.apache.commons.math3.util.FastMath;

/**
 * The inverted logarithmic transformation.
 * <p>
 * The logarithmic transformation is transformation which specified formula:
 *
 * <pre>
 * {@code s} = {@code c} * log(1 + {@code r}) + {@code b}
 * {@code r} = e<sup>({@code s}-{@code b})/{@code c}</sup> - 1
 * {@code c}: the constant value
 * {@code b}: the constant value
 * {@code r}: the pixel input
 * {@code s}: the pixel output
 * </pre>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class InvertedLogarithmicTransform implements Transform
{
	/**
	 * The string represents the parameter {@code c}.
	 */
	public static final String	PARAM_C	= "c";							//$NON-NLS-1$
	/**
	 * The string represents the parameter {@code b}.
	 */
	public static final String	PARAM_B	= "b";							//$NON-NLS-1$

	/**
	 * The constant value {@code c}.
	 */
	protected double			c;

	/**
	 * The constant value {@code b}.
	 */
	protected double			b;

	/**
	 * Construct an instance of <tt>LogarithmicTransform</tt> with default
	 * parameters.
	 *
	 * <pre>
	 * {@code c} = 256 / log(256)
	 * {@code b} = 0.0
	 * </pre>
	 */
	public InvertedLogarithmicTransform()
	{
		this(256 / Math.log(256), 0.0);
	}

	/**
	 * Construct an instance of <tt>LogarithmicTransform</tt> with
	 * specified constant value {@code c}.
	 *
	 * @param c the constant value {@code c}
	 * @param b the constant value {@code b}
	 */
	public InvertedLogarithmicTransform(double c, double b)
	{
		this.c = c;
		this.b = b;
	}

	/**
	 * Getter for the constant value {@code b}.
	 *
	 * @return the constant value {@code b}
	 */
	public double getB()
	{
		return this.b;
	}

	/**
	 * Getter for the constant value {@code c}.
	 *
	 * @return the constant value {@code c}
	 */
	public double getC()
	{
		return this.c;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return "s = exp( (r - b) / c ) - 1";//$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Transform#getInverseTransform()
	 */
	@Override
	public Function getInverseTransform() throws UnsupportedOperationException
	{
		return new LogarithmicTransform(this.c, this.b);
	}

	/**
	 * Returns an empty properties instance of current inversion transformation,
	 * due to this transformation needs no parameter.
	 *
	 * @see com.github.frankjiang.image4j.math.func.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(InvertedLogarithmicTransform.PARAM_C, this.c);
		p.put(InvertedLogarithmicTransform.PARAM_B, this.b);
		return p;
	}

	/**
	 * Setter for the constant value {@code b}.
	 *
	 * @param b the constant value {@code b}
	 */
	public void setB(double b)
	{
		this.b = b;
	}

	/**
	 * Setter for the constant value {@code c}.
	 *
	 * @param c the constant value {@code c}
	 */
	public void setC(double c)
	{
		this.c = c;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Function#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
		Object obj = null;
		obj = p.get(InvertedLogarithmicTransform.PARAM_C);
		if (obj != null && obj instanceof Number)
			this.c = ((Number) obj).doubleValue();
		obj = p.get(InvertedLogarithmicTransform.PARAM_B);
		if (obj != null && obj instanceof Number)
			this.b = ((Number) obj).doubleValue();
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("s = exp( (r%+f) / %f ) - 1", -this.b, this.c);//$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#toString()
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
		return FastMath.expm1((r - this.b) / this.c);
	}
}

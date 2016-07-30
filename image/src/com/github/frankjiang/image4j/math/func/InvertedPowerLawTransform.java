/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. PowerLawTransform.java is PROPRIETARY/CONFIDENTIAL built in
 * 2013. Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

import org.apache.commons.math3.util.FastMath;

import com.github.frankjiang.image4j.color.EnhanceOp;

/**
 * The inverted power-law transformation.
 * <p>
 * In this transform, the power-law transformation will be performed on red,
 * green and blue channels.
 * </p>
 *
 * <pre>
 * {@code s} = {@code c} * ({@code r} / ({@code L}-1)) ^ <code>&gamma;</code> + {@code b}
 * {@code r} = ( (s - b) / c ) ^ ( 1/<code>&gamma;</code> )
 * {@code c}: the constant value
 * {@code b}: the constant value
 * {@code L}: the scale level
 * <code>&gamma;</code>: the gamma parameter
 * {@code r}: the pixel input
 * {@code s}: the pixel output
 * </pre>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class InvertedPowerLawTransform implements Transform
{
	/**
	 * The string represents the parameter {@code c}.
	 */
	public static final String	PARAM_C		= "c";												//$NON-NLS-1$
	/**
	 * The string represents the parameter {@code b}.
	 */
	public static final String	PARAM_B		= "b";												//$NON-NLS-1$
	/**
	 * The string represents the parameter <code>&gamma;</code>.
	 */
	public static final String	PARAM_GAMMA	= "\u03b3";					 //$NON-NLS-1$
	/**
	 * The constant value {@code c}.
	 */
	protected double			c;
	/**
	 * The constant value <code>&gamma;</code>.
	 */
	protected double			gamma;
	/**
	 * The constant value {@code b}.
	 */
	protected double			b;

	/**
	 * Construct an instance of <tt>PowerLawTransform</tt> with default
	 * parameters.
	 *
	 * <pre>
	 * {@code c} = {@linkplain EnhanceOp#SCALE_LEVEL} - 1
	 * <code>&gamma;</code> = 1.0
	 * {@code b} = 0.0
	 * </pre>
	 */
	public InvertedPowerLawTransform()
	{
		this.c = 255;
		this.b = 0;
		this.gamma = 1;
	}

	/**
	 * Construct an instance of <tt>PowerLawTransform</tt> with specified
	 * parameters.
	 *
	 * @param gamma the constant value of <code>&gamma;</code>
	 */
	public InvertedPowerLawTransform(double gamma)
	{
		this(255, gamma, 0);
	}

	/**
	 * Construct an instance of <tt>PowerLawTransform</tt> with specified
	 * parameters.
	 *
	 * @param c
	 *            the constant value {@code c}
	 * @param gamma
	 *            the constant value of <code>&gamma;</code>
	 * @param b
	 *            the constant value {@code b}
	 */
	public InvertedPowerLawTransform(double c, double gamma, double b)
	{
		this.c = c;
		this.b = b;
		this.gamma = gamma;
		if (gamma < 0)
			throw new IllegalArgumentException("The parameter \u03b3 must be positive.");
	}

	/**
	 * Getter for the constant value {@code b}.
	 *
	 * @return {@code b}
	 */
	public double getB()
	{
		return this.b;
	}

	/**
	 * Getter for the constant value c.
	 *
	 * @return {@code c}
	 */
	public double getC()
	{
		return this.c;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.enhance.time.Transform#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return "s = (L-1) * ((s-b) / c) ^ (1 / \u03b3)";//$NON-NLS-1$
	}

	/**
	 * Getter for constant value <code>&gamma;</code>.
	 *
	 * @return <code>&gamma;</code>.
	 */
	public double getGamma()
	{
		return this.gamma;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.Transform#getInverseTransform()
	 */
	@Override
	public Function getInverseTransform() throws UnsupportedOperationException
	{
		return new PowerLawTransform(this.c, this.gamma, this.b);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.enhance.time.Transform#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(InvertedPowerLawTransform.PARAM_C, this.c);
		p.put(InvertedPowerLawTransform.PARAM_B, this.b);
		p.put(InvertedPowerLawTransform.PARAM_GAMMA, this.gamma);
		return p;
	}

	/**
	 * Setter for the constant value {@code b}.
	 *
	 * @param b
	 *            the value of {@code b}
	 */
	public void setB(double b)
	{
		this.b = b;
	}

	/**
	 * Setter for the constant value {@code c}.
	 *
	 * @param c
	 *            the value of {@code c}
	 */
	public void setC(double c)
	{
		this.c = c;
	}

	/**
	 * Setter for <code>&gamma;</code>.
	 *
	 * @param gamma
	 *            the value of <code>&gamma;</code>.
	 */
	public void setGamma(double gamma)
	{
		this.gamma = gamma;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.enhance.time.Transform#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
		Object obj = null;
		obj = p.get(InvertedPowerLawTransform.PARAM_C);
		if (obj != null && obj instanceof Number)
			this.c = ((Number) obj).doubleValue();
		obj = p.get(InvertedPowerLawTransform.PARAM_B);
		if (obj != null && obj instanceof Number)
			this.b = ((Number) obj).doubleValue();
		obj = p.get(InvertedPowerLawTransform.PARAM_GAMMA);
		if (obj != null && obj instanceof Number)
			this.gamma = ((Number) obj).doubleValue();
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("r = %d * ((s%+f) / %f) ^ %f", 255, -this.b, this.c, 1 / this.gamma);//$NON-NLS-1$
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
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.enhance.time.Transform#value(double)
	 */
	@Override
	public double value(double r)
	{
		return 255 * FastMath.pow((r - this.b) / this.c, 1 / this.gamma);
	}
}

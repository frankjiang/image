/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. Quadratic.java is PROPRIETARY/CONFIDENTIAL built in 2013. Use is
 * subject to license terms.
 */
package com.github.frankjiang.image4j.math;

import java.util.Properties;

import com.github.frankjiang.image4j.math.func.Function;

/**
 * The quadratic function.
 *
 * <pre>
 * f(x) = Ax<sup>2</sup> + Bx + C
 * </pre>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Quadratic implements Function
{
	/**
	 * The parameter string.
	 */
	public static final String	PARAM_A	= "A", PARAM_B = "B", PARAM_C = "C";
	/**
	 * The quadratic parameters.
	 */
	protected double			a, b, c;

	/**
	 * Construct an instance of <tt>Quadratic</tt>.
	 *
	 * <pre>
	 * f(x) = Ax<sup>2</sup> + Bx + C
	 * </pre>
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public Quadratic(double a, double b, double c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return "Ax^2 + Bx + C = 0";
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(Quadratic.PARAM_A, this.a);
		p.put(Quadratic.PARAM_B, this.b);
		p.put(Quadratic.PARAM_C, this.c);
		return p;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
		Object obj = null;
		obj = p.get(Quadratic.PARAM_A);
		if (obj != null && obj instanceof Number)
			this.a = ((Number) obj).doubleValue();
		obj = p.get(Quadratic.PARAM_B);
		if (obj != null && obj instanceof Number)
			this.b = ((Number) obj).doubleValue();
		obj = p.get(Quadratic.PARAM_C);
		if (obj != null && obj instanceof Number)
			this.c = ((Number) obj).doubleValue();
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("%f*x^2%+f * x%+f = 0", this.a, this.b, this.c);//$NON-NLS-1$
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
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		return this.a * r * r + this.b * r + this.c;
	}
}

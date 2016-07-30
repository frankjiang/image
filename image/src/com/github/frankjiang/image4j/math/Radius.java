/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * DistanceRadius.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math;

import java.util.Properties;

import com.github.frankjiang.image4j.math.func.ScalableFunction;

/**
 * The radius distance interrupt filter function.
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Radius implements ScalableFunction
{
	/**
	 * The parameter string.
	 */
	public static final String	PARAM_D		= "d";
	/**
	 * The radius of Fourier radius low pass filter.
	 */
	protected double			radius;
	/**
	 * If <tt>true</tt>, the filter will be low pass, otherwise high pass.
	 */
	protected boolean			isLowPass	= true;
	/**
	 * The scale parameter.
	 */
	protected double			scale;

	/**
	 * Construct an instance of <tt>DistanceRadius</tt>.
	 *
	 * @param radius
	 *            the radius of pass
	 * @param isLowPass
	 *            if <tt>true</tt>, the filter will be low pass, otherwise high
	 *            pass
	 */
	public Radius(double radius, boolean isLowPass)
	{
		this.radius = radius;
		this.isLowPass = isLowPass;
		this.scale = 255;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		if (this.isLowPass)
			return "H(u,v) = {1, D(u,v)<D0; 0, otherwise}";//$NON-NLS-1$
		else
			return "H(u,v) = {0, D(u,v)<D0; 1, otherwise}";//$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(Radius.PARAM_D, this.radius);
		return p;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.ScalableFunction#getScale()
	 */
	@Override
	public double getScale()
	{
		return this.scale;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.ScalableFunction#scaledFunction(double)
	 */
	@Override
	public double scaledFunction(double r)
	{
		return this.scale * this.value(r);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
		Object obj = null;
		obj = p.get(Radius.PARAM_D);
		if (obj != null && obj instanceof Number)
			this.radius = ((Number) obj).doubleValue();
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.ScalableFunction#setScale(double)
	 */
	@Override
	public void setScale(double scale)
	{
		this.scale = scale;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		if (this.isLowPass)
			return String.format("H(u,v) = {1, D(u,v)<%f; 0, otherwise}", this.radius);//$NON-NLS-1$
		else
			return String.format("H(u,v) = {0, D(u,v)<%f; 1, otherwise}", this.radius);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		return this.isLowPass ^ r < this.radius ? 0 : 1;
	}
}

/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ButternworthFuntion.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

/**
 * Function of Butterworth low pass filter.
 * <p>
 * H<sub>lp</sub>(u,v) = 1 / (1 + [D(u,v) + D<sub>0</sub>]<sup>2n</sup>)<br>
 * H<sub>hp</sub> = 1 - H<sub>lp</sub>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ButternworthFunction implements ScalableFunction
{
	/**
	 * The parameter strings.
	 */
	public static final String	PARAM_D		= "d",
										PARAM_N = "n";
	/**
	 * The interrupt distance D<sub>0</sub>.
	 */
	protected double			d;
	/**
	 * The power parameter n.
	 */
	protected int				n;
	/**
	 * The scale parameter.
	 */
	protected double			scale;
	/**
	 * If <tt>true</tt>, the filter will be low pass, otherwise high pass.
	 */
	protected boolean			isLowPass	= true;

	/**
	 * Construct an instance of <tt>ButternworthFuntion</tt>.
	 *
	 * @param d
	 *            the interrupt distance D<sub>0</sub>
	 * @param n
	 *            the power parameter n
	 * @param scale
	 *            the scale parameter used for scaling output value
	 * @param isLowPass
	 *            if <tt>true</tt>, the filter will be low pass, otherwise high
	 *            pass
	 */
	public ButternworthFunction(double d, int n, double scale, boolean isLowPass)
	{
		if (d <= 0)
			throw new IllegalArgumentException(
					String.format("Butternworth interrupt distance(%f) must be positive.", d));
		this.d = d;
		this.n = n;
		this.scale = scale;
		this.isLowPass = isLowPass;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		if (this.isLowPass)
			return "H(u,v) = 1 / (1 + (D(u,v) / D0)^2n)"; //$NON-NLS-1$
		else
			return "H(u,v) = 1 / (1 + (D0 / D(u,v))^2n)"; //$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(ButternworthFunction.PARAM_D, this.d);
		p.put(ButternworthFunction.PARAM_N, this.n);
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
		obj = p.get(ButternworthFunction.PARAM_D);
		if (obj != null && obj instanceof Number)
			this.d = ((Number) obj).doubleValue();
		obj = p.get(ButternworthFunction.PARAM_N);
		if (obj != null && obj instanceof Number)
			this.n = ((Number) obj).intValue();
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
			return String.format("H(u,v) = 1 / (1 + (D(u,v)/%f)^%d)", this.d, 2 * this.n);
		else
			return String.format("H(u,v) = 1 / (1 + (%f/D(u,v))^%d)", this.d, 2 * this.n);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		if (this.isLowPass)
			return 1 / (1 + Math.pow(r / this.d, 2 * this.n));
		else
			return 1 / (1 + Math.pow(this.d / r, 2 * this.n));
	}
}

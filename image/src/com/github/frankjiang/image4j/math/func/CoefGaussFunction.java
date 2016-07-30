/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CoefGuass.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

/**
 * The coefficient Gauss function.
 * <p>
 * H<sub>lp</sub>(u,v) = exp(-c * [D(u,v) / D<sub>0</sub>]<sup>2</sup>)<br>
 * H<sub>hp</sub>(u,v) = 1 - H<sub>lp</sub>(u,v)
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CoefGaussFunction implements ScalableFunction
{
	/**
	 * The parameter string &sigma;(D<sub>0</sub>).
	 */
	public static final String	PARAM_SIGMA	= "sigma",
										PARAM_C = "c";
	/**
	 * The interrupt distance &sigma;(D<sub>0</sub>).
	 */
	protected double			sigma;
	/**
	 * The gradient coefficient {@code c}.
	 */
	protected double			coef;
	/**
	 * The scale parameter.
	 */
	protected double			scale;
	/**
	 * If <tt>true</tt>, the filter will be low pass, otherwise high pass.
	 */
	protected boolean			isLowPass	= true;

	/**
	 * Construct an instance of <tt>GaussFuntion</tt>.
	 *
	 * @param sigma
	 *            the interrupt distance &sigma;(D<sub>0</sub>)
	 * @param coef
	 *            the gradient coefficient {@code c}
	 * @param scale
	 *            the scale parameter used for scaling output value
	 * @param isLowPass
	 *            if <tt>true</tt>, the filter will be low pass, otherwise high
	 *            pass
	 */
	public CoefGaussFunction(double sigma, double coef, double scale, boolean isLowPass)
	{
		if (sigma <= 0)
			throw new IllegalArgumentException(String
					.format("Gauss interrupt distance sigma = (%f) must be positive.", sigma));
		this.sigma = sigma;
		this.coef = coef;
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
			return "H(u,v) = exp(-c * D\u00b2(u,v) * sigma\u00b2)"; //$NON-NLS-1$
		else
			return "H(u,v) = 1 - exp(-c * D\u00b2(u,v) / 2 * sigma\u00b2)"; //$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(CoefGaussFunction.PARAM_SIGMA, this.sigma);
		p.put(CoefGaussFunction.PARAM_C, this.coef);
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
		obj = p.get(CoefGaussFunction.PARAM_SIGMA);
		if (obj != null && obj instanceof Number)
			this.sigma = ((Number) obj).doubleValue();
		obj = p.get(CoefGaussFunction.PARAM_C);
		if (obj != null && obj instanceof Number)
			this.coef = ((Number) obj).doubleValue();
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
			return String.format("H(u,v) = exp(D\u00b2(u,v) / %f)",//$NON-NLS-1$
					-this.sigma * this.sigma * this.coef);
		else
			return String.format("H(u,v) = 1 - exp(D\u00b2(u,v) / %f)",//$NON-NLS-1$
					-this.sigma * this.sigma * this.coef);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		if (this.isLowPass)
			return Math.exp(-this.coef * Math.pow(r / this.sigma, 2.0));
		else
			return 1 - Math.exp(-this.coef * Math.pow(r / this.sigma, 2.0));
	}
}

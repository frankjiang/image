/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * HomomorphicEnhanceFilterFunction.java is PROPRIETARY/CONFIDENTIAL built in
 * 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

/**
 * The homomorphic enhance filter.
 * <p>
 * H<sup>*</sup>(u,v) = (&gamma;<sub>h</sub> - &gamma;<sub>l</sub>) * H(u,v) +
 * &gamma;<sub>l</sub><br>
 * H(u,v): original frequency filter<br>
 * &gamma;<sub>h</sub>: the limit parameter for high frequency<br>
 * &gamma;<sub>l</sub>: the limit parameter for low frequency
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class HomomorphicEnhanceFilterFunction implements ScalableFunction
{
	/**
	 * The parameters.
	 */
	public static final String	PARAM_H	= "h", PARAM_L = "l";
	/**
	 * The distance base filter function.
	 */
	protected Function			function;
	/**
	 * The limit parameter &gamma;<sub>h</sub> for high frequency.
	 */
	protected double			high;
	/**
	 * The limit parameter &gamma;<sub>l</sub> for low frequency.
	 */
	protected double			low;
	/**
	 * The scale parameter.
	 */
	protected double			scale;

	/**
	 * Construct an instance of <tt>HomomorphicEnhanceFilterFunction</tt>.
	 *
	 * @param function
	 *            the frequency domain filter function
	 * @param high
	 *            the limit parameter &gamma;<sub>h</sub> for high frequency
	 * @param low
	 *            the limit parameter &gamma;<sub>l</sub> for low frequency
	 */
	public HomomorphicEnhanceFilterFunction(Function function, double high, double low)
	{
		this.function = function;
		this.high = high;
		this.low = low;
		this.scale = 128;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return String.format("H(u,v) = (h - l) * ( %s ) + l", //$NON-NLS-1$
				this.split(this.function.getFunctionString()));
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = this.function.getProperties();
		p.put(HomomorphicEnhanceFilterFunction.PARAM_H, this.high);
		p.put(HomomorphicEnhanceFilterFunction.PARAM_L, this.low);
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
		obj = p.get(HomomorphicEnhanceFilterFunction.PARAM_H);
		if (obj != null && obj instanceof Number)
			this.high = ((Number) obj).doubleValue();
		obj = p.get(HomomorphicEnhanceFilterFunction.PARAM_L);
		if (obj != null && obj instanceof Number)
			this.low = ((Number) obj).doubleValue();
		this.function.setProperties(p);
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
	 * Returns the splitted function string.
	 *
	 * @param s
	 *            the function string
	 * @return the splitted function
	 */
	private String split(String s)
	{
		int idx = s.indexOf("= ");
		if (idx != -1)
			return s.substring(idx + 2);
		{
			idx = s.indexOf('=');
			if (idx != -1)
				return s.substring(idx + 1);
			else
				return s;
		}
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("H(u,v) = %f*(%s)%+f", this.high - this.low, this.low, //$NON-NLS-1$
				this.split(this.function.getFunctionString()));
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#value(double)
	 */
	@Override
	public double value(double r)
	{
		return (this.high - this.low) * this.function.value(r) + this.low;
	}
}

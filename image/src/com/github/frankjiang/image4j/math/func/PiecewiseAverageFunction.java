/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. PiecewiseAverageFunction.java is PROPRIETARY/CONFIDENTIAL built in
 * 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

/**
 * A piecewise average function.
 *
 * <pre>
 * f(x) =
 * weight / size, x&isin;[begin, end)
 * 0, otherwise
 *
 * <strong>weight</strong>: the weight of average calculating
 * <strong>begin</strong>: begin value of the region
 * <strong>end</strong>: end value of the region
 * </pre>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class PiecewiseAverageFunction implements Function
{
	/**
	 * The parameter strings.
	 */
	public static final String	PARAM_BEGIN	= "begin",												//$NON-NLS-1$
			PARAM_END = "end", PARAM_WEIGHT = "weight";						//$NON-NLS-1$//$NON-NLS-2$
	/**
	 * The begin value of the region.
	 */
	protected double			begin;
	/**
	 * The end value of the region.
	 */
	protected double			end;
	/**
	 * The weight of average calculating.
	 */
	protected double			weight;
	/**
	 * The division value of {@code weight} / ({@code end} - {@code begin}).
	 */
	protected double			division;

	/**
	 * Construct an instance of <tt>PiecewiseAverageFunction</tt> with specified
	 * begin
	 * value, end value and weight.
	 *
	 * @param begin
	 *            the begin value of the region
	 * @param end
	 *            the end value of the region
	 * @param weight
	 *            the weight of average calculating
	 */
	public PiecewiseAverageFunction(double begin, double end, double weight)
	{
		this.begin = begin;
		this.end = end;
		this.weight = weight;
		this.division = weight / (end - begin);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getFunctionString()
	 */
	@Override
	public String getFunctionString()
	{
		return "f(x) = {weight / (end - begin), x∈[begin,end) | 0, otherwise}";//$NON-NLS-1$
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#getProperties()
	 */
	@Override
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put(PiecewiseAverageFunction.PARAM_BEGIN, this.begin);
		p.put(PiecewiseAverageFunction.PARAM_END, this.end);
		p.put(PiecewiseAverageFunction.PARAM_WEIGHT, this.weight);
		return p;
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties p)
	{
		Object obj = null;
		obj = p.get(PiecewiseAverageFunction.PARAM_BEGIN);
		if (obj != null && obj instanceof Number)
			this.begin = ((Number) obj).doubleValue();
		obj = p.get(PiecewiseAverageFunction.PARAM_END);
		if (obj != null && obj instanceof Number)
			this.end = ((Number) obj).doubleValue();
		obj = p.get(PiecewiseAverageFunction.PARAM_WEIGHT);
		if (obj != null && obj instanceof Number)
			this.weight = ((Number) obj).doubleValue();
		this.division = this.weight / (this.end - this.begin);
	}

	/**
	 * @see com.github.frankjiang.image4j.math.func.frank.dip.math.Function#toFunction()
	 */
	@Override
	public String toFunction()
	{
		return String.format("f(x) = {%f, x∈[%f,%f) | 0, otherwise}"//$NON-NLS-1$
				, this.division, this.begin, this.end);
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
		if (r < this.begin)
			return this.begin;
		if (r > this.end)
			return this.end;
		return r / this.division;
	}
}

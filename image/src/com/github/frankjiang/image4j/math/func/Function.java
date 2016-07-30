/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. Function.java is PROPRIETARY/CONFIDENTIAL built in 2013. Use is
 * subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

import java.util.Properties;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * The function interface.
 * <p>
 * In this interface, the normal function is defined, obeying the formula:
 *
 * <pre>
 * {@code s} = T({@code r})
 * {@code r}: the input
 * {@code s}: the output
 * T: the function
 * </pre>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface Function extends UnivariateFunction
{
	/**
	 * Returns a string to represent to current function.
	 * <p>
	 * In this function string, the parameters will be represented by its
	 * parameter name.
	 * </p>
	 *
	 * @return the string to represent the function
	 */
	public String getFunctionString();

	/**
	 * Returns the properties needed by the transform.
	 *
	 * @return the properties
	 */
	public Properties getProperties();

	/**
	 * Set the needed to be changed properties in the transform.
	 *
	 * @param p
	 *            the properties to set
	 */
	public void setProperties(Properties p);

	/**
	 * Returns a string to represent to current function.
	 * <p>
	 * In this function string, the parameters will be represented by real
	 * numbers, not the parameter name.
	 * </p>
	 *
	 * @see Object#toString()
	 * @return the function string
	 */
	public String toFunction();

	/**
	 * Returns the pixel value {@code s} after the input pixel value {@code r}
	 * is transformed.
	 *
	 * @param r
	 *            the input value
	 * @return the output value
	 */
	@Override
	public double value(double r);
}

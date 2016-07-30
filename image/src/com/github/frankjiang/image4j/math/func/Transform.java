/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. Transform.java is PROPRIETARY/CONFIDENTIAL built in 2013. Use is
 * subject to license terms.
 */
package com.github.frankjiang.image4j.math.func;

/**
 * The transform interface.
 * <p>
 * In this interface, the normal function of transform is defined, obeying the
 * formula:
 *
 * <pre>
 * {@code s} = T({@code r})
 * {@code r}: the pixel input
 * {@code s}: the pixel output
 * T: the transform function
 * </pre>
 *
 * This interface contains an inverse interface selection for the current
 * transform.
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface Transform extends Function
{
	/**
	 * Returns the instance of the inverse transformation for the current
	 * transformation.
	 *
	 * @return function of the inverse transformation
	 * @throws UnsupportedOperationException
	 *             if the inverse transformation is not defined.
	 */
	public Function getInverseTransform() throws UnsupportedOperationException;
}

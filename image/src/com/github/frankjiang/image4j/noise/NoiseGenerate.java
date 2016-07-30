/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * NoiseGenerate.java is PROPRIETARY/CONFIDENTIAL built in 2:15:45 AM, Jul 2,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.noise;

/**
 * The interface of noise generating.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public interface NoiseGenerate
{
	/**
	 * Generates the noise according to the specified array and appends to
	 * values in the array.
	 *
	 * @param a the specified array
	 */
	public void generate(double[] a);

	/**
	 * Generates the noise according to the specified array and appends to
	 * values in the array.
	 *
	 * @param a the specified array
	 */
	public void generate(float[] a);

	/**
	 * Generates the noise according to the specified array and appends to
	 * values in the array.
	 *
	 * @param a the specified array
	 */
	public void generate(int[] a);

	/**
	 * Generates the noise according to the specified array and appends to
	 * values in the array.
	 *
	 * @param a the specified array
	 */
	public void generate(long[] a);
}

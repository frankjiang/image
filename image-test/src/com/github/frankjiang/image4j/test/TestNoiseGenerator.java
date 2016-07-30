/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * TestNoiseGenerator.java is PROPRIETARY/CONFIDENTIAL built in 3:20:10 AM, Jul
 * 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.test;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.github.frankjiang.image4j.noise.GaussianNoiseGenerator;
import com.github.frankjiang.image4j.noise.NoiseGenerate;

/**
 * Test cases for noise generators.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class TestNoiseGenerator
{
	NoiseGenerate generator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		generator = new GaussianNoiseGenerator(3);
	}

	@Test
	public void test()
	{
		int[] aInt = new int[10];
		long[] aLong = new long[10];
		float[] aFloat = new float[10];
		double[] aDouble = new double[10];
		generator.generate(aInt);
		generator.generate(aLong);
		generator.generate(aFloat);
		generator.generate(aDouble);
		System.out.println(Arrays.toString(aInt));
		System.out.println(Arrays.toString(aLong));
		System.out.println(Arrays.toString(aFloat));
		System.out.println(Arrays.toString(aDouble));
	}

}

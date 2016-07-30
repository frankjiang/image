/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * GaussianNoiseGenerator.java is PROPRIETARY/CONFIDENTIAL built in 2:52:23 AM,
 * Jul 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.noise;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

/**
 * The noise generator for Gaussian like noises.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class GaussianNoiseGenerator implements NoiseGenerate
{
	/**
	 * The average of the Gaussian kernel, usually called &mu;.
	 */
	protected double	mu;
	/**
	 * The standard deviation of the Gaussian kernel, usually called &sigma.
	 */
	protected double	sigma;

	/**
	 * The "normal" Gaussian random generator with &mu; = 0 and &sigma; = 1,
	 * which obeys <code>N(0,1)</code>.
	 */
	protected Random	random;

	/**
	 * Construct an instance of <tt>GaussianNoiseGenerator</tt>.
	 *
	 * @param mu the average of the Gaussian kernel, usually called &mu;.
	 * @param sigma the standard deviation of the Gaussian kernel, usually
	 *            called &sigma.
	 */
	public GaussianNoiseGenerator(double sigma)
	{
		this(0.0, sigma);
	}

	/**
	 * Construct an instance of <tt>GaussianNoiseGenerator</tt>.
	 *
	 * @param mu the average of the Gaussian kernel, usually called &mu;.
	 * @param sigma the standard deviation of the Gaussian kernel, usually
	 *            called &sigma.
	 */
	public GaussianNoiseGenerator(double mu, double sigma)
	{
		this.mu = mu;
		this.sigma = sigma;
		this.random = new Random();
	}

	/**
	 * @see com.github.frankjiang.image4j.noise.NoiseGenerate#generate(double[])
	 */
	@Override
	public void generate(double[] a)
	{
		for (int i = 0; i < a.length; i++)
			a[i] += this.next();
	}

	/**
	 * @see com.github.frankjiang.image4j.noise.NoiseGenerate#generate(float[])
	 */
	@Override
	public void generate(float[] a)
	{
		for (int i = 0; i < a.length; i++)
			a[i] += this.next();
	}

	/**
	 * @see com.github.frankjiang.image4j.noise.NoiseGenerate#generate(int[])
	 */
	@Override
	public void generate(int[] a)
	{
		for (int i = 0; i < a.length; i++)
			a[i] = (int) FastMath.round(a[i] + this.next());
	}

	/**
	 * @see com.github.frankjiang.image4j.noise.NoiseGenerate#generate(long[])
	 */
	@Override
	public void generate(long[] a)
	{
		for (int i = 0; i < a.length; i++)
			a[i] = FastMath.round(a[i] + this.next());
	}

	private double next()
	{
		double v = this.random.nextGaussian();
		v *= this.sigma;
		v += this.mu;
		return v;
	}

}

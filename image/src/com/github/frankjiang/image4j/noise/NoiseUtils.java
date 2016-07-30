/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * NoiseUtils.java is PROPRIETARY/CONFIDENTIAL built in 3:30:15 AM, Jul 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.noise;

import java.awt.image.BufferedImage;

/**
 * The noise utitilies.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class NoiseUtils
{
	public BufferedImage getGaussianNoisedImage(BufferedImage image, double sigma)
	{
		GaussianNoiseGenerator generator = new GaussianNoiseGenerator(sigma);
		return new NoisedImageOp(generator).filter(image, null);
	}

	public BufferedImage getGaussianNoisedImage(BufferedImage image, double mu, double sigma)
	{
		GaussianNoiseGenerator generator = new GaussianNoiseGenerator(mu, sigma);
		return new NoisedImageOp(generator).filter(image, null);
	}
}

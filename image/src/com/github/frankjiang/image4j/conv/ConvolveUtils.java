/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ConvolveUtils.java is PROPRIETARY/CONFIDENTIAL built in 7:41:21 PM, Jul 1,
 * 2016.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.conv;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * The convolution utilities.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ConvolveUtils
{
	public static BufferedImage convolve(BufferedImage src, int edgeCondition, RenderingHints hints,
			Kernel... kernels)
	{
		if (kernels.length == 0)
			return src;
		if (kernels.length == 1)
			return new ConvolveOp(kernels[0], edgeCondition, hints).filter(src, null);
		else
			return new MultiKernelConvolveOp(edgeCondition, hints, kernels).filter(src, null);
	}

	public static BufferedImage convolve(BufferedImage src, Kernel... kernels)
	{
		return ConvolveUtils.convolve(src, ConvolveOp.EDGE_NO_OP, null, kernels);
	}
}

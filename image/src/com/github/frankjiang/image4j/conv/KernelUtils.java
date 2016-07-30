/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * KernelUtils.java is PROPRIETARY/CONFIDENTIAL built in 6:32:31 PM, Jul 1,
 * 2016.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.conv;

import java.awt.image.Kernel;

/**
 * The utilities for creating convolve {@linkplain Kernel} instances.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class KernelUtils
{
	/**
	 * The guassian function.
	 *
	 * @param sum
	 *            value of <code>u<sup>2</sup>+v<sup>2</sup></code>
	 * @param sigma
	 *            the standard deviation
	 * @return the guassian value
	 */
	private static final float gaussian(float sum, float sigma)
	{
		sigma = sigma * sigma;
		return (float) (Math.exp(-sum / 2.0 / sigma) / Math.sqrt(2 * Math.PI * sigma));
	}

	/**
	 * Returns a Gaussian kernel with the specified parameters.
	 *
	 * @param radius the radius of a
	 * @param sigma the standard deviation of the kernel, which is usually
	 *            called the <code>&sigma;</code>
	 * @return the Gaussian kernel
	 */
	public static Kernel getGaussianKernel(int radius, float sigma)
	{
		return KernelUtils.getGaussianKernel(radius * 2 + 1, radius * 2 + 1, sigma);
	}

	/**
	 * Returns a Gaussian kernel with the specified parameters.
	 *
	 * @param width the width of the kernel
	 * @param height the height of the kernel
	 * @param sigma the standard deviation of the kernel, which is usually
	 *            called the <code>&sigma;</code>
	 * @return the Gaussian kernel
	 */
	public static Kernel getGaussianKernel(int width, int height, float sigma)
	{
		if (sigma <= 0 || width <= 0 || height <= 0)
			throw new IllegalArgumentException("Sigma value, width and height must be possitive.");
		float[] kernel = new float[width * height];
		float centerX = (width % 2 != 0 ? width - 1 : width) / 2.0f;
		float centerY = (height % 2 != 0 ? height - 1 : height) / 2.0f;
		float u, v, summary = 0.0f;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				u = centerX - x;
				v = centerY - y;
				kernel[y * width + x] = KernelUtils.gaussian(u * u + v * v, sigma);
				summary += kernel[y * width + x];
			}
		for (int i = 0; i < kernel.length; i++)
			kernel[i] /= summary;
		return new Kernel(width, height, kernel);
	}

	/**
	 * Returns a harmonic mean kernel in type 16.
	 * <table>
	 * <tr align="right">
	 * <td></td>
	 * <td width="16%">|</td>
	 * <td width="16%">1</td>
	 * <td width="16%">2</td>
	 * <td width="16%">1</td>
	 * <td width="16%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>1/16&times;</td>
	 * <td>|</td>
	 * <td>2</td>
	 * <td>4</td>
	 * <td>2</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right" >
	 * <td></td>
	 * <td>|</td>
	 * <td>1</td>
	 * <td>2</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel getHarmonicMeanKernel16()
	{
		float base = 16f;
		return KernelUtils.getSingleKernel(3, 3, 1 / base, 2 / base, 1 / base, 2 / base, 4 / base,
				2 / base, 1 / base, 2 / base, 1 / base);
	}

	/**
	 * Returns a harmonic mean kernel in type 9.
	 * <table>
	 * <tr align="right">
	 * <td></td>
	 * <td width="16%">|</td>
	 * <td width="16%">1</td>
	 * <td width="16%">1</td>
	 * <td width="16%">1</td>
	 * <td width="16%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>1/9&times;</td>
	 * <td>|</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right" >
	 * <td></td>
	 * <td>|</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel getHarmonicMeanKernel9()
	{
		float base = 9f;
		float value = 1 / base;
		return KernelUtils.getSingleKernel(3, 3, value, value, value, value, value, value, value,
				value, value);
	}

	/**
	 * Returns Kirsch kernels.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="4.3%">|</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%"></td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%"></td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">5</td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%"></td>
	 * <td width="4.3%">|</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">-3</td>
	 * <td width="4.3%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>0</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>0</td>
	 * <td>5</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>0</td>
	 * <td>5</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>0</td>
	 * <td>5</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>5</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>|</td>
	 * </tr>
	 * <tr>
	 * <td>&nbsp;</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>0</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>0</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>0</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>0</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>5</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>5</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>-3</td>
	 * <td>|</td>
	 * </tr>
	 * <table>
	 * </p>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel[] getKirschKernel()
	{
		return new Kernel[] { // 8 kernels of 3x3
				KernelUtils.getSingleKernel(3, 3, 5, 5, 5, -3, 0, -3, -3, -3, -3),
				KernelUtils.getSingleKernel(3, 3, -3, 5, 5, -3, 0, 5, -3, -3, -3),
				KernelUtils.getSingleKernel(3, 3, -3, -3, 5, -3, 0, 5, -3, -3, 5),
				KernelUtils.getSingleKernel(3, 3, -3, -3, -3, -3, 0, 5, -3, 5, 5),
				KernelUtils.getSingleKernel(3, 3, -3, -3, -3, -3, 0, -3, 5, 5, 5),
				KernelUtils.getSingleKernel(3, 3, -3, -3, -3, 5, 0, -3, 5, 5, -3),
				KernelUtils.getSingleKernel(3, 3, 5, -3, -3, 5, 0, -3, 5, -3, -3),
				KernelUtils.getSingleKernel(3, 3, 5, 5, -3, 5, 0, -3, -3, -3, -3) };
	}

	/**
	 * Returns a Laplacian kernel im type 4.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="20%">|</td>
	 * <td width="20%">0</td>
	 * <td width="20%">1</td>
	 * <td width="20%">0</td>
	 * <td width="20%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>1</td>
	 * <td>-4</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right" >
	 * <td>|</td>
	 * <td>0</td>
	 * <td>1</td>
	 * <td>0</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel getLaplacianKernel4()
	{
		return KernelUtils.getSingleKernel(3, 3, 0, 1, 0, 1, -4, 1, 0, 1, 0);
	}

	/**
	 * Laplacian kernel.
	 * <p>
	 * <table>
	 * <tr align="right" width="33%">
	 * <td width="20%">|</td>
	 * <td width="20%">1</td>
	 * <td width="20%">1</td>
	 * <td width="20%">1</td>
	 * <td width="20%">|</td>
	 * </tr>
	 * <tr align="right" width="33%">
	 * <td>|</td>
	 * <td>1</td>
	 * <td>-8</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right" >
	 * <td>|</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>1</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel getLaplacianKernel8()
	{
		return KernelUtils.getSingleKernel(3, 3, 1, 1, 1, 1, -8, 1, 1, 1, 1);
	}

	/**
	 * Returns a LoG(Laplace and Gauss) kernel, which is named as Mexican hat
	 * convolution.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="14%">|</td>
	 * <td width="14%">-2</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">2</td>
	 * <td width="14%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td width="14%">|</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">0</td>
	 * <td width="14%">8</td>
	 * <td width="14%">0</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td width="14%">|</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">8</td>
	 * <td width="14%">24</td>
	 * <td width="14%">8</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td width="14%">|</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">0</td>
	 * <td width="14%">8</td>
	 * <td width="14%">0</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td width="14%">|</td>
	 * <td width="14%">-2</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">-4</td>
	 * <td width="14%">2</td>
	 * <td width="14%">|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @return the convolve kernel
	 */
	public static Kernel getLoGKernel()
	{
		return KernelUtils.getSingleKernel(5, 5, -2, -4, -4, -4, -2, -4, 0, 8, 0, -4, -4, 8, 24, 8,
				-4, -4, 0, 8, 0, -4, -2, -4, -4, -4, -2);
	}

	/**
	 * Returns Prewitt kernels.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="9%">|</td>
	 * <td width="9%">-1</td>
	 * <td width="9%">0</td>
	 * <td width="9%">1</td>
	 * <td width="9%">|</td>
	 * <td></td>
	 * <td width="9%">|</td>
	 * <td width="9%">1</td>
	 * <td width="9%">1</td>
	 * <td width="9%">1</td>
	 * <td width="9%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>-1</td>
	 * <td>0</td>
	 * <td>1</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>0</td>
	 * <td>0</td>
	 * <td>0</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>-1</td>
	 * <td>0</td>
	 * <td>1</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-1</td>
	 * <td>-1</td>
	 * <td>-1</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @return then convolve kernels
	 */
	public static Kernel[] getPrewittKernel()
	{
		return new Kernel[] { KernelUtils.getSingleKernel(3, 3, -1, 0, 1, -1, 0, 1, -1, 0, 1),
				KernelUtils.getSingleKernel(3, 3, 1, 1, 1, 0, 0, 0, -1, -1 - 1) };
	}

	/**
	 * Returns Robert kernels.
	 * This is a mutliple kernel use {@linkplain MultiKernelConvolveOp} to
	 * filter.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="11%">|</td>
	 * <td width="11%">1</td>
	 * <td width="11%">0</td>
	 * <td width="11%">|</td>
	 * <td></td>
	 * <td width="11%">|</td>
	 * <td width="11%">0</td>
	 * <td width="11%">1</td>
	 * <td width="11%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>0</td>
	 * <td>-1</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-1</td>
	 * <td>0</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @see MultiKernelConvolveOp
	 * @return the convolve kernel
	 */
	public static Kernel[] getRobertKernel()
	{
		return new Kernel[] { KernelUtils.getSingleKernel(2, 2, 1, 0, 0, -1),
				KernelUtils.getSingleKernel(2, 2, 0, 1, -1, 0) };
	}

	/**
	 * The single kernel is kernel structure with only one matrix inside.
	 *
	 * @param width the width of the kernel
	 * @param height the height of the kernel
	 * @param values the kernel values
	 */
	protected static Kernel getSingleKernel(int width, int height, float... values)
	{
		float[] kernel = new float[width * height];
		for (int i = 0; i < kernel.length && i < values.length; i++)
			kernel[i] = values[i];
		return new Kernel(width, height, kernel);
	}

	/**
	 * Returns the Sobel kernels.
	 * <p>
	 * <table>
	 * <tr align="right">
	 * <td width="9%">|</td>
	 * <td width="9%">-1</td>
	 * <td width="9%">-2</td>
	 * <td width="9%">-1</td>
	 * <td width="9%">|</td>
	 * <td></td>
	 * <td width="9%">|</td>
	 * <td width="9%">-1</td>
	 * <td width="9%">0</td>
	 * <td width="9%">1</td>
	 * <td width="9%">|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>0</td>
	 * <td>0</td>
	 * <td>0</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-2</td>
	 * <td>0</td>
	 * <td>-2</td>
	 * <td>|</td>
	 * </tr>
	 * <tr align="right">
	 * <td>|</td>
	 * <td>1</td>
	 * <td>2</td>
	 * <td>1</td>
	 * <td>|</td>
	 * <td></td>
	 * <td>|</td>
	 * <td>-1</td>
	 * <td>0</td>
	 * <td>-1</td>
	 * <td>|</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * @return then convolve kernels
	 */
	public static Kernel[] getSobelKernel()
	{
		return new Kernel[] { KernelUtils.getSingleKernel(3, 3, -1, 0, 1, -2, 0, 2, -1, 0, 1),
				KernelUtils.getSingleKernel(3, 3, -1, -2, -1, 0, 0, 0, 1, 2, 1) };
	}
}

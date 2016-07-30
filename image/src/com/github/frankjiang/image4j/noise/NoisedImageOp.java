/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * NoisedImageOp.java is PROPRIETARY/CONFIDENTIAL built in 2:13:14 AM, Jul 2,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.noise;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.github.frankjiang.image4j.ImagingOp;

/**
 * The abstract noised image builder for noise appending operations.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class NoisedImageOp extends ImagingOp
{
	/**
	 * The noise generator.
	 */
	protected NoiseGenerate generator;

	/**
	 * Construct an instance of <tt>NoisedImageOp</tt>.
	 *
	 * @param generator the noise generator
	 */
	public NoisedImageOp(NoiseGenerate generator)
	{
		this.generator = generator;
	}

	/**
	 * Returns generator.
	 *
	 * @return the generator
	 */
	public NoiseGenerate getGenerator()
	{
		return this.generator;
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		int height = dst.getHeight();
		int width = dst.getWidth();
		Raster raster = src.getRaster();
		ColorModel model = src.getColorModel();
		int[] channels = new int[3];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
			{
				// get sources and generate noises
				Object inData = raster.getDataElements(x, y, null);
				channels[0] = model.getRed(inData);
				channels[1] = model.getGreen(inData);
				channels[2] = model.getBlue(inData);
				this.generator.generate(channels);

				// check bounds
				for (int i = 0; i < channels.length; i++)
				{
					if (channels[i] < 0)
						channels[i] = 0;
					if (channels[i] > 255)
						channels[i] = 255;
				}

				// set results
				dst.setRGB(x, y, 0xff << 24 | channels[0] << 16 | channels[1] << 8 | channels[2]);
			}
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.Raster,
	 *      java.awt.image.WritableRaster)
	 */
	@Override
	protected void operate(Raster src, WritableRaster dst)
	{
		int height = dst.getHeight();
		int width = dst.getWidth();
		Raster raster = src;
		int[] data = new int[4];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
			{
				// get sources and generate noises
				raster.getPixel(x, y, data);
				this.generator.generate(data);
				// check bounds
				for (int i = 0; i < data.length; i++)
				{
					if (data[i] < 0)
						data[i] = 0;
					if (data[i] > 255)
						data[i] = 255;
				}
				// set pixel
				dst.setPixel(x, y, data);
			}
	}

	/**
	 * Set generator.
	 *
	 * @param generator the value of generator
	 */
	public void setGenerator(NoiseGenerate generator)
	{
		this.generator = generator;
	}

}

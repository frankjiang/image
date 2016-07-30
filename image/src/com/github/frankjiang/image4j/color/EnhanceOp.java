/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. GrayTransformation.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.color;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.commons.math3.util.FastMath;

import com.github.frankjiang.image4j.ImagingOp;
import com.github.frankjiang.image4j.math.func.Transform;
import com.github.frankjiang.image4j.math.mask.Mask;

/**
 * The abstract class of image enhancement for the images whose source color
 * space is sRGB.
 * <p>
 * In this class, the procedure of image transform is defined. The procedure is
 * performing formula:
 *
 * <pre>
 * {@code s} = T({@code r})
 * {@code r}: the pixel input
 * {@code s}: the pixel output
 * T: the transform function
 * </pre>
 *
 * to all the pixels in the image.
 * </p>
 *
 * @see Transform
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public abstract class EnhanceOp extends ImagingOp
{
	/**
	 * Flag for enhancing the Alpha channel only.
	 */
	public static final int	TYPE_ALPHA		= 0x0;
	/**
	 * Flag for enhancing the Red, Green, Blue channels.
	 */
	public static final int	TYPE_RGB		= 0x7;
	/**
	 * Flag for enhancing the Red channel only.
	 */
	public static final int	TYPE_RED		= 0x1;
	/**
	 * Flag for enhancing the Green channel only.
	 */
	public static final int	TYPE_GREEN		= 0x2;
	/**
	 * Flag for enhancing the Blue channel only.
	 */
	public static final int	TYPE_BLUE		= 0x4;
	/**
	 * Flag for enhancing the Hues channel only.
	 */
	public static final int	TYPE_HUES		= 0x8;
	/**
	 * Flag for enhancing the Saturation channel only..
	 */
	public static final int	TYPE_SATURATION	= 0x10;
	/**
	 * Flag for enhancing the Brightness channel only.
	 */
	public static final int	TYPE_BRIGHTNESS	= 0x20;

	/**
	 * The flags for enhance the color channels.
	 * <p>
	 * The flags are the bits of {@linkplain #channels} value, 1 for
	 * enhancement, 0 for ignoring it.
	 * </p>
	 * <p>
	 * The flags are ordered as [Brightness, Saturation, Hue, Blue, Green, Red]
	 * from High bits to Low bits.
	 * </p>
	 * <p>
	 * For instance, value 0x7 (binary "111")
	 * which indicates to ehance the three channels of [Red, Green, Blue].
	 * </p>
	 *
	 * @see #TYPE_RGB
	 * @see #TYPE_RED
	 * @see #TYPE_GREEN
	 * @see #TYPE_BLUE
	 * @see #TYPE_HUES
	 * @see #TYPE_SATURATION
	 * @see #TYPE_BRIGHTNESS
	 */
	protected int			channels;

	/**
	 * The image mask, default <code>null</code>.
	 */
	protected Mask			mask;

	/**
	 * Construct an instance of <tt>EnhanceOp</tt>.
	 */
	protected EnhanceOp()
	{
	}

	/**
	 * Construct an instance of <tt>EnhanceOp</tt>.
	 *
	 * @param channels the channels flag
	 */
	public EnhanceOp(int channels)
	{
		this.channels = channels;
		if ((channels & 0x3f) == 0)
			throw new IllegalArgumentException("No avaliable channels flag found.");
		this.setMask(null);
	}

	/**
	 * Returns the flags for enhance the color channels.
	 *
	 * @return the channels flag
	 */
	public int getChannels()
	{
		return this.channels;
	}

	/**
	 * Returns the image mask.
	 *
	 * @return the image mask
	 */
	public Mask getMask()
	{
		return this.mask;
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		int channel_num = 3;
		int width = src.getWidth();
		int height = src.getHeight();
		int rgb, rgbs[] = new int[channel_num];
		if (this.channels == 0)
		// Enhance the Alpha channel
		{
			int alpha;
			ColorModel model = src.getColorModel();
			WritableRaster raster = src.getRaster();
			if (this.mask == null)
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						alpha = model.getAlpha(raster.getDataElements(x, y, null));
						truncate(perform(alpha));
						rgb = src.getRGB(x, y);
						dst.setRGB(x, y, (alpha << 24) | (rgb & 0x00ffffff));
					}
			else
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						float coef = this.mask.mask(x, y);
						alpha = model.getAlpha(raster.getDataElements(x, y, null));
						alpha = truncate(alpha + FastMath.round(coef * (perform(alpha) - alpha)));
						rgb = src.getRGB(x, y);
						dst.setRGB(x, y, (alpha << 24) | (rgb & 0x00ffffff));
					}
		}
		else if ((this.channels & 0x7) != 0)
		// Ehance the R, G, B channels
		{
			boolean[] flags = { (this.channels & 0x1) == 1, (this.channels >> 1 & 0x1) == 1,
					(this.channels >> 2 & 0x1) == 1 };
			if (this.mask == null)
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						rgb = src.getRGB(x, y);
						for (int z = 0; z < channel_num; z++)
							rgbs[z] = rgb >> (channel_num - z - 1 << 3) & 0xff;
						for (int z = 0; z < channel_num; z++)
							if (flags[z])
								rgbs[z] = truncate(perform(rgbs[z]));
						dst.setRGB(x, y, 0xff000000 | rgbs[0] << 16 | rgbs[1] << 8 | rgbs[2]);
					}
			else
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						float coef = this.mask.mask(x, y);
						rgb = src.getRGB(x, y);
						for (int z = 0; z < channel_num; z++)
							rgbs[z] = rgb >> (channel_num - z - 1 << 3) & 0xff;
						for (int z = 0; z < channel_num; z++)
							if (flags[z])
								rgbs[z] = truncate(rgbs[z]
										+ FastMath.round(coef * (perform(rgbs[z]) - rgbs[z])));
						dst.setRGB(x, y, 0xff000000 | rgbs[0] << 16 | rgbs[1] << 8 | rgbs[2]);
					}
		}
		else
		// Enhance the H, S, B channels
		{
			boolean[] flags = { (this.channels >> 3 & 0x1) == 1, (this.channels >> 4 & 0x1) == 1,
					(this.channels >> 5 & 0x1) == 1 };
			float[] hsb = new float[3];
			if (this.mask == null)
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						rgb = src.getRGB(x, y);
						for (int z = 0; z < channel_num; z++)
							rgbs[z] = rgb >> (channel_num - z - 1 << 3) & 0xff;
						Color.RGBtoHSB(rgbs[0], rgbs[1], rgbs[2], hsb);
						for (int z = 0; z < 3; z++)
							if (flags[z])
								hsb[z] = truncate(perform(FastMath.round(hsb[z] * 256))) / 256f;
						rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
						dst.setRGB(x, y, rgb);
					}
			else
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++)
					{
						rgb = src.getRGB(x, y);
						for (int z = 0; z < channel_num; z++)
							rgbs[z] = rgb >> (channel_num - z - 1 << 3) & 0xff;
						Color.RGBtoHSB(rgbs[0], rgbs[1], rgbs[2], hsb);
						float coef = this.mask.mask(x, y);
						for (int z = 0; z < 3; z++)
							if (flags[z])
								hsb[z] = truncate(hsb[z] + coef
										* (perform(FastMath.round(hsb[z] * 256)) / 256f - hsb[z]));
						rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
						dst.setRGB(x, y, rgb);
					}
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
		int[] data = new int[3];
		if ((this.channels & 0x7) != 0)
		// Ehance the R, G, B channels
		{
			boolean[] flags = { (this.channels & 0x1) == 1, (this.channels >> 1 & 0x1) == 1,
					(this.channels >> 2 & 0x1) == 1 };
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
				{
					raster.getPixel(x, y, data);
					float coef = this.mask == null ? 1 : this.mask.mask(x, y);
					for (int z = 0; z < 3; z++)
						if (flags[z])
							data[z] = FastMath.round(coef * this.perform(data[z]));
					dst.setPixel(x, y, data);
				}
		}
		else
		// Enhance the H, S, B channels
		{
			boolean[] flags = { (this.channels >> 3 & 0x1) == 1, (this.channels >> 4 & 0x1) == 1,
					(this.channels >> 5 & 0x1) == 1 };
			float[] hsb = new float[3];
			int rgb;
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
				{
					raster.getPixel(x, y, data);
					Color.RGBtoHSB(data[0], data[1], data[2], hsb);
					float coef = this.mask == null ? 1 : this.mask.mask(x, y);
					for (int z = 0; z < 3; z++)
						if (flags[z])
							hsb[z] = coef * this.perform(FastMath.round(hsb[0] * 256)) / 256f;
					rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
					data[0] = rgb >> 16 & 0xff;
					data[1] = rgb >> 8 & 0xff;
					data[2] = rgb & 0xff;
					dst.setPixel(x, y, data);
				}
		}
	}

	/**
	 * Perform the enhancement to the specified rgb value.
	 *
	 * @param value the pixel value in 0-255
	 * @return the enhanced pixel value
	 */
	protected abstract int perform(int value);

	/**
	 * Set the flags for enhance the color channels.
	 *
	 * @param channels the value of channels flag
	 * @see #channels
	 */
	public void setChannels(int channels)
	{
		this.channels = channels;
	}

	/**
	 * Set the image mask.
	 *
	 * @param mask the value of image mask
	 */
	public void setMask(Mask mask)
	{
		this.mask = mask;
	}

	protected float truncate(float value)
	{
		if (value < 0)
			return 0;
		if (value > 1f)
			return 1f;
		return value;
	}

	protected int truncate(int value)
	{
		if (value < 0)
			return 0;
		if (value > 255)
			return 255;
		return value;
	}
}

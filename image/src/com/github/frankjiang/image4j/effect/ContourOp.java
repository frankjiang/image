/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ContourOp.java is PROPRIETARY/CONFIDENTIAL built in 6:07:50 PM, Jul 3, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.effect;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;

import com.github.frankjiang.image4j.ImagingOp;
import com.github.frankjiang.image4j.morph.MorphKernel;

/**
 * The contour operation.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ContourOp extends ImagingOp
{
	protected Color			color;

	protected MorphKernel	kernel;

	public ContourOp(Color color, MorphKernel kernel)
	{
		this.color = color;
		this.kernel = kernel;
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		int width = src.getWidth();
		int height = src.getHeight();
		int[][] alpha = new int[height][width];
		int[][] red = new int[height][width];
		int[][] green = new int[height][width];
		int[][] blue = new int[height][width];
		// Ehance the full sRGB color space
		ColorModel model = src.getColorModel();
		WritableRaster raster = src.isAlphaPremultiplied() ? src.getAlphaRaster() : src.getRaster();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				Object pixel = raster.getDataElements(x, y, null);
				alpha[y][x] = model.getAlpha(pixel);
				red[y][x] = model.getRed(pixel);
				green[y][x] = model.getGreen(pixel);
				blue[y][x] = model.getBlue(pixel);
			}
		int[][][] data = { alpha, red, green, blue };
		Point[] offsets = this.kernel.getOffsets();
		ArrayList<Integer>[] lists = new ArrayList[data.length];
		for (int i = 0; i < lists.length; i++)
			lists[i] = new ArrayList<>(offsets.length);
		int px, py;
		int a, r, g, b;
		// float pInner, pOuter;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				for (ArrayList<Integer> list : lists)
					list.clear();
				for (Point p : offsets)
				{
					px = p.x + x;
					if (px < 0 || px >= width)
						continue;
					py = p.y + y;
					if (py < 0 || py >= height)
						continue;
					for (int z = 0; z < lists.length; z++)
						lists[z].add(data[z][py][px]);
				}
				a = Collections.max(lists[0]);
				if (alpha[y][x] != 0)
				{
					r = Collections.max(lists[1]);
					g = Collections.max(lists[2]);
					b = Collections.max(lists[3]);
					dst.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
				}
				else
					dst.setRGB(x, y, (a << 24) | (color.getRGB() & 0x00ffffff));
			}
	}
	
	/**
	 * Returns color.
	 * @return the color
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Set color.
	 * @param color the value of color
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

}

/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ColorMorphOp.java is PROPRIETARY/CONFIDENTIAL built in 4:19:56 AM, Jul 3,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.morph;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;

import com.github.frankjiang.image4j.ImagingLib;
import com.github.frankjiang.image4j.ImagingOp;

/**
 * TODO
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ColorMorphOp extends ImagingOp
{
	/**
	 * The erode operation.
	 */
	public static final int	TYPE_ERODE	= 0;
	/**
	 * The dilate operation.
	 */
	public static final int	TYPE_DILATE	= 1;

	public static final int	TYPE_OPEN	= 2;

	public static final int	TYPE_CLOSE	= 3;

	protected MorphKernel	kernel;

	protected int			type;

	public ColorMorphOp(MorphKernel kernel, int type)
	{
		this.kernel = kernel;
		this.type = type;
	}

	protected void dilate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		int width = src.getWidth();
		int height = src.getHeight();
		int rgb;
		int[][] alpha = new int[height][width];
		int[][] red = new int[height][width];
		int[][] green = new int[height][width];
		int[][] blue = new int[height][width];
		// ColorModel model = src.getColorModel();
		// Ehance the full sRGB color space
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				rgb = src.getRGB(x, y);
				alpha[y][x] = rgb >> 24 & 0xff;
				red[y][x] = rgb >> 16 & 0xff;
				green[y][x] = rgb >> 8 & 0xff;
				blue[y][x] = rgb & 0xff;
			}
		int[][][] data = { alpha, red, green, blue };
		Point[] offsets = this.kernel.offsets;
		ArrayList<Integer>[] lists = new ArrayList[data.length];
		for (int i = 0; i < lists.length; i++)
			lists[i] = new ArrayList<>(offsets.length);
		int px, py;
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

				dst.setRGB(x, y, Collections.max(lists[0]) << 24 | Collections.max(lists[1]) << 16
						| Collections.max(lists[2]) << 8 | Collections.max(lists[3]));
			}
	}

	protected void dilate(Raster src, WritableRaster dst)
	{
		int width = src.getWidth();
		int height = src.getHeight();
		int[][][] data = new int[height][width][3];
		// Ehance the full sRGB color space
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				src.getPixel(x, y, data[y][x]);
		Point[] offsets = this.kernel.offsets;
		ArrayList<Integer>[] lists = new ArrayList[3];
		for (int i = 0; i < 3; i++)
			lists[i] = new ArrayList<>(offsets.length);
		int px, py;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				for (int z = 0; z < 3; z++)
					lists[z].clear();
				for (Point p : offsets)
				{
					px = p.x + x;
					if (px < 0 || px > width)
						continue;
					py = p.y + y;
					if (py < 0 || py > height)
						continue;
					for (int z = 0; z < 3; z++)
						lists[z].add(data[py][px][z]);
				}
				for (int z = 0; z < 3; z++)
					data[y][x][z] = Collections.max(lists[z]);
				dst.setPixel(x, y, data[y][x]);
			}
	}

	protected void erode(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		int width = src.getWidth();
		int height = src.getHeight();
		int rgb;
		int[][] alpha = new int[height][width];
		int[][] red = new int[height][width];
		int[][] green = new int[height][width];
		int[][] blue = new int[height][width];
		int[][][] data = { alpha, red, green, blue };
		// Ehance the full sRGB color space
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				rgb = src.getRGB(x, y);
				alpha[y][x] = rgb >> 24 & 0xff;
				red[y][x] = rgb >> 16 & 0xff;
				green[y][x] = rgb >> 8 & 0xff;
				blue[y][x] = rgb & 0xff;
			}
		Point[] offsets = this.kernel.offsets;
		ArrayList<Integer>[] lists = new ArrayList[4];
		for (int i = 0; i < lists.length; i++)
			lists[i] = new ArrayList<>(offsets.length);
		int px, py;
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

				dst.setRGB(x, y, Collections.min(lists[0]) << 24 | Collections.min(lists[1]) << 16
						| Collections.min(lists[2]) << 8 | Collections.min(lists[3]));
			}
	}

	protected void erode(Raster src, WritableRaster dst)
	{
		int width = src.getWidth();
		int height = src.getHeight();
		int[][][] data = new int[height][width][3];
		// Ehance the full sRGB color space
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				src.getPixel(x, y, data[y][x]);
		Point[] offsets = this.kernel.offsets;
		ArrayList<Integer>[] lists = new ArrayList[3];
		for (int i = 0; i < 3; i++)
			lists[i] = new ArrayList<>(offsets.length);
		int px, py;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				for (int z = 0; z < 3; z++)
					lists[z].clear();
				for (Point p : offsets)
				{
					px = p.x + x;
					if (px < 0 || px > width)
						continue;
					py = p.y + y;
					if (py < 0 || py > height)
						continue;
					for (int z = 0; z < 3; z++)
						lists[z].add(data[py][px][z]);
				}
				for (int z = 0; z < 3; z++)
					data[y][x][z] = Collections.min(lists[z]);
				dst.setPixel(x, y, data[y][x]);
			}
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		ImagingLib.makeTransparentImage(dst);
		switch (this.type)
		{
		case TYPE_ERODE:
			this.erode(src, dst);
			break;
		case TYPE_DILATE:
			this.dilate(src, dst);
			break;
		case TYPE_OPEN:
		{
			BufferedImage tmp = this.createCompatibleDestImage(src, src.getColorModel());
			ImagingLib.makeTransparentImage(tmp);
			this.erode(src, tmp);
			this.dilate(tmp, dst);
		}
			break;
		case TYPE_CLOSE:
		{
			BufferedImage tmp = this.createCompatibleDestImage(src, src.getColorModel());
			ImagingLib.makeTransparentImage(tmp);
			this.dilate(src, tmp);
			this.erode(tmp, dst);
		}
			break;
		default:
			throw new IllegalArgumentException("Unknown morphology operation type.");
		}
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.Raster,
	 *      java.awt.image.WritableRaster)
	 */
	@Override
	protected void operate(Raster src, WritableRaster dst)
	{
		throw new UnsupportedOperationException("Cannot filter the raster with morphology.");
	}

}

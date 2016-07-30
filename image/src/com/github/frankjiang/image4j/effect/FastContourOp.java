/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * FastContourOp.java is PROPRIETARY/CONFIDENTIAL built in 6:07:50 PM, Jul 3,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;

import com.github.frankjiang.image4j.morph.MorphKernel;

/**
 * The fast contour operation.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class FastContourOp extends ContourOp
{

	public FastContourOp(Color color, MorphKernel kernel)
	{
		super(color, kernel);
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
		// Ehance the full sRGB color space
		ColorModel model = src.getColorModel();
		WritableRaster raster = src.getRaster();
		Graphics2D g = dst.createGraphics();
		g.drawImage(src, 0, 0, null);

		int px, py;
		int rgb = color.getRGB();
		Point[] offsets = this.kernel.getOffsets();
		Boolean[][] map = new Boolean[height][width];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				map[y][x] = (model.getAlpha(raster.getDataElements(x, y, null)) == 0);

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if (map[y][x] != null && !map[y][x])
					for (Point p : offsets)
					{
						px = p.x + x;
						if (px < 0 || px >= width)
							continue;
						py = p.y + y;
						if (py < 0 || py >= height)
							continue;
						if (map[py][px] != null && map[py][px])
						{
							map[py][px] = null;
							dst.setRGB(px, py, rgb);
						}
					}
		g.dispose();
	}

}

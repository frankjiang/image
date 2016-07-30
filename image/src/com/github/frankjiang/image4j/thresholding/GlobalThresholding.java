/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * GlobalThresholding.java is PROPRIETARY/CONFIDENTIAL built in 5:39:36 PM, Jul
 * 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.thresholding;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * The global thresholding.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class GlobalThresholding extends AbstractThresholding
{
	public GlobalThresholding()
	{

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
		int threshold = 0;
		Raster rasterSrc = src.getRaster();
		WritableRaster rasterDst = dst.getRaster();

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				byte[] data = (byte[]) rasterSrc.getDataElements(x, y, null);
				if ((data[0] & 0xff) > threshold)
					data[0] = -1;
				else
					data[0] = 0;
				rasterDst.setDataElements(x, y, data);
			}
	}

}

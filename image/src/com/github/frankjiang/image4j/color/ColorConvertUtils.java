/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ColorConvertUtils.java is PROPRIETARY/CONFIDENTIAL built in 1:31:09 AM, Jul
 * 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.color;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;

/**
 * The color space convert utilities.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ColorConvertUtils
{
	/**
	 * Returns the grayscale version of the specified image.
	 * If the source image is a grayscale image, then returns itself; otherwise
	 * creates a converted image for it.
	 *
	 * @param image the source image
	 * @return the grayscale image
	 */
	public static BufferedImage getGrayImage(BufferedImage image)
	{
		ColorModel colorModel = image.getColorModel();
		if (colorModel.getColorSpace().getType() != ColorSpace.CS_GRAY)
			return new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
					.filter(image, null);
		else
			return image;
	}
}

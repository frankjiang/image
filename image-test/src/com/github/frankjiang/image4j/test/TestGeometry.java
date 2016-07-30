/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights reserved.
 * TestGeometry.java is PROPRIETARY/CONFIDENTIAL built in 3:32:32 AM, Jul 2, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.test;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.github.frankjiang.image4j.geom.GeometryUtils;

/**
 * Test case for geometry.
 * <p>
 * </p>
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class TestGeometry
{
	@Test
	public void testPerspectiveTransform()
	{
		BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		GeometryUtils.scale(image, 0.5f, 0.5f, GeometryUtils.TYPE_BICUBIC);
	}
}

/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * MorphKernel.java is PROPRIETARY/CONFIDENTIAL built in 4:20:54 AM, Jul 3,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.morph;

import java.awt.Point;
import java.util.ArrayList;

/**
 * The morphology kernel class.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class MorphKernel
{
	public static MorphKernel getSquareKernel(int edge)
	{
		if (edge < 2)
			throw new IllegalArgumentException("The value of edge is less than 2.");
		MorphKernel kernel = new MorphKernel();
		kernel.offsets = new Point[edge * edge];
		int i = 0;
		int begin = -(edge / 2);
		int end = edge + begin;
		for (int y = begin; y < end; y++)
			for (int x = begin; x < end; x++)
				kernel.offsets[i++] = new Point(x, y);
		return kernel;
	}
	
	public static MorphKernel getShadowKernel(int edge)
	{
		ArrayList<Point> points = new ArrayList<>();
		for (int y = 0; y < edge; y++)
			for (int x = 0; x < edge; x++)
				points.add(new Point(x, y));
		Point[] offsets = points.toArray(new Point[0]);
		return new MorphKernel(offsets);
	}

	protected Point[] offsets;

	protected MorphKernel()
	{

	}

	public MorphKernel(Point... offsets)
	{
		if (offsets.length < 2)
			throw new IllegalArgumentException("The number of offset point is less than 2.");
		this.offsets = offsets;
	}

	public Point[] getOffsets()
	{
		return this.offsets;
	}
}

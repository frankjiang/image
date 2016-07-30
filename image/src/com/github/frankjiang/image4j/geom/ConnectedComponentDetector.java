/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ConnectedComponentDetector.java is PROPRIETARY/CONFIDENTIAL built in 6:52:29
 * PM, Jul 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.geom;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.LinkedList;
import java.util.Stack;

/**
 * The connected components detector.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ConnectedComponentDetector
{
	// @formatter:off
	/**
	 * The direction of detection.
	 */
	private static final int[][]				DIRECTIONS	= new int[][] 
	{
		{0, +1},
		{0, -1},
		{+1, 0},
		{-1, 0}
	};
	// @formatter:on

	protected BufferedImage					image;
	private Match							match;
	private ColorModel						model;
	private Raster							raster;
	private LinkedList<ConnectedComponent>	list;

	public ConnectedComponentDetector(BufferedImage image)
	{
		this(image, false);
	}
	public ConnectedComponentDetector(BufferedImage image, boolean reverse)
	{
		this.image = image;
		this.model = image.getColorModel();
		this.raster = image.getRaster();
		this.list = new LinkedList<>();
		match = reverse ? new MatchBlack() : new MatchWhite();
	}

	private interface Match
	{
		boolean match(int x, int y);
	}

	private class MatchWhite implements Match
	{
		/**
		 * @see com.github.frankjiang.image4j.geom.ConnectedComponentDetector.Match#match(int,
		 *      int)
		 */
		@Override
		public boolean match(int x, int y)
		{
			return model.getBlue(raster.getDataElements(x, y, null)) >= 127;
		}

	}

	private class MatchBlack implements Match
	{

		/**
		 * @see com.github.frankjiang.image4j.geom.ConnectedComponentDetector.Match#match(int,
		 *      int)
		 */
		@Override
		public boolean match(int x, int y)
		{
			return model.getBlue(raster.getDataElements(x, y, null)) < 127;
		}

	}

	/**
	 * Returns the detected connected components.
	 * 
	 * @return the detected connected components
	 */
	public LinkedList<ConnectedComponent> detect()
	{
		int width = image.getWidth();
		int height = image.getHeight();
		boolean[][] map = new boolean[height][width];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if (!map[y][x] && match.match(x, y))
				{
					ConnectedComponent comp = new ConnectedComponent();
					Point pt = new Point(x, y);
					Stack<Point> stack = new Stack<>();
					stack.add(pt);
					map[y][x] = true;
					while (!stack.isEmpty())
					{
						pt = stack.pop();
						comp.addPoint(pt);
						for (int d = 0; d < DIRECTIONS.length; d++)
						{
							int xt = pt.x + DIRECTIONS[d][0];
							int yt = pt.y + DIRECTIONS[d][1];
							if (xt > -1 && yt > -1 && xt < width && yt < height && !map[yt][xt])
							{
								map[yt][xt] = true;
								if (match.match(xt, yt))
									stack.push(new Point(xt, yt));
							}
						}
					}
					list.add(comp);
				}
		return list;
	}
}

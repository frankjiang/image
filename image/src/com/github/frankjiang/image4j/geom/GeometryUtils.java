/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * GeometryUtils.java is PROPRIETARY/CONFIDENTIAL built in 6:10:45 PM, Jul 1,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.geom;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.annotation.Native;

import com.github.frankjiang.image4j.math.Quadrangle;

/**
 * The geometry utilities.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class GeometryUtils
{
	/**
	 * Bicubic interpolation type.
	 */
	@Native
	public static final int	TYPE_BICUBIC			= 3;

	/**
	 * Bilinear interpolation type.
	 */
	@Native
	public static final int	TYPE_BILINEAR			= 2;

	/**
	 * Nearest-neighbor interpolation type.
	 */
	@Native
	public static final int	TYPE_NEAREST_NEIGHBOR	= 1;

	protected static Point[] getPoints(Polygon poly)
	{
		Point[] pts = new Point[poly.npoints];
		for (int i = 0; i < poly.npoints; i++)
			pts[i] = new Point(poly.xpoints[i], poly.ypoints[i]);
		return pts;
	}

	public static BufferedImage scale(BufferedImage image, float sx, float sy,
			int interploationType)
	{
		AffineTransform xform = AffineTransform.getScaleInstance(sx, sy);
		Object value;
		switch (interploationType)
		{
		case TYPE_NEAREST_NEIGHBOR:
			value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			break;
		default:
		case TYPE_BILINEAR:
			value = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			break;
		case TYPE_BICUBIC:
			value = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			break;
		}
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, value);
		AffineTransformOp op = new AffineTransformOp(xform, hints);
		return op.filter(image, null);
	}

	public static BufferedImage scale(BufferedImage image, int size, int interploationType)
	{
		float scale = size * 1f / Math.min(image.getWidth(), image.getHeight());
		return GeometryUtils.scale(image, scale, scale, interploationType);
	}

	public static Polygon transform(AffineTransform xform, Polygon poly)
	{
		Point[] pts = GeometryUtils.getPoints(poly);
		xform.transform(pts, 0, pts, 0, pts.length);
		int[] xpoints = new int[poly.npoints];
		int[] ypoints = new int[poly.npoints];
		for (int i = 0; i < pts.length; i++)
		{
			xpoints[i] = pts[i].x;
			ypoints[i] = pts[i].y;
		}
		return new Polygon(xpoints, ypoints, poly.npoints);
	}

	public static Quadrangle transform(AffineTransform xform, Quadrangle quad)
	{
		Point[] pts = GeometryUtils.getPoints(quad);
		xform.transform(pts, 0, pts, 0, pts.length);
		return new Quadrangle(pts[0], pts[1], pts[3], pts[2]);
	}

	public static Quadrangle transform(AffineTransform xform, Rectangle rect)
	{
		return GeometryUtils.transform(xform, new Quadrangle(rect));
	}
}

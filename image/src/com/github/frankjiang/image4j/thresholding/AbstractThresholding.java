/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * AbstractThresholding.java is PROPRIETARY/CONFIDENTIAL built in 5:24:06 PM,
 * Jul 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.thresholding;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;

import com.github.frankjiang.image4j.ImagingOp;
import com.github.frankjiang.image4j.color.ColorConvertUtils;

/**
 * The abstract thresholding operator.
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public abstract class AbstractThresholding extends ImagingOp
{

	/**
	 * Creates a zeroed destination image with the correct size and number of
	 * bands. A <CODE>RasterFormatException</CODE> may be thrown if the
	 * transformed width or height is equal to 0.
	 * <p>
	 * If <CODE>destCM</CODE> is null,
	 * an appropriate <CODE>ColorModel</CODE> is used; this
	 * <CODE>ColorModel</CODE> may have
	 * an alpha channel even if the source <CODE>ColorModel</CODE> is opaque.
	 *
	 * @param src The <CODE>BufferedImage</CODE> to be transformed.
	 * @param destCM <CODE>ColorModel</CODE> of the destination. If null,
	 *            an appropriate <CODE>ColorModel</CODE> is used.
	 * @return The zeroed destination image.
	 */
	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
	{
		BufferedImage image;
		Rectangle r = this.getBounds2D(src).getBounds();

		// If r.x (or r.y) is < 0, then we want to only create an image
		// that is in the positive range.
		// If r.x (or r.y) is > 0, then we need to create an image that
		// includes the translation.
		int w = r.x + r.width;
		int h = r.y + r.height;
		if (w <= 0)
			throw new RasterFormatException(
					"Transformed width (" + w + ") is less than or equal to 0.");
		if (h <= 0)
			throw new RasterFormatException(
					"Transformed height (" + h + ") is less than or equal to 0.");

		if (destCM == null)
		{
			ColorModel cm = src.getColorModel();
			image = new BufferedImage(cm, src.getRaster().createCompatibleWritableRaster(w, h),
					cm.isAlphaPremultiplied(), null);
		}
		else
			image = new BufferedImage(destCM, destCM.createCompatibleWritableRaster(w, h),
					destCM.isAlphaPremultiplied(), null);

		return image;
	}

	//	/**
	//	 * Creates a zeroed destination <CODE>Raster</CODE> with the correct size
	//	 * and number of bands. A <CODE>RasterFormatException</CODE> may be thrown
	//	 * if the transformed width or height is equal to 0.
	//	 *
	//	 * @param src The <CODE>Raster</CODE> to be transformed.
	//	 * @return The zeroed destination <CODE>Raster</CODE>.
	//	 */
	//	@Override
	//	public WritableRaster createCompatibleDestRaster(Raster src)
	//	{
	//		Rectangle2D r = this.getBounds2D(src);
	//
	//		return src.createCompatibleWritableRaster((int) r.getX(), (int) r.getY(),
	//				(int) r.getWidth(), (int) r.getHeight());
	//	}

	/**
	 * Transforms the source <CODE>BufferedImage</CODE> and stores the results
	 * in the destination <CODE>BufferedImage</CODE>.
	 * If the color models for the two images do not match, a color
	 * conversion into the destination color model is performed.
	 * If the destination image is null,
	 * a <CODE>BufferedImage</CODE> is created with the source
	 * <CODE>ColorModel</CODE>.
	 * <p>
	 * The coordinates of the rectangle returned by
	 * <code>getBounds2D(BufferedImage)</code>
	 * are not necessarily the same as the coordinates of the
	 * <code>BufferedImage</code> returned by this method. If the
	 * upper-left corner coordinates of the rectangle are
	 * negative then this part of the rectangle is not drawn. If the
	 * upper-left corner coordinates of the rectangle are positive
	 * then the filtered image is drawn at that position in the
	 * destination <code>BufferedImage</code>.
	 * <p>
	 * An <CODE>IllegalArgumentException</CODE> is thrown if the source is
	 * the same as the destination.
	 *
	 * @param src The <CODE>BufferedImage</CODE> to transform.
	 * @param dst The <CODE>BufferedImage</CODE> in which to store the results
	 *            of the transformation.
	 * @return The filtered <CODE>BufferedImage</CODE>.
	 * @throws IllegalArgumentException if <code>src</code> and
	 *             <code>dst</code> are the same
	 * @throws ImagingOpException if the image cannot be transformed
	 *             because of a data-processing error that might be
	 *             caused by an invalid image format, tile format, or
	 *             image-processing operation, or any other unsupported
	 *             operation.
	 */
	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst)
	{
		if (src == null)
			throw new NullPointerException("src image is null");
		if (src.getColorModel().getColorSpace().getType() != ColorSpace.CS_GRAY)
			src = ColorConvertUtils.getGrayImage(src);
		int width = src.getWidth();
		int height = src.getHeight();
		int imageType = BufferedImage.TYPE_BYTE_BINARY;
		if (dst == null)
			dst = new BufferedImage(width, height, imageType);
		else if (dst.getType() != BufferedImage.TYPE_BYTE_BINARY)
			throw new IllegalArgumentException(String.format(
					"The user provided dst image type is %d, not TYPE_BYTE_BINARY (%d)",
					dst.getType(), BufferedImage.TYPE_BYTE_BINARY));
		operate(src, dst);
		return dst;
	}

	/**
	 * Returns the bounding box of the transformed destination. The
	 * rectangle returned is the actual bounding box of the
	 * transformed points. The coordinates of the upper-left corner
	 * of the returned rectangle might not be (0,&nbsp;0).
	 *
	 * @param src The <CODE>BufferedImage</CODE> to be transformed.
	 * @return The <CODE>Rectangle2D</CODE> representing the destination's
	 *         bounding box.
	 */
	@Override
	public Rectangle2D getBounds2D(BufferedImage src)
	{
		return src.getRaster().getBounds();
	}

	/**
	 * Returns the location of the corresponding destination point given a
	 * point in the source. If <CODE>dstPt</CODE> is specified, it
	 * is used to hold the return value.
	 *
	 * @param srcPt The <code>Point2D</code> that represents the source
	 *            point.
	 * @param dstPt The <CODE>Point2D</CODE> in which to store the result.
	 * @return The <CODE>Point2D</CODE> in the destination that corresponds to
	 *         the specified point in the source.
	 */
	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
	{
		if (dstPt == null)
			dstPt = srcPt instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
		dstPt.setLocation(srcPt);
		return dstPt;
	}

	/**
	 * Returns the rendering hints used by this transform operation.
	 *
	 * @return The <CODE>RenderingHints</CODE> object associated with this op.
	 */
	@Override
	public RenderingHints getRenderingHints()
	{
		return this.hints;
	}

	/**
	 * Operates the source raster <code>src</code> and stores the result in the
	 * destination raster <code>dst</code>.
	 *
	 * @param src the source image
	 * @param dst the destination image
	 * @throws ImagingOpException if an error occurs during this operation
	 */
	protected void operate(Raster src, WritableRaster dst)
	{
		throw new UnsupportedOperationException("The operation for rasters is not defined yet.");
	}

}

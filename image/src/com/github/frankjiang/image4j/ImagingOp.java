/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ImagingOp.java is PROPRIETARY/CONFIDENTIAL built in 1:47:00 AM, Jul 2,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;

/**
 * The image operator for a common image operation.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public abstract class ImagingOp implements BufferedImageOp
{
	/**
	 * The rendering hints.
	 */
	protected RenderingHints hints;

	/**
	 * Construct an instance of <tt>ImagingOp</tt>.
	 */
	protected ImagingOp()
	{
	};

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

		boolean needToConvert = false;
		ColorModel srcCM = src.getColorModel();
		ColorModel dstCM;
		BufferedImage origDst = dst;

		if (dst == null)
		{
			dst = this.createCompatibleDestImage(src, null);
			dstCM = srcCM;
			origDst = dst;
		}
		else
		{
			dstCM = dst.getColorModel();
			if (srcCM.getColorSpace().getType() != dstCM.getColorSpace().getType())
				if (srcCM.getTransparency() == Transparency.OPAQUE)
				{
					// Need to convert first
					ColorConvertOp ccop = new ColorConvertOp(this.hints);
					BufferedImage tmpSrc = null;
					int sw = src.getWidth();
					int sh = src.getHeight();
					if (dstCM.getTransparency() == Transparency.OPAQUE)
						tmpSrc = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
					else
					{
						WritableRaster r = dstCM.createCompatibleWritableRaster(sw, sh);
						tmpSrc = new BufferedImage(dstCM, r, dstCM.isAlphaPremultiplied(), null);
					}
					src = ccop.filter(src, tmpSrc);
				}
				else
				{
					needToConvert = true;
					dst = this.createCompatibleDestImage(src, null);
				}
		}

		if (dst.getColorModel() instanceof IndexColorModel)
			dst = new BufferedImage(dst.getWidth(), dst.getHeight(), BufferedImage.TYPE_INT_ARGB);

		this.operate(src, dst);

		if (needToConvert)
		{
			ColorConvertOp ccop = new ColorConvertOp(this.hints);
			ccop.filter(dst, origDst);
		}
		else if (origDst != dst)
		{
			Graphics2D g = origDst.createGraphics();
			try
			{
				g.setComposite(AlphaComposite.Src);
				g.drawImage(dst, 0, 0, null);
			}
			finally
			{
				g.dispose();
			}
		}

		return origDst;
	}

//	/**
//	 * Transforms the source <CODE>Raster</CODE> and stores the results in
//	 * the destination <CODE>Raster</CODE>. This operation performs the
//	 * transform band by band.
//	 * <p>
//	 * If the destination <CODE>Raster</CODE> is null, a new
//	 * <CODE>Raster</CODE> is created.
//	 * An <CODE>IllegalArgumentException</CODE> may be thrown if the source is
//	 * the same as the destination or if the number of bands in
//	 * the source is not equal to the number of bands in the
//	 * destination.
//	 * <p>
//	 * The coordinates of the rectangle returned by
//	 * <code>getBounds2D(Raster)</code>
//	 * are not necessarily the same as the coordinates of the
//	 * <code>WritableRaster</code> returned by this method. If the
//	 * upper-left corner coordinates of rectangle are negative then
//	 * this part of the rectangle is not drawn. If the coordinates
//	 * of the rectangle are positive then the filtered image is drawn at
//	 * that position in the destination <code>Raster</code>.
//	 * <p>
//	 *
//	 * @param src The <CODE>Raster</CODE> to transform.
//	 * @param dst The <CODE>Raster</CODE> in which to store the results of the
//	 *            transformation.
//	 * @return The transformed <CODE>Raster</CODE>.
//	 * @throws ImagingOpException if the raster cannot be transformed
//	 *             because of a data-processing error that might be
//	 *             caused by an invalid image format, tile format, or
//	 *             image-processing operation, or any other unsupported
//	 *             operation.
//	 */
//	@Override
//	public WritableRaster filter(Raster src, WritableRaster dst)
//	{
//		if (src == null)
//			throw new NullPointerException("src image is null");
//		if (dst == null)
//			dst = this.createCompatibleDestRaster(src);
//		if (src.getNumBands() != dst.getNumBands())
//			throw new IllegalArgumentException("Number of src bands (" + src.getNumBands()
//					+ ") does not match number of " + " dst bands (" + dst.getNumBands() + ")");
//		this.operate(src, dst);
//
//		return dst;
//	}

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

//	/**
//	 * Returns the bounding box of the transformed destination. The
//	 * rectangle returned will be the actual bounding box of the
//	 * transformed points. The coordinates of the upper-left corner
//	 * of the returned rectangle might not be (0,&nbsp;0).
//	 *
//	 * @param src The <CODE>Raster</CODE> to be transformed.
//	 * @return The <CODE>Rectangle2D</CODE> representing the destination's
//	 *         bounding box.
//	 */
//	@Override
//	public Rectangle2D getBounds2D(Raster src)
//	{
//		return src.getBounds();
//	}

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
	 * Operates the source image <code>src</code> and stores the result in the
	 * destination image <code>dst</code>.
	 *
	 * @param src the source image
	 * @param dst the destination image
	 * @throws ImagingOpException if an error occurs during this operation
	 */
	abstract protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException;

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

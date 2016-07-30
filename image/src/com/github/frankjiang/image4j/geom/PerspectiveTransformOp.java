/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Geometry.java is PROPRIETARY/CONFIDENTIAL built in 2013.
 * Use is subject to license terms.
 */
package com.github.frankjiang.image4j.geom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.lang.annotation.Native;

import org.apache.commons.math3.util.FastMath;

import com.github.frankjiang.image4j.ImagingLib;
import com.github.frankjiang.image4j.ImagingOp;

/**
 * The geometry transform operators.
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @param <T>
 * @version 1.0.0
 */
public class PerspectiveTransformOp extends ImagingOp implements RasterOp
{
	/**
	 * The scheme of filling the background.
	 *
	 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
	 * @version 1.0.0
	 */
	public static enum Fill
	{
		/**
		 * No filling.
		 */
		None,
		/**
		 * Fill with black pixels.
		 */
		Black,

		/**
		 * Fill with blank pixels.
		 */
		Blank,

		/**
		 * Fill with white pixels.
		 */
		White;
	}

	/**
	 * Bicubic interpolation type.
	 */
	@Native
	public static final int			TYPE_BICUBIC			= 3;

	/**
	 * Bilinear interpolation type.
	 */
	@Native
	public static final int			TYPE_BILINEAR			= 2;

	/**
	 * Nearest-neighbor interpolation type.
	 */
	@Native
	public static final int			TYPE_NEAREST_NEIGHBOR	= 1;

	/**
	 * The edge filling scheme.
	 */
	protected Fill					fillScheme;
	/**
	 * The interpolation interpolationType.
	 */
	protected int					interpolationType;
	/**
	 * The transformation.
	 */
	protected PerspectiveTransform	xform;

	/**
	 * Construct an instance of <tt>PerspectiveTransformOp</tt> with bilinear
	 * interpolation scheme and fill the padding with black.
	 */
	public PerspectiveTransformOp(PerspectiveTransform xform)
	{
		this(xform, PerspectiveTransformOp.TYPE_BILINEAR, Fill.Blank);
	}

	/**
	 * Construct an instance of <tt>Geometry</tt>.
	 *
	 * @param interpolationType
	 *            the interpolationType of interpolation
	 * @param fillScheme
	 *            the edge filling scheme
	 */
	public PerspectiveTransformOp(PerspectiveTransform xform, int interpolationType,
			Fill fillScheme)
	{
		this.xform = xform;
		this.interpolationType = interpolationType;
		this.fillScheme = fillScheme;
		Object value;
		switch (interpolationType)
		{
		case TYPE_BICUBIC:
			value = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			break;
		default:
		case TYPE_BILINEAR:
			value = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			break;
		case TYPE_NEAREST_NEIGHBOR:
			value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			break;
		}
		this.hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, value);
	}

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
			if (this.interpolationType != PerspectiveTransformOp.TYPE_NEAREST_NEIGHBOR
					&& (cm instanceof IndexColorModel
							|| cm.getTransparency() == Transparency.OPAQUE))
				image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			else
				image = new BufferedImage(cm, src.getRaster().createCompatibleWritableRaster(w, h),
						cm.isAlphaPremultiplied(), null);
		}
		else
			image = new BufferedImage(destCM, destCM.createCompatibleWritableRaster(w, h),
					destCM.isAlphaPremultiplied(), null);

		return image;
	}

	/**
	 * Creates a zeroed destination <CODE>Raster</CODE> with the correct size
	 * and number of bands. A <CODE>RasterFormatException</CODE> may be thrown
	 * if the transformed width or height is equal to 0.
	 *
	 * @param src The <CODE>Raster</CODE> to be transformed.
	 * @return The zeroed destination <CODE>Raster</CODE>.
	 */
	@Override
	public WritableRaster createCompatibleDestRaster(Raster src)
	{
		Rectangle2D r = this.getBounds2D(src);

		return src.createCompatibleWritableRaster((int) r.getX(), (int) r.getY(),
				(int) r.getWidth(), (int) r.getHeight());
	}

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
		if (src == dst)
			throw new IllegalArgumentException(
					"src image cannot be the " + "same as the dst image");

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
			{
				int type = this.xform.getType();
				boolean needTrans = (type & (AffineTransform.TYPE_MASK_ROTATION
						| AffineTransform.TYPE_GENERAL_TRANSFORM)) != 0;
				if (!needTrans && type != AffineTransform.TYPE_TRANSLATION
						&& type != AffineTransform.TYPE_IDENTITY)
				{
					double[] mtx = new double[4];
					this.xform.getMatrix(mtx);
					// Check out the matrix.  A non-integral scale will force ARGB
					// since the edge conditions can't be guaranteed.
					needTrans = mtx[0] != (int) mtx[0] || mtx[3] != (int) mtx[3];
				}

				if (needTrans && srcCM.getTransparency() == Transparency.OPAQUE)
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

		}

		if (this.interpolationType != PerspectiveTransformOp.TYPE_NEAREST_NEIGHBOR
				&& dst.getColorModel() instanceof IndexColorModel)
			dst = new BufferedImage(dst.getWidth(), dst.getHeight(), BufferedImage.TYPE_INT_ARGB);

		// fill the background with transparent color
		if (fillScheme != Fill.None)
		{
			Graphics2D g = dst.createGraphics();
			Color color;
			switch (this.fillScheme)
			{
			case Black:
				color = Color.BLACK;
				break;
			default:
			case Blank:
				color = new Color(255, 255, 255, 0);
				break;
			case White:
				color = Color.WHITE;
				break;
			}
			g.setColor(color);
			g.fillRect(0, 0, dst.getWidth(), dst.getHeight());
			g.dispose();
		}

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

	/**
	 * Transforms the source <CODE>Raster</CODE> and stores the results in
	 * the destination <CODE>Raster</CODE>. This operation performs the
	 * transform band by band.
	 * <p>
	 * If the destination <CODE>Raster</CODE> is null, a new
	 * <CODE>Raster</CODE> is created.
	 * An <CODE>IllegalArgumentException</CODE> may be thrown if the source is
	 * the same as the destination or if the number of bands in
	 * the source is not equal to the number of bands in the
	 * destination.
	 * <p>
	 * The coordinates of the rectangle returned by
	 * <code>getBounds2D(Raster)</code>
	 * are not necessarily the same as the coordinates of the
	 * <code>WritableRaster</code> returned by this method. If the
	 * upper-left corner coordinates of rectangle are negative then
	 * this part of the rectangle is not drawn. If the coordinates
	 * of the rectangle are positive then the filtered image is drawn at
	 * that position in the destination <code>Raster</code>.
	 * <p>
	 *
	 * @param src The <CODE>Raster</CODE> to transform.
	 * @param dst The <CODE>Raster</CODE> in which to store the results of the
	 *            transformation.
	 * @return The transformed <CODE>Raster</CODE>.
	 * @throws ImagingOpException if the raster cannot be transformed
	 *             because of a data-processing error that might be
	 *             caused by an invalid image format, tile format, or
	 *             image-processing operation, or any other unsupported
	 *             operation.
	 */
	@Override
	public WritableRaster filter(Raster src, WritableRaster dst)
	{
		if (src == null)
			throw new NullPointerException("src image is null");
		if (dst == null)
			dst = this.createCompatibleDestRaster(src);
		if (src == dst)
			throw new IllegalArgumentException(
					"src image cannot be the " + "same as the dst image");
		if (src.getNumBands() != dst.getNumBands())
			throw new IllegalArgumentException("Number of src bands (" + src.getNumBands()
					+ ") does not match number of " + " dst bands (" + dst.getNumBands() + ")");

		this.operate(src, dst);
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
		return this.getBounds2D(src.getRaster());
	}

	/**
	 * Returns the bounding box of the transformed destination. The
	 * rectangle returned will be the actual bounding box of the
	 * transformed points. The coordinates of the upper-left corner
	 * of the returned rectangle might not be (0,&nbsp;0).
	 *
	 * @param src The <CODE>Raster</CODE> to be transformed.
	 * @return The <CODE>Rectangle2D</CODE> representing the destination's
	 *         bounding box.
	 */
	@Override
	public Rectangle2D getBounds2D(Raster src)
	{
		int w = src.getWidth();
		int h = src.getHeight();

		// Get the bounding box of the src and transform the corners
		float[] pts = { 0, 0, w, 0, w, h, 0, h };
		this.xform.transform(pts, 0, pts, 0, 4);

		// Get the v0, vt of the dst
		float fmaxX = pts[0];
		float fmaxY = pts[1];
		float fminX = pts[0];
		float fminY = pts[1];
		for (int i = 2; i < 8; i += 2)
		{
			if (pts[i] > fmaxX)
				fmaxX = pts[i];
			else if (pts[i] < fminX)
				fminX = pts[i];
			if (pts[i + 1] > fmaxY)
				fmaxY = pts[i + 1];
			else if (pts[i + 1] < fminY)
				fminY = pts[i + 1];
		}

		return new Rectangle2D.Float(fminX, fminY, fmaxX - fminX, fmaxY - fminY);
	}

	/**
	 * Returns the interpolation type used by this op.
	 *
	 * @return the interpolation type.
	 * @see #TYPE_NEAREST_NEIGHBOR
	 * @see #TYPE_BILINEAR
	 * @see #TYPE_BICUBIC
	 */
	public int getInterpolationType()
	{
		return this.interpolationType;
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
		return this.xform.transform(srcPt, dstPt);
	}

	/**
	 * Returns the rendering hints used by this transform operation.
	 *
	 * @return The <CODE>RenderingHints</CODE> object associated with this op.
	 */
	@Override
	public RenderingHints getRenderingHints()
	{
		if (this.hints == null)
		{
			Object val;
			switch (this.interpolationType)
			{
			case TYPE_NEAREST_NEIGHBOR:
				val = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
				break;
			case TYPE_BILINEAR:
				val = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
				break;
			case TYPE_BICUBIC:
				val = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
				break;
			default:
				// Should never get here
				throw new InternalError("Unknown interpolation type " + this.interpolationType);

			}
			this.hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, val);
		}

		return this.hints;
	}

	/**
	 * Returns the affine transform used by this transform operation.
	 *
	 * @return The <CODE>AffineTransform</CODE> associated with this op.
	 */
	public PerspectiveTransform getTransform()
	{
		return (PerspectiveTransform) this.xform.clone();
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.BufferedImage,
	 *      java.awt.image.BufferedImage)
	 */
	@Override
	protected void operate(BufferedImage src, BufferedImage dst) throws ImagingOpException
	{
		if (ImagingLib.filter(this, src, dst) == null)
			throw new ImagingOpException("Unable to transform src image");
	}

	/**
	 * @see com.github.frankjiang.image4j.ImagingOp#operate(java.awt.image.Raster,
	 *      java.awt.image.WritableRaster)
	 */
	@Override
	protected void operate(Raster src, WritableRaster dst)
	{
		if (ImagingLib.filter(this, src, dst) == null)
			throw new ImagingOpException("Unable to transform src image");
	}

	// We need to be able to invert the transform if we want to
	// transform the image.  If the determinant of the matrix is 0,
	// then we can't invert the transform.
	void validateTransform(AffineTransform xform)
	{
		if (FastMath.abs(xform.getDeterminant()) <= Double.MIN_VALUE)
			throw new ImagingOpException("Unable to invert transform " + xform);
	}

}

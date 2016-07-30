/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ImageLib.java is PROPRIETARY/CONFIDENTIAL built in 3:26:31 PM, Jul 1, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.github.frankjiang.image4j.geom.PerspectiveTransform;
import com.github.frankjiang.image4j.geom.PerspectiveTransformOp;
import com.github.frankjiang.image4j.gui.ImagePanel;
import com.github.frankjiang.image4j.math.Interpolation;
import com.github.frankjiang.image4j.math.InterpolationBicubic;
import com.github.frankjiang.image4j.math.InterpolationBilinear;
import com.github.frankjiang.image4j.math.InterpolationNearestNeighbour;

import sun.awt.image.SunWritableRaster;

/**
 * This class provides a hook to access platform-specific
 * imaging code.
 * If the implementing class cannot handle the op, tile format or
 * image format, the method will return null;
 * If there is an error when processing the
 * data, the implementing class may either return null
 * (in which case our java code will be executed) or may throw
 * an exception.
 */
public class ImagingLib
{
	/**
	 * The number of native operations.
	 */
	private static final int	NUM_NATIVE_OPS	= 4;
	/**
	 * The operation tag for look up operations
	 */
	private static final int	LOOKUP_OP		= 0;
	/**
	 * The operation tag for affine transfromation.
	 */
	private static final int	AFFINE_OP		= 1;
	/**
	 * The operation tag for convolution operators.
	 */
	private static final int	CONVOLVE_OP		= 2;
	/**
	 * The operation tag for perspective transfromation.
	 */
	private static final int	PERSPECTIVE_OP	= 3;

	/**
	 * The native operation classes which uses this library.
	 */
	private static Class[]		nativeOpClass	= new Class[ImagingLib.NUM_NATIVE_OPS];

	static
	{
		//
		// Cache the class references of the operations we know about
		// at the time this class is initially loaded.
		//
		try
		{
			ImagingLib.nativeOpClass[ImagingLib.LOOKUP_OP] = Class
					.forName("java.awt.image.LookupOp");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Could not find class: " + e);
		}
		try
		{
			ImagingLib.nativeOpClass[ImagingLib.AFFINE_OP] = Class
					.forName("java.awt.image.AffineTransformOp");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Could not find class: " + e);
		}
		try
		{
			ImagingLib.nativeOpClass[ImagingLib.CONVOLVE_OP] = Class
					.forName("java.awt.image.ConvolveOp");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Could not find class: " + e);
		}
		try
		{
			ImagingLib.nativeOpClass[ImagingLib.PERSPECTIVE_OP] = Class
					.forName("com.github.frankjiang.image4j.geom.PerspectiveTransformOp");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Could not find class: " + e);
		}
	}

	/**
	 * Display the specified image.
	 * 
	 * @param title the title of the dialog
	 * @param image the specified image to be displayed
	 */
	public static JFrame display(String title, Image image)
	{
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		ImagePanel panel = new ImagePanel(image);
		// panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		return frame;
	}

	/**
	 * Filter the specified source image to the specified destination image with
	 * the specified image operator.
	 *
	 * @param op the specified image operator
	 * @param src the specified source image
	 * @param dst the specified destination image, if <code>null</code> a
	 *            compatible image will be created
	 * @return the filtered destination image
	 */
	public static BufferedImage filter(BufferedImageOp op, BufferedImage src, BufferedImage dst)
	{
		// Create the destination image
		if (dst == null)
			dst = op.createCompatibleDestImage(src, null);

		BufferedImage retBI = null;
		switch (ImagingLib.getNativeOpIndex(op.getClass()))
		{
		case LOOKUP_OP:
			// REMIND: Fix this!
			LookupTable table = ((LookupOp) op).getTable();
			if (table.getOffset() != 0)
				// Right now the native code doesn't support offsets
				return null;
			if (table instanceof ByteLookupTable)
			{
				ByteLookupTable bt = (ByteLookupTable) table;
				if (sun.awt.image.ImagingLib.lookupByteBI(src, dst, bt.getTable()) > 0)
					retBI = dst;
			}
			break;

		case AFFINE_OP:
		{
			AffineTransformOp bOp = (AffineTransformOp) op;
			double[] matrix = new double[6];

			bOp.getTransform().getMatrix(matrix);

			if (sun.awt.image.ImagingLib.transformBI(src, dst, matrix,
					bOp.getInterpolationType()) > 0)
				retBI = dst;
		}
			break;

		case CONVOLVE_OP:
			ConvolveOp cOp = (ConvolveOp) op;
			if (sun.awt.image.ImagingLib.convolveBI(src, dst, cOp.getKernel(),
					cOp.getEdgeCondition()) > 0)
				retBI = dst;
			break;
		case PERSPECTIVE_OP:
			PerspectiveTransformOp bOp = (PerspectiveTransformOp) op;
			double[] matrix = new double[9];
			bOp.getTransform().getMatrix(matrix);
			if (ImagingLib.transformBIExtend9(src, dst, matrix, bOp.getInterpolationType()) > 0)
				retBI = dst;
			break;
		default:
			break;
		}

		if (retBI != null)
			SunWritableRaster.markDirty(retBI);

		return retBI;
	}

	/**
	 * Filter the specified source raster to the specified destination raster
	 * with
	 * the specified raster operator.
	 *
	 * @param op the specified raster operator
	 * @param src the specified source raster
	 * @param dst the specified destination raster, if <code>null</code> a
	 *            compatible raster will be created
	 * @return the filtered destination raster
	 */
	public static WritableRaster filter(RasterOp op, Raster src, WritableRaster dst)
	{
		// Create the destination tile
		if (dst == null)
			dst = op.createCompatibleDestRaster(src);

		WritableRaster retRaster = null;
		switch (ImagingLib.getNativeOpIndex(op.getClass()))
		{

		case LOOKUP_OP:
			// REMIND: Fix this!
			LookupTable table = ((LookupOp) op).getTable();
			if (table.getOffset() != 0)
				// Right now the native code doesn't support offsets
				return null;
			if (table instanceof ByteLookupTable)
			{
				ByteLookupTable bt = (ByteLookupTable) table;
				if (sun.awt.image.ImagingLib.lookupByteRaster(src, dst, bt.getTable()) > 0)
					retRaster = dst;
			}
			break;

		case AFFINE_OP:
		{
			AffineTransformOp bOp = (AffineTransformOp) op;
			double[] matrix = new double[6];
			bOp.getTransform().getMatrix(matrix);
			if (sun.awt.image.ImagingLib.transformRaster(src, dst, matrix,
					bOp.getInterpolationType()) > 0)
				retRaster = dst;
		}
			break;

		case CONVOLVE_OP:
			ConvolveOp cOp = (ConvolveOp) op;
			if (sun.awt.image.ImagingLib.convolveRaster(src, dst, cOp.getKernel(),
					cOp.getEdgeCondition()) > 0)
				retRaster = dst;
			break;

		case PERSPECTIVE_OP:
		{
			PerspectiveTransformOp bOp = (PerspectiveTransformOp) op;
			double[] matrix = new double[9];
			bOp.getTransform().getMatrix(matrix);
			if (ImagingLib.transformRasterExtend9(src, dst, matrix, bOp.getInterpolationType()) > 0)
				retRaster = dst;
		}
			break;
		default:
			break;
		}

		if (retRaster != null)
			SunWritableRaster.markDirty(retRaster);

		return retRaster;

	}

	private static int getNativeOpIndex(Class opClass)
	{
		//
		// Search for this class in cached list of
		// classes supplying native acceleration
		//
		int opIndex = -1;
		for (int i = 0; i < ImagingLib.NUM_NATIVE_OPS; i++)
			if (opClass == ImagingLib.nativeOpClass[i])
			{
				opIndex = i;
				break;
			}
		return opIndex;
	}

	/**
	 * Inverted transform the specified source point to the destination point
	 * with the
	 * specified transformation matrix.
	 * This transformation matrix contains 9 elements, which applies:
	 *
	 * <pre>
	 *	[ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
	 *	[ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
	 *	[ w ]   [  m20  m21  m22  ] [ 1 ]   [ m20x + m21y + m22 ]
	 *
	 *	  x' = (m00x + m01y + m02)
	 *	  y' = (m10x + m11y + m12)
	 *
	 *        w  = (m20x + m21y + m22)
	 *
	 *        X = x' / w
	 *        Y = y' / w
	 * </pre>
	 *
	 * The transformation matrix <code>flatmatrix</code> will be organized in
	 * the order of:
	 *
	 * <pre>
	 * [m00 m10 m20 m01 m11 m21 m02 m12 m22]
	 * </pre>
	 *
	 * @param ptSrc the source point
	 * @param ptDst the destination point
	 * @param flatmatrix the transformation matrix
	 * @return the transformed destination point
	 */
	public static Point2D inverseTransform(Point2D ptSrc, Point2D ptDst, double[] flatmatrix)
			throws NoninvertibleTransformException
	{
		if (ptSrc == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (ptDst == null)
			if (ptSrc instanceof Point2D.Double)
				ptDst = new Point2D.Double();
			else
				ptDst = new Point2D.Float();
		// Copy source coords into local variables in case src == dst
		double x = ptSrc.getX();
		double y = ptSrc.getY();

		double tmp_x = (flatmatrix[4] * flatmatrix[8] - flatmatrix[7] * flatmatrix[5]) * x
				+ (flatmatrix[6] * flatmatrix[5] - flatmatrix[3] * flatmatrix[8]) * y
				+ (flatmatrix[3] * flatmatrix[7] - flatmatrix[6] * flatmatrix[4]);
		double tmp_y = (flatmatrix[7] * flatmatrix[2] - flatmatrix[1] * flatmatrix[8]) * x
				+ (flatmatrix[0] * flatmatrix[8] - flatmatrix[6] * flatmatrix[2]) * y
				+ (flatmatrix[6] * flatmatrix[1] - flatmatrix[0] * flatmatrix[7]);
		double w = (flatmatrix[1] * flatmatrix[5] - flatmatrix[4] * flatmatrix[2]) * x
				+ (flatmatrix[3] * flatmatrix[2] - flatmatrix[0] * flatmatrix[5]) * y
				+ (flatmatrix[0] * flatmatrix[4] - flatmatrix[3] * flatmatrix[1]);

		double wabs = w;
		if (w < 0)
			wabs = -w;
		if (wabs < PerspectiveTransform.PERSPECTIVE_DIVIDE_EPSILON)
			throw new NoninvertibleTransformException("this transformation cannot be inverted");

		ptDst.setLocation(tmp_x / w, tmp_y / w);

		return ptDst;
	}

	public static BufferedImage copy(BufferedImage image)
	{
		int w = image.getWidth();
		int h = image.getHeight();
		ColorModel destCM = image.getColorModel();
		BufferedImage result = new BufferedImage(destCM,
				destCM.createCompatibleWritableRaster(w, h), destCM.isAlphaPremultiplied(), null);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		return result;
	}

	public static void makeTransparentImage(BufferedImage image)
	{
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
	}

	/**
	 * Transform the specified source point to the destination point with the
	 * specified transformation matrix.
	 * This transformation matrix contains 9 elements, which applies:
	 *
	 * <pre>
	 *	[ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
	 *	[ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
	 *	[ w ]   [  m20  m21  m22  ] [ 1 ]   [ m20x + m21y + m22 ]
	 *
	 *	  x' = (m00x + m01y + m02)
	 *	  y' = (m10x + m11y + m12)
	 *
	 *        w  = (m20x + m21y + m22)
	 *
	 *        X = x' / w
	 *        Y = y' / w
	 * </pre>
	 *
	 * The transformation matrix <code>flatmatrix</code> will be organized in
	 * the order of:
	 *
	 * <pre>
	 * [m00 m10 m20 m01 m11 m21 m02 m12 m22]
	 * </pre>
	 *
	 * @param ptSrc the source point
	 * @param ptDst the destination point
	 * @param flatmatrix the transformation matrix
	 * @return the transformed destination point
	 */
	public static Point2D transform(Point2D ptSrc, Point2D ptDst, double[] flatmatrix)
	{
		if (ptSrc == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (ptDst == null)
			if (ptSrc instanceof Point2D.Double)
				ptDst = new Point2D.Double();
			else
				ptDst = new Point2D.Float();

		double x = ptSrc.getX();
		double y = ptSrc.getY();
		double w = flatmatrix[2] * x + flatmatrix[5] * y + flatmatrix[8];
		ptDst.setLocation((flatmatrix[0] * x + flatmatrix[3] * y + flatmatrix[6]) / w,
				(flatmatrix[1] * x + flatmatrix[4] * y + flatmatrix[7]) / w);

		return ptDst;
	}

	//	static public int convolveBI(BufferedImage src, BufferedImage dst,
	//            Kernel kernel, int edgeHint)
	//	{
	//		
	//	}

	/**
	 * Filter the specified source image to the specified destination image with
	 * the specified transformation matrix and interpolation type.
	 *
	 * @param src the specified source image
	 * @param dst the specified destination image, if <code>null</code> a
	 *            compatible image will be created
	 * @param matrix the 9-elements transformation matrix
	 * @param interpolationType the interpolation type
	 * @return the exit code, &gt1 if succeeded; otherwise failed
	 */
	protected static int transformBIExtend9(BufferedImage src, BufferedImage dst, double[] matrix,
			int interpolationType)
	{
		int height = src.getHeight();
		int width = src.getWidth();
		Rectangle rect = new Rectangle(dst.getWidth(), dst.getHeight());

		int xt, yt, alpha, red, green, blue;
		int[] pixels = new int[4]; // p00, p10, p01, p11
		float ex, ey, dx, dy;
		Point2D.Float p = new Point2D.Float();
		ColorModel model = src.getColorModel();
		Interpolation interpolation;
		switch (interpolationType)
		{
		case PerspectiveTransformOp.TYPE_BICUBIC:
			interpolation = new InterpolationBicubic();
			break;
		default:
		case PerspectiveTransformOp.TYPE_BILINEAR:
			interpolation = new InterpolationBilinear();
			break;
		case PerspectiveTransformOp.TYPE_NEAREST_NEIGHBOR:
			interpolation = new InterpolationNearestNeighbour();
			break;
		}

		// WritableRaster alphaRaster = src.getAlphaRaster();
		WritableRaster raster = src.getRaster();
		try
		{
			for (int x = 0; x < rect.width; x++)
				for (int y = 0; y < rect.height; y++)
				{
					p.setLocation(x, y);
					ImagingLib.inverseTransform(p, p, matrix);

					dx = p.x;
					dy = p.y;
					xt = (int) dx;
					yt = (int) dy;
					ex = dx - xt;
					ey = dy - yt;

					// manage the edge
					// -----------------------------------
					if (xt > width - 1 || xt < -1 || yt > height - 1 || yt < -1)
						continue;

					// in case of overflow
					if (xt >= width - 1)
						xt = width - 2;
					else if (xt < 0)
						xt = 0;
					if (yt >= height - 1)
						yt = height - 2;
					else if (yt < 0)
						yt = 0;

					// data00, data01, data10, data11
					Object[] data = { raster.getDataElements(xt, yt, null),
							raster.getDataElements(xt, yt + 1, null),
							raster.getDataElements(xt + 1, yt, null),
							raster.getDataElements(xt + 1, yt + 1, null) };
					// -----------------------------------
					// alpha channel
					for (int i = 0; i < 4; i++)
						pixels[i] = model.getAlpha(data[i]);
					alpha = interpolation.interpolate(ex, ey, pixels[0], pixels[1], pixels[2],
							pixels[3]);
					// red channel
					// -----------------------------------
					for (int i = 0; i < 4; i++)
						pixels[i] = model.getRed(data[i]);
					red = interpolation.interpolate(ex, ey, pixels[0], pixels[1], pixels[2],
							pixels[3]);
					// green channel
					// -----------------------------------
					for (int i = 0; i < 4; i++)
						pixels[i] = model.getGreen(data[i]);
					green = interpolation.interpolate(ex, ey, pixels[0], pixels[1], pixels[2],
							pixels[3]);
					// blue channel
					// -----------------------------------
					for (int i = 0; i < 4; i++)
						pixels[i] = model.getBlue(data[i]);
					blue = interpolation.interpolate(ex, ey, pixels[0], pixels[1], pixels[2],
							pixels[3]);
					// set pixel
					dst.setRGB(x, y, alpha << 24 | red << 16 | green << 8 | blue);
				}
		}
		catch (NoninvertibleTransformException e)
		{
			return -1;
		}
		return 1;
	}

	/**
	 * Filter the specified source image to the specified destination image with
	 * the specified transformation matrix and interpolation type.
	 *
	 * @param src the specified source raster
	 * @param dst the specified destination raster, if <code>null</code> a
	 *            compatible raster will be created
	 * @param matrix the 9-elements transformation matrix
	 * @param interpolationType the interpolation type
	 * @return the exit code, &gt1 if succeeded; otherwise failed
	 * @throws NoninvertibleTransformException if the transformation cannot be
	 *             inverted
	 */
	protected static int transformRasterExtend9(Raster src, WritableRaster dst, double[] matrix,
			int interpolationType)
	{
		int height = src.getHeight();
		int width = src.getWidth();
		Rectangle rect = new Rectangle(dst.getWidth(), dst.getHeight());

		int xt, yt;
		float ex, ey, dx, dy;
		Point2D.Float p = new Point2D.Float();

		Interpolation interpolation;
		switch (interpolationType)
		{
		case PerspectiveTransformOp.TYPE_BICUBIC:
			interpolation = new InterpolationBicubic();
			break;
		default:
		case PerspectiveTransformOp.TYPE_BILINEAR:
			interpolation = new InterpolationBilinear();
			break;
		case PerspectiveTransformOp.TYPE_NEAREST_NEIGHBOR:
			interpolation = new InterpolationNearestNeighbour();
			break;
		}

		try
		{
			int[][] data = new int[4][4];
			int[] out = new int[4];
			for (int x = 0; x < rect.width; x++)
				for (int y = 0; y < rect.height; y++)
				{
					p.setLocation(x, y);
					ImagingLib.inverseTransform(p, p, matrix);

					dx = p.x;
					dy = p.y;
					xt = (int) dx;
					yt = (int) dy;
					ex = dx - xt;
					ey = dy - yt;

					// manage the edge
					// -----------------------------------
					if (xt > width - 1 || xt < -1 || yt > height - 1 || yt < -1)
						continue;

					// in case of overflow
					if (xt >= width - 1)
						xt = width - 2;
					else if (xt < 0)
						xt = 0;
					if (yt >= height - 1)
						yt = height - 2;
					else if (yt < 0)
						yt = 0;

					// data00, data01, data10, data11
					src.getPixel(xt, yt, data[0]);
					src.getPixel(xt, yt + 1, data[1]);
					src.getPixel(xt + 1, yt, data[2]);
					src.getPixel(xt + 1, yt + 1, data[3]);
					// Do interpolation in spite of alpha channel
					for (int i = 0; i < 4; i++)
						out[i] = interpolation.interpolate(ex, ey, data[0][i], data[1][i],
								data[2][i], data[3][i]);
					// set pixel
					dst.setPixel(x, y, out);
				}
		}
		catch (NoninvertibleTransformException e)
		{
			return -1;
		}
		return 1;
	}
}

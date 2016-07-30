/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * PerspectiveTransform.java is PROPRIETARY/CONFIDENTIAL built in 9:06:47 PM,
 * Jun 28, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.geom;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

import com.github.frankjiang.image4j.math.Quadrangle;

/**
 * A 2D perspective (or projective) transform, used by various OpImages.
 * <p>
 * A perspective transformation is capable of mapping an arbitrary
 * quadrilateral into another arbitrary quadrilateral, while
 * preserving the straightness of lines. Unlike an affine
 * transformation, the parallelism of lines in the source is not
 * necessarily preserved in the output.
 * <p>
 * Such a coordinate transformation can be represented by a 3x3
 * matrix which transforms homogenous source coordinates
 * <code>(x,&nbsp;y,&nbsp;1)</code> into destination coordinates
 * <code>(x',&nbsp;y',&nbsp;w)</code>. To convert back into non-homogenous
 * coordinates (X, Y), <code>x'</code> and <code>y'</code> are divided by
 * <code>w</code>.
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
 */
public final class PerspectiveTransform extends AffineTransform // implements GeometryTransform, Cloneable, Serializable
{
	/**
	 * The epsilon (accuracy) for the division operation in this transformation.
	 */
	public static final double	PERSPECTIVE_DIVIDE_EPSILON	= 1.0e-10;

	/**
	 * serialVersionUID.
	 */
	private static final long	serialVersionUID			= 338876943317029640L;

	/**
	 * Creates a PerspectiveTransform that maps an arbitrary
	 * quadrilateral onto another arbitrary quadrilateral.
	 *
	 * <pre>
	 * (x0, y0) -> (x0p, y0p)
	 * (x1, y1) -> (x1p, y1p)
	 * (x2, y2) -> (x2p, y2p)
	 * (x3, y3) -> (x3p, y3p)
	 * </pre>
	 */
	public static PerspectiveTransform getQuadToQuad(double x0, double y0, double x1, double y1,
			double x2, double y2, double x3, double y3, double x0p, double y0p, double x1p,
			double y1p, double x2p, double y2p, double x3p, double y3p)
	{
		PerspectiveTransform tx1 = PerspectiveTransform.getQuadToSquare(x0, y0, x1, y1, x2, y2, x3,
				y3);

		PerspectiveTransform tx2 = PerspectiveTransform.getSquareToQuad(x0p, y0p, x1p, y1p, x2p,
				y2p, x3p, y3p);

		tx1.concatenate(tx2);
		return tx1;
	}

	/**
	 * Creates a PerspectiveTransform that maps an arbitrary
	 * quadrilateral onto another arbitrary quadrilateral.
	 *
	 * <pre>
	 * (x0, y0) -> (x0p, y0p)
	 * (x1, y1) -> (x1p, y1p)
	 * (x2, y2) -> (x2p, y2p)
	 * (x3, y3) -> (x3p, y3p)
	 * </pre>
	 */
	public static PerspectiveTransform getQuadToQuad(float x0, float y0, float x1, float y1,
			float x2, float y2, float x3, float y3, float x0p, float y0p, float x1p, float y1p,
			float x2p, float y2p, float x3p, float y3p)
	{
		return PerspectiveTransform.getQuadToQuad((double) x0, (double) y0, (double) x1,
				(double) y1, (double) x2, (double) y2, (double) x3, (double) y3, (double) x0p,
				(double) y0p, (double) x1p, (double) y1p, (double) x2p, (double) y2p, (double) x3p,
				(double) y3p);
	}

	/**
	 * Creates a PerspectiveTransform that maps an arbitrary
	 * quadrilateral onto another arbitrary quadrilateral.
	 *
	 * <pre>
	 * src -> dst
	 * </pre>
	 */
	public static PerspectiveTransform getQuadToQuad(Quadrangle src, Quadrangle dst)
	{
		// @formatter:off
		return PerspectiveTransform.getQuadToQuad(
				src.xpoints[0], src.ypoints[0], src.xpoints[1], src.ypoints[1],
				src.xpoints[2], src.ypoints[2], src.xpoints[3], src.ypoints[3],

				dst.xpoints[0], dst.ypoints[0], dst.xpoints[1], dst.ypoints[1],
				dst.xpoints[2], dst.ypoints[2], dst.xpoints[3], dst.ypoints[3]);
		// @formatter:on
	}

	/**
	 * Creates a PerspectiveTransform that maps an arbitrary
	 * quadrilateral onto the unit square.
	 *
	 * <pre>
	 * (x0, y0) -> (0, 0)
	 * (x1, y1) -> (1, 0)
	 * (x2, y2) -> (1, 1)
	 * (x3, y3) -> (0, 1)
	 * </pre>
	 */
	public static PerspectiveTransform getQuadToSquare(double x0, double y0, double x1, double y1,
			double x2, double y2, double x3, double y3)
	{
		PerspectiveTransform tx = new PerspectiveTransform();
		PerspectiveTransform.getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
		tx.makeAdjoint();
		return tx;
	}

	/**
	 * Creates a PerspectiveTransform that maps an arbitrary
	 * quadrilateral onto the unit square.
	 *
	 * <pre>
	 * (x0, y0) -> (0, 0)
	 * (x1, y1) -> (1, 0)
	 * (x2, y2) -> (1, 1)
	 * (x3, y3) -> (0, 1)
	 * </pre>
	 */
	public static PerspectiveTransform getQuadToSquare(float x0, float y0, float x1, float y1,
			float x2, float y2, float x3, float y3)
	{
		return PerspectiveTransform.getQuadToSquare((double) x0, (double) y0, (double) x1,
				(double) y1, (double) x2, (double) y2, (double) x3, (double) y3);
	}

	/**
	 * Creates a PerspectiveTransform that maps the unit square
	 * onto an arbitrary quadrilateral.
	 *
	 * <pre>
	 * (0, 0) -> (x0, y0)
	 * (1, 0) -> (x1, y1)
	 * (1, 1) -> (x2, y2)
	 * (0, 1) -> (x3, y3)
	 * </pre>
	 */
	public static PerspectiveTransform getSquareToQuad(double x0, double y0, double x1, double y1,
			double x2, double y2, double x3, double y3)
	{
		PerspectiveTransform tx = new PerspectiveTransform();
		PerspectiveTransform.getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
		return tx;
	}

	private static final void getSquareToQuad(double x0, double y0, double x1, double y1, double x2,
			double y2, double x3, double y3, PerspectiveTransform tx)
	{
		double dx3 = x0 - x1 + x2 - x3;
		double dy3 = y0 - y1 + y2 - y3;

		tx.m22 = 1.0F;

		if (dx3 == 0.0F && dy3 == 0.0F)
		{ // to do: use tolerance
			tx.m00 = x1 - x0;
			tx.m01 = x2 - x1;
			tx.m02 = x0;
			tx.m10 = y1 - y0;
			tx.m11 = y2 - y1;
			tx.m12 = y0;
			tx.m20 = 0.0F;
			tx.m21 = 0.0F;
		}
		else
		{
			double dx1 = x1 - x2;
			double dy1 = y1 - y2;
			double dx2 = x3 - x2;
			double dy2 = y3 - y2;

			double invdet = 1.0F / (dx1 * dy2 - dx2 * dy1);
			tx.m20 = (dx3 * dy2 - dx2 * dy3) * invdet;
			tx.m21 = (dx1 * dy3 - dx3 * dy1) * invdet;
			tx.m00 = x1 - x0 + tx.m20 * x1;
			tx.m01 = x3 - x0 + tx.m21 * x3;
			tx.m02 = x0;
			tx.m10 = y1 - y0 + tx.m20 * y1;
			tx.m11 = y3 - y0 + tx.m21 * y3;
			tx.m12 = y0;
		}
	}

	/**
	 * Creates a PerspectiveTransform that maps the unit square
	 * onto an arbitrary quadrilateral.
	 *
	 * <pre>
	 * (0, 0) -> (x0, y0)
	 * (1, 0) -> (x1, y1)
	 * (1, 1) -> (x2, y2)
	 * (0, 1) -> (x3, y3)
	 * </pre>
	 */
	public static PerspectiveTransform getSquareToQuad(float x0, float y0, float x1, float y1,
			float x2, float y2, float x3, float y3)
	{
		return PerspectiveTransform.getSquareToQuad((double) x0, (double) y0, (double) x1,
				(double) y1, (double) x2, (double) y2, (double) x3, (double) y3);
	}

	/**
	 * An element of the transform matrix.
	 */
	double m00, m01, m02, m10, m11, m12, m20, m21, m22;

	/**
	 * Constructs an identity PerspectiveTransform.
	 */
	protected PerspectiveTransform()
	{
		this.m00 = this.m11 = this.m22 = 1.0;
		this.m01 = this.m02 = this.m10 = this.m12 = this.m20 = this.m21 = 0.0;
	}

	/**
	 * Constructs a new PerspectiveTransform with the same effect
	 * as an existing AffineTransform.
	 *
	 * @throws IllegalArgumentException if transform is null
	 */
	public PerspectiveTransform(AffineTransform transform)
	{
		if (transform == null)
			throw new IllegalArgumentException("the transformation cannot be null");

		this.m00 = transform.getScaleX();
		this.m01 = transform.getShearX();
		this.m02 = transform.getTranslateX();
		this.m10 = transform.getShearY();
		this.m11 = transform.getScaleY();
		this.m12 = transform.getTranslateY();
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Constructs a new PerspectiveTransform from 9 doubles.
	 *
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m10
	 * @param m11
	 * @param m12
	 * @param m20
	 * @param m21
	 * @param m22
	 */
	public PerspectiveTransform(double m00, double m01, double m02, double m10, double m11,
			double m12, double m20, double m21, double m22)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	}

	/**
	 * Constructs a new PerspectiveTransform from a one-dimensional
	 * array of 9 doubles, in row-major order.
	 * The values in the array are assumed to be
	 * { m00 m01 m02 m10 m11 m12 m20 m21 m22 }.
	 *
	 * @param flatmatrix the input matrix
	 * @throws IllegalArgumentException if flatmatrix is null
	 * @throws ArrayIndexOutOfBoundsException if flatmatrix is too small
	 */
	public PerspectiveTransform(double[] flatmatrix)
	{
		if (flatmatrix == null)
			throw new IllegalArgumentException("the input matrix cannot be null");

		this.m00 = flatmatrix[0];
		this.m01 = flatmatrix[1];
		this.m02 = flatmatrix[2];
		this.m10 = flatmatrix[3];
		this.m11 = flatmatrix[4];
		this.m12 = flatmatrix[5];
		this.m20 = flatmatrix[6];
		this.m21 = flatmatrix[7];
		this.m22 = flatmatrix[8];
	}

	/**
	 * Constructs a new PerspectiveTransform from a two-dimensional
	 * array of doubles.
	 *
	 * @param matrix the input matrix
	 * @throws IllegalArgumentException if matrix is null
	 * @throws ArrayIndexOutOfBoundsException if matrix is too small
	 */
	public PerspectiveTransform(double[][] matrix)
	{
		if (matrix == null)
			throw new IllegalArgumentException("the input matrix cannot be null");

		this.m00 = matrix[0][0];
		this.m01 = matrix[0][1];
		this.m02 = matrix[0][2];
		this.m10 = matrix[1][0];
		this.m11 = matrix[1][1];
		this.m12 = matrix[1][2];
		this.m20 = matrix[2][0];
		this.m21 = matrix[2][1];
		this.m22 = matrix[2][2];
	}

	/**
	 * Post-concatenates a given AffineTransform to this transform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	@Override
	public void concatenate(AffineTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("the input transformation cannot be null");

		// Extend Tx: Tx.m20 = 0, Tx.m21 = 0, Tx.m22 = 1

		double tx_m00 = Tx.getScaleX();
		double tx_m01 = Tx.getShearX();
		double tx_m02 = Tx.getTranslateX();
		double tx_m10 = Tx.getShearY();
		double tx_m11 = Tx.getScaleY();
		double tx_m12 = Tx.getTranslateY();

		double m00p = this.m00 * tx_m00 + this.m10 * tx_m01 + this.m20 * tx_m02;
		double m01p = this.m01 * tx_m00 + this.m11 * tx_m01 + this.m21 * tx_m02;
		double m02p = this.m02 * tx_m00 + this.m12 * tx_m01 + this.m22 * tx_m02;
		double m10p = this.m00 * tx_m10 + this.m10 * tx_m11 + this.m20 * tx_m12;
		double m11p = this.m01 * tx_m10 + this.m11 * tx_m11 + this.m21 * tx_m12;
		double m12p = this.m02 * tx_m10 + this.m12 * tx_m11 + this.m22 * tx_m12;
		double m20p = this.m20;
		double m21p = this.m21;
		double m22p = this.m22;

		this.m00 = m00p;
		this.m10 = m10p;
		this.m20 = m20p;
		this.m01 = m01p;
		this.m11 = m11p;
		this.m21 = m21p;
		this.m02 = m02p;
		this.m12 = m12p;
		this.m22 = m22p;
	}

	/**
	 * Post-concatenates a given PerspectiveTransform to this transform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	public void concatenate(PerspectiveTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		double m00p = this.m00 * Tx.m00 + this.m10 * Tx.m01 + this.m20 * Tx.m02;
		double m10p = this.m00 * Tx.m10 + this.m10 * Tx.m11 + this.m20 * Tx.m12;
		double m20p = this.m00 * Tx.m20 + this.m10 * Tx.m21 + this.m20 * Tx.m22;
		double m01p = this.m01 * Tx.m00 + this.m11 * Tx.m01 + this.m21 * Tx.m02;
		double m11p = this.m01 * Tx.m10 + this.m11 * Tx.m11 + this.m21 * Tx.m12;
		double m21p = this.m01 * Tx.m20 + this.m11 * Tx.m21 + this.m21 * Tx.m22;
		double m02p = this.m02 * Tx.m00 + this.m12 * Tx.m01 + this.m22 * Tx.m02;
		double m12p = this.m02 * Tx.m10 + this.m12 * Tx.m11 + this.m22 * Tx.m12;
		double m22p = this.m02 * Tx.m20 + this.m12 * Tx.m21 + this.m22 * Tx.m22;

		this.m00 = m00p;
		this.m10 = m10p;
		this.m20 = m20p;
		this.m01 = m01p;
		this.m11 = m11p;
		this.m21 = m21p;
		this.m02 = m02p;
		this.m12 = m12p;
		this.m22 = m22p;
	}

	/**
	 * Returns a new PerpectiveTransform that is the adjoint,
	 * of the current transform. The adjoint is defined as
	 * the matrix of cofactors, which in turn are the determinants
	 * of the submatrices defined by removing the row and column
	 * of each element from the original matrix in turn.
	 * <p>
	 * The adjoint is a scalar multiple of the inverse matrix.
	 * Because points to be transformed are converted into homogeneous
	 * coordinates, where scalar factors are irrelevant, the adjoint
	 * may be used in place of the true inverse. Since it is unnecessary
	 * to normalize the adjoint, it is both faster to compute and more
	 * numerically stable than the true inverse.
	 */
	public PerspectiveTransform createAdjoint() throws CloneNotSupportedException
	{
		PerspectiveTransform tx = (PerspectiveTransform) this.clone();
		tx.makeAdjoint();
		return tx;
	}

	/**
	 * Returns a new PerpectiveTransform that is the inverse
	 * of the current transform.
	 *
	 * @return the inverted transformation
	 * @throws NoninvertibleTransformException if transform cannot be inverted
	 */
	public PerspectiveTransform createInvertedTransformation()
			throws NoninvertibleTransformException
	{
		PerspectiveTransform tx = (PerspectiveTransform) this.clone();
		tx.makeAdjoint();
		if (FastMath.abs(tx.m22) < PerspectiveTransform.PERSPECTIVE_DIVIDE_EPSILON)
			throw new NoninvertibleTransformException("This transformation cannot be inverted.");
		tx.normalize();
		return tx;
	}

	/**
	 * @see com.github.frankjiang.image4j.geom.GeometryTransform#createTransformedShape(java.awt.Shape)
	 */
	@Override
	public Rectangle createTransformedShape(Shape shape)
	{
		Rectangle rect = shape.getBounds();
		Point[] points = { new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y),
				new Point(rect.x, rect.y + rect.height),
				new Point(rect.x + rect.width, rect.y + rect.height) };
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		for (Point p : points)
		{
			this.transform(p, p);
			if (p.x > maxX)
				maxX = p.x;
			if (p.x < minX)
				minX = p.x;
			if (p.y > maxY)
				maxY = p.y;
			if (p.y < minY)
				minY = p.y;
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Tests if this PerspectiveTransform equals a supplied one.
	 *
	 * @param obj The PerspectiveTransform to be compared to this one.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PerspectiveTransform))
			return false;

		PerspectiveTransform a = (PerspectiveTransform) obj;

		return this.m00 == a.m00 && this.m10 == a.m10 && this.m20 == a.m20 && this.m01 == a.m01
				&& this.m11 == a.m11 && this.m21 == a.m21 && this.m02 == a.m02 && this.m12 == a.m12
				&& this.m22 == a.m22;
	}

	/**
	 * Returns the determinant of the matrix representation of the
	 * transform.
	 */
	@Override
	public double getDeterminant()
	{
		return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
				- this.m01 * (this.m10 * this.m22 - this.m12 * this.m20)
				+ this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
	}

	/**
	 * Retrieves the 6 specifiable values in the 3x3 affine transformation
	 * matrix and places them into an array of double precisions values.
	 * The values are stored in the array as
	 * {&nbsp;m00&nbsp;m10&nbsp;m01&nbsp;m11&nbsp;m02&nbsp;m12&nbsp;}.
	 * An array of 4 doubles can also be specified, in which case only the
	 * first four elements representing the non-transform
	 * parts of the array are retrieved and the values are stored into
	 * the array as {&nbsp;m00&nbsp;m10&nbsp;m01&nbsp;m11&nbsp;}
	 *
	 * @param flatmatrix the double array used to store the returned
	 *            values.
	 * @see #getScaleX
	 * @see #getScaleY
	 * @see #getShearX
	 * @see #getShearY
	 * @see #getTranslateX
	 * @see #getTranslateY
	 * @since 1.2
	 */
	@Override
	public void getMatrix(double[] flatmatrix)
	{
		if (flatmatrix.length < 9)
			return;
		flatmatrix[0] = this.m00;
		flatmatrix[1] = this.m10;
		flatmatrix[2] = this.m20;
		flatmatrix[3] = this.m01;
		flatmatrix[4] = this.m11;
		flatmatrix[5] = this.m21;
		flatmatrix[6] = this.m02;
		flatmatrix[7] = this.m12;
		flatmatrix[8] = this.m22;
	}

	/**
	 * Retrieves the 9 specifiable values in the 3x3 affine
	 * transformation matrix into a 2-dimensional array of double
	 * precision values. The values are stored into the 2-dimensional
	 * array using the row index as the first subscript and the column
	 * index as the second.
	 *
	 * @param matrix The 2-dimensional double array to store the
	 *            returned values. The array is assumed to be at least 3x3.
	 * @throws ArrayIndexOutOfBoundsException if matrix is too small
	 */
	public double[][] getMatrix(double[][] matrix)
	{
		if (matrix == null)
			matrix = new double[3][3];

		matrix[0][0] = this.m00;
		matrix[0][1] = this.m01;
		matrix[0][2] = this.m02;
		matrix[1][0] = this.m10;
		matrix[1][1] = this.m11;
		matrix[1][2] = this.m12;
		matrix[2][0] = this.m20;
		matrix[2][1] = this.m21;
		matrix[2][2] = this.m22;

		return matrix;
	}

	/**
	 * Inverse transforms an array of double precision coordinates by
	 * this transform.
	 *
	 * @param srcPts The array containing the source point coordinates.
	 *            Each point is stored as a pair of x,y coordinates.
	 * @param dstPts The array where the transformed point coordinates are
	 *            returned. Each point is stored as a pair of x,y coordinates.
	 * @param srcOff The offset to the first point to be transformed
	 *            in the source array.
	 * @param dstOff The offset to the location where the first transformed
	 *            point is stored in the destination array.
	 * @param numPts The number of point objects to be transformed.
	 * @throws NoninvertibleTransformException if the matrix cannot be
	 *             inverted.
	 * @throws IllegalArgumentException if srcPts is null
	 * @throws ArrayIndexOutOfBoundsException if srcPts is too small
	 * @throws NoninvertibleTransformException transform cannot be inverted
	 */
	@Override
	public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff,
			int numPts) throws NoninvertibleTransformException
	{
		if (srcPts == null)
			throw new IllegalArgumentException("the input points cannot be null");

		if (dstPts == null)
			dstPts = new double[numPts * 2 + dstOff];

		while (numPts-- > 0)
		{
			double x = srcPts[srcOff++];
			double y = srcPts[srcOff++];

			double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x
					+ (this.m02 * this.m21 - this.m01 * this.m22) * y
					+ (this.m01 * this.m12 - this.m02 * this.m11);
			double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x
					+ (this.m00 * this.m22 - this.m02 * this.m20) * y
					+ (this.m02 * this.m10 - this.m00 * this.m12);
			double w = (this.m10 * this.m21 - this.m11 * this.m20) * x
					+ (this.m01 * this.m20 - this.m00 * this.m21) * y
					+ (this.m00 * this.m11 - this.m01 * this.m10);

			double wabs = w;
			if (w < 0)
				wabs = -w;
			if (wabs < PerspectiveTransform.PERSPECTIVE_DIVIDE_EPSILON)
				throw new NoninvertibleTransformException("this transformation cannot be inverted");

			dstPts[dstOff++] = tmp_x / w;
			dstPts[dstOff++] = tmp_y / w;
		}
	}

	/**
	 * Inverse transforms the specified ptSrc and stores the result in ptDst.
	 * If ptDst is null, a new Point2D object will be allocated before
	 * storing. In either case, ptDst containing the transformed point
	 * is returned for convenience.
	 * Note that ptSrc and ptDst can the same. In this case, the input
	 * point will be overwritten with the transformed point.
	 *
	 * @param ptSrc The point to be inverse transformed.
	 * @param ptDst The resulting transformed point.
	 * @throws NoninvertibleTransformException if the matrix cannot be
	 *             inverted.
	 * @throws IllegalArgumentException if ptSrc is null
	 */
	@Override
	public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst)
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

		double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x
				+ (this.m02 * this.m21 - this.m01 * this.m22) * y
				+ (this.m01 * this.m12 - this.m02 * this.m11);
		double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x
				+ (this.m00 * this.m22 - this.m02 * this.m20) * y
				+ (this.m02 * this.m10 - this.m00 * this.m12);
		double w = (this.m10 * this.m21 - this.m11 * this.m20) * x
				+ (this.m01 * this.m20 - this.m00 * this.m21) * y
				+ (this.m00 * this.m11 - this.m01 * this.m10);

		double wabs = w;
		if (w < 0)
			wabs = -w;
		if (wabs < PerspectiveTransform.PERSPECTIVE_DIVIDE_EPSILON)
			throw new NoninvertibleTransformException("this transformation cannot be inverted");

		ptDst.setLocation(tmp_x / w, tmp_y / w);

		return ptDst;
	}

	/**
	 * Returns the boolean true value if this PerspectiveTransform is an
	 * identity transform. Returns false otherwise.
	 */
	@Override
	public boolean isIdentity()
	{
		return this.m01 == 0.0 && this.m02 == 0.0 && this.m10 == 0.0 && this.m12 == 0.0
				&& this.m20 == 0.0 && this.m21 == 0.0 && this.m22 != 0.0
				&& this.m00 / this.m22 == 1.0 && this.m11 / this.m22 == 1.0;
	}

	/**
	 * Replaces the matrix with its adjoint.
	 */
	private final void makeAdjoint()
	{
		double m00p = this.m11 * this.m22 - this.m12 * this.m21;
		double m01p = this.m12 * this.m20 - this.m10 * this.m22; // flipped sign
		double m02p = this.m10 * this.m21 - this.m11 * this.m20;
		double m10p = this.m02 * this.m21 - this.m01 * this.m22; // flipped sign
		double m11p = this.m00 * this.m22 - this.m02 * this.m20;
		double m12p = this.m01 * this.m20 - this.m00 * this.m21; // flipped sign
		double m20p = this.m01 * this.m12 - this.m02 * this.m11;
		double m21p = this.m02 * this.m10 - this.m00 * this.m12; // flipped sign
		double m22p = this.m00 * this.m11 - this.m01 * this.m10;

		// Transpose and copy sub-determinants
		this.m00 = m00p;
		this.m01 = m10p;
		this.m02 = m20p;
		this.m10 = m01p;
		this.m11 = m11p;
		this.m12 = m21p;
		this.m20 = m02p;
		this.m21 = m12p;
		this.m22 = m22p;
	}

	/**
	 * Scales the matrix elements so m22 is equal to 1.0.
	 * m22 must not be equal to 0.
	 */
	private final void normalize()
	{
		double invscale = 1.0 / this.m22;
		this.m00 *= invscale;
		this.m01 *= invscale;
		this.m02 *= invscale;
		this.m10 *= invscale;
		this.m11 *= invscale;
		this.m12 *= invscale;
		this.m20 *= invscale;
		this.m21 *= invscale;
		this.m22 = 1.0;
	}

	/**
	 * Pre-concatenates a given AffineTransform to this transform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	@Override
	public void preConcatenate(AffineTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		// Extend Tx: Tx.m20 = 0, Tx.m21 = 0, Tx.m22 = 1

		double tx_m00 = Tx.getScaleX();
		double tx_m01 = Tx.getShearX();
		double tx_m02 = Tx.getTranslateX();
		double tx_m10 = Tx.getShearY();
		double tx_m11 = Tx.getScaleY();
		double tx_m12 = Tx.getTranslateY();

		double m00p = tx_m00 * this.m00 + tx_m10 * this.m01;
		double m01p = tx_m01 * this.m00 + tx_m11 * this.m01;
		double m02p = tx_m02 * this.m00 + tx_m12 * this.m01 + this.m02;
		double m10p = tx_m00 * this.m10 + tx_m10 * this.m11;
		double m11p = tx_m01 * this.m10 + tx_m11 * this.m11;
		double m12p = tx_m02 * this.m10 + tx_m12 * this.m11 + this.m12;
		double m20p = tx_m00 * this.m20 + tx_m10 * this.m21;
		double m21p = tx_m01 * this.m20 + tx_m11 * this.m21;
		double m22p = tx_m02 * this.m20 + tx_m12 * this.m21 + this.m22;

		this.m00 = m00p;
		this.m10 = m10p;
		this.m20 = m20p;
		this.m01 = m01p;
		this.m11 = m11p;
		this.m21 = m21p;
		this.m02 = m02p;
		this.m12 = m12p;
		this.m22 = m22p;
	}

	/**
	 * Pre-concatenates a given PerspectiveTransform to this transform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	public void preConcatenate(PerspectiveTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		double m00p = Tx.m00 * this.m00 + Tx.m10 * this.m01 + Tx.m20 * this.m02;
		double m10p = Tx.m00 * this.m10 + Tx.m10 * this.m11 + Tx.m20 * this.m12;
		double m20p = Tx.m00 * this.m20 + Tx.m10 * this.m21 + Tx.m20 * this.m22;
		double m01p = Tx.m01 * this.m00 + Tx.m11 * this.m01 + Tx.m21 * this.m02;
		double m11p = Tx.m01 * this.m10 + Tx.m11 * this.m11 + Tx.m21 * this.m12;
		double m21p = Tx.m01 * this.m20 + Tx.m11 * this.m21 + Tx.m21 * this.m22;
		double m02p = Tx.m02 * this.m00 + Tx.m12 * this.m01 + Tx.m22 * this.m02;
		double m12p = Tx.m02 * this.m10 + Tx.m12 * this.m11 + Tx.m22 * this.m12;
		double m22p = Tx.m02 * this.m20 + Tx.m12 * this.m21 + Tx.m22 * this.m22;

		this.m00 = m00p;
		this.m10 = m10p;
		this.m20 = m20p;
		this.m01 = m01p;
		this.m11 = m11p;
		this.m21 = m21p;
		this.m02 = m02p;
		this.m12 = m12p;
		this.m22 = m22p;
	}

	/**
	 * Concatenates this transform with a rotation transformation.
	 * This is equivalent to calling concatenate(R), where R is an
	 * PerspectiveTransform represented by the following matrix:
	 *
	 * <pre>
	 *		[   cos(theta)    -sin(theta)    0   ]
	 *		[   sin(theta)     cos(theta)    0   ]
	 *		[       0              0         1   ]
	 * </pre>
	 *
	 * Rotating with a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 *
	 * @param theta The angle of rotation in radians.
	 */
	@Override
	public void rotate(double theta)
	{
		PerspectiveTransform Tx = new PerspectiveTransform();
		Tx.setToRotation(theta);
		this.concatenate(Tx);
	}

	/**
	 * Concatenates this transform with a translated rotation transformation.
	 * This is equivalent to the following sequence of calls:
	 *
	 * <pre>
	 * translate(x, y);
	 * rotate(theta);
	 * translate(-x, -y);
	 * </pre>
	 *
	 * Rotating with a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 *
	 * @param theta The angle of rotation in radians.
	 * @param x The X coordinate of the origin of the rotation
	 * @param y The Y coordinate of the origin of the rotation
	 */
	@Override
	public void rotate(double theta, double x, double y)
	{
		PerspectiveTransform Tx = new PerspectiveTransform();
		Tx.setToRotation(theta, x, y);
		this.concatenate(Tx);
	}

	/**
	 * Concatenates this transform with a scaling transformation.
	 * This is equivalent to calling concatenate(S), where S is an
	 * PerspectiveTransform represented by the following matrix:
	 *
	 * <pre>
	 *		[   sx   0    0   ]
	 *		[   0    sy   0   ]
	 *		[   0    0    1   ]
	 * </pre>
	 *
	 * @param sx The X axis scale factor.
	 * @param sy The Y axis scale factor.
	 */
	@Override
	public void scale(double sx, double sy)
	{
		PerspectiveTransform Tx = new PerspectiveTransform();
		Tx.setToScale(sx, sy);
		this.concatenate(Tx);
	}

	/**
	 * Resets this transform to the Identity transform.
	 */
	@Override
	public void setToIdentity()
	{
		this.m00 = this.m11 = this.m22 = 1.0;
		this.m01 = this.m10 = this.m02 = this.m20 = this.m12 = this.m21 = 0.0;
	}

	/**
	 * Sets this transform to a rotation transformation.
	 * The matrix representing this transform becomes:
	 *
	 * <pre>
	 *		[   cos(theta)    -sin(theta)    0   ]
	 *		[   sin(theta)     cos(theta)    0   ]
	 *		[       0              0         1   ]
	 * </pre>
	 *
	 * Rotating with a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 *
	 * @param theta The angle of rotation in radians.
	 */
	@Override
	public void setToRotation(double theta)
	{
		this.m00 = FastMath.cos(theta);
		this.m01 = -FastMath.sin(theta);
		this.m02 = 0.0;
		this.m10 = -this.m01;    // FastMath.sin(theta);
		this.m11 = this.m00;      // FastMath.cos(theta);
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Sets this transform to a rotation transformation
	 * about a specified point (x, y). This is equivalent
	 * to the following sequence of calls:
	 *
	 * <pre>
	 * setToTranslate(x, y);
	 * rotate(theta);
	 * translate(-x, -y);
	 * </pre>
	 *
	 * Rotating with a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 *
	 * @param theta The angle of rotation in radians.
	 * @param x The X coordinate of the origin of the rotation
	 * @param y The Y coordinate of the origin of the rotation
	 */
	@Override
	public void setToRotation(double theta, double x, double y)
	{
		this.setToRotation(theta);
		double sin = this.m10;
		double oneMinusCos = 1.0 - this.m00;
		this.m02 = x * oneMinusCos + y * sin;
		this.m12 = y * oneMinusCos - x * sin;
	}

	/**
	 * Sets this transform to a scale transformation
	 * with scale factors sx and sy.
	 * The matrix representing this transform becomes:
	 *
	 * <pre>
	 *		[   sx   0    0   ]
	 *		[   0    sy   0   ]
	 *		[   0    0    1   ]
	 * </pre>
	 *
	 * @param sx The X axis scale factor.
	 * @param sy The Y axis scale factor.
	 */
	@Override
	public void setToScale(double sx, double sy)
	{
		this.m00 = sx;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = sy;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Sets this transform to a shearing transformation
	 * with shear factors sx and sy.
	 * The matrix representing this transform becomes:
	 *
	 * <pre>
	 *		[   1  shx    0   ]
	 *		[ shy    1    0   ]
	 *		[   0    0    1   ]
	 * </pre>
	 *
	 * @param shx The factor by which coordinates are shifted towards
	 *            the positive X axis direction according to their Y
	 *            coordinate.
	 * @param shy The factor by which coordinates are shifted towards
	 *            the positive Y axis direction according to their X
	 *            coordinate.
	 */
	@Override
	public void setToShear(double shx, double shy)
	{
		this.m00 = 1.0;
		this.m01 = shx;
		this.m02 = 0.0;
		this.m10 = shy;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Sets this transform to a translation transformation.
	 * The matrix representing this transform becomes:
	 *
	 * <pre>
	 *		[   1    0    tx  ]
	 *		[   0    1    ty  ]
	 *		[   0    0    1   ]
	 * </pre>
	 *
	 * @param tx The distance by which coordinates are translated in the
	 *            X axis direction
	 * @param ty The distance by which coordinates are translated in the
	 *            Y axis direction
	 */
	@Override
	public void setToTranslation(double tx, double ty)
	{
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = tx;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = ty;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Sets this transform to a given AffineTransform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	@Override
	public void setTransform(AffineTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("the affine transformation cannot be null");

		this.m00 = Tx.getScaleX();
		this.m01 = Tx.getShearX();
		this.m02 = Tx.getTranslateX();
		this.m10 = Tx.getShearY();
		this.m11 = Tx.getScaleY();
		this.m12 = Tx.getTranslateY();
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	/**
	 * Sets this transform using a two-dimensional array of double precision
	 * values. The row index is first, and the column index is second.
	 *
	 * @param matrix The 2D double array to be used for setting this transform.
	 *            The array is assumed to be at least 3x3.
	 * @throws IllegalArgumentException if matrix is null
	 * @throws ArrayIndexOutOfBoundsException if matrix is too small
	 * @since JAI 1.1
	 */
	public void setTransform(double[][] matrix)
	{
		if (matrix == null)
			throw new IllegalArgumentException("the input matrix cannot be null");

		this.m00 = matrix[0][0];
		this.m01 = matrix[0][1];
		this.m02 = matrix[0][2];
		this.m10 = matrix[1][0];
		this.m11 = matrix[1][1];
		this.m12 = matrix[1][2];
		this.m20 = matrix[2][0];
		this.m21 = matrix[2][1];
		this.m22 = matrix[2][2];
	}

	/**
	 * Sets this transform to a given PerspectiveTransform.
	 *
	 * @throws IllegalArgumentException if Tx is null
	 */
	public void setTransform(PerspectiveTransform Tx)
	{
		if (Tx == null)
			throw new IllegalArgumentException("the affine transformation cannot be null");

		this.m00 = Tx.m00;
		this.m01 = Tx.m01;
		this.m02 = Tx.m02;
		this.m10 = Tx.m10;
		this.m11 = Tx.m11;
		this.m12 = Tx.m12;
		this.m20 = Tx.m20;
		this.m21 = Tx.m21;
		this.m22 = Tx.m22;
	}

	/**
	 * Concatenates this transform with a shearing transformation.
	 * This is equivalent to calling concatenate(SH), where SH is an
	 * PerspectiveTransform represented by the following matrix:
	 *
	 * <pre>
	 *		[   1   shx   0   ]
	 *		[  shy   1    0   ]
	 *		[   0    0    1   ]
	 * </pre>
	 *
	 * @param shx The factor by which coordinates are shifted towards
	 *            the positive X axis direction according to their Y
	 *            coordinate.
	 * @param shy The factor by which coordinates are shifted towards
	 *            the positive Y axis direction according to their X
	 *            coordinate.
	 */
	@Override
	public void shear(double shx, double shy)
	{
		PerspectiveTransform Tx = new PerspectiveTransform();
		Tx.setToShear(shx, shy);
		this.concatenate(Tx);
	}

	/**
	 * Returns a String that represents the value of this Object.
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Perspective transform matrix\n");
		sb.append(this.m00);
		sb.append("\t");
		sb.append(this.m01);
		sb.append("\t");
		sb.append(this.m02);
		sb.append("\n");
		sb.append(this.m10);
		sb.append("\t");
		sb.append(this.m11);
		sb.append("\t");
		sb.append(this.m12);
		sb.append("\n");
		sb.append(this.m20);
		sb.append("\t");
		sb.append(this.m21);
		sb.append("\t");
		sb.append(this.m22);
		sb.append("\n");
		return new String(sb);
	}

	/**
	 * Transforms an array of double precision coordinates by this transform.
	 *
	 * @param srcPts The array containing the source point coordinates.
	 *            Each point is stored as a pair of x,y coordinates.
	 * @param dstPts The array where the transformed point coordinates are
	 *            returned. Each point is stored as a pair of x,y coordinates.
	 * @param srcOff The offset to the first point to be transformed
	 *            in the source array.
	 * @param dstOff The offset to the location where the first transformed
	 *            point is stored in the destination array.
	 * @param numPts The number of point objects to be transformed.
	 * @throws IllegalArgumentException if srcPts is null
	 * @throws ArrayIndexOutOfBoundsException if srcPts is too small
	 */
	@Override
	public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
	{

		if (srcPts == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (dstPts == null)
			dstPts = new double[numPts * 2 + dstOff];

		while (numPts-- > 0)
		{
			double x = srcPts[srcOff++];
			double y = srcPts[srcOff++];
			double w = this.m20 * x + this.m21 * y + this.m22;

			if (w == 0)
			{
				dstPts[dstOff++] = x;
				dstPts[dstOff++] = y;
			}
			else
			{
				dstPts[dstOff++] = (this.m00 * x + this.m01 * y + this.m02) / w;
				dstPts[dstOff++] = (this.m10 * x + this.m11 * y + this.m12) / w;
			}
		}
	}

	/**
	 * Transforms an array of double precision coordinates by this transform,
	 * storing the results into an array of floats.
	 *
	 * @param srcPts The array containing the source point coordinates.
	 *            Each point is stored as a pair of x,y coordinates.
	 * @param dstPts The array where the transformed point coordinates are
	 *            returned. Each point is stored as a pair of x,y coordinates.
	 * @param srcOff The offset to the first point to be transformed
	 *            in the source array.
	 * @param dstOff The offset to the location where the first transformed
	 *            point is stored in the destination array.
	 * @param numPts The number of point objects to be transformed.
	 * @throws IllegalArgumentException if srcPts is null
	 * @throws ArrayIndexOutOfBoundsException if srcPts is too small
	 */
	@Override
	public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
	{

		if (srcPts == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (dstPts == null)
			dstPts = new float[numPts * 2 + dstOff];

		while (numPts-- > 0)
		{
			double x = srcPts[srcOff++];
			double y = srcPts[srcOff++];
			double w = this.m20 * x + this.m21 * y + this.m22;

			if (w == 0)
			{
				dstPts[dstOff++] = (float) x;
				dstPts[dstOff++] = (float) y;
			}
			else
			{
				dstPts[dstOff++] = (float) ((this.m00 * x + this.m01 * y + this.m02) / w);
				dstPts[dstOff++] = (float) ((this.m10 * x + this.m11 * y + this.m12) / w);
			}
		}
	}

	/**
	 * Transforms an array of floating point coordinates by this transform,
	 * storing the results into an array of doubles.
	 *
	 * @param srcPts The array containing the source point coordinates.
	 *            Each point is stored as a pair of x,y coordinates.
	 * @param srcOff The offset to the first point to be transformed
	 *            in the source array.
	 * @param dstPts The array where the transformed point coordinates are
	 *            returned. Each point is stored as a pair of x,y coordinates.
	 * @param dstOff The offset to the location where the first transformed
	 *            point is stored in the destination array.
	 * @param numPts The number of points to be transformed.
	 * @throws IllegalArgumentException if srcPts is null
	 * @throws ArrayIndexOutOfBoundsException if srcPts is too small
	 */
	@Override
	public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
	{

		if (srcPts == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (dstPts == null)
			dstPts = new double[numPts * 2 + dstOff];

		while (numPts-- > 0)
		{
			float x = srcPts[srcOff++];
			float y = srcPts[srcOff++];
			double w = this.m20 * x + this.m21 * y + this.m22;

			if (w == 0)
			{
				dstPts[dstOff++] = x;
				dstPts[dstOff++] = y;
			}
			else
			{
				dstPts[dstOff++] = (this.m00 * x + this.m01 * y + this.m02) / w;
				dstPts[dstOff++] = (this.m10 * x + this.m11 * y + this.m12) / w;
			}
		}
	}

	/**
	 * Transforms an array of floating point coordinates by this transform.
	 *
	 * @param srcPts The array containing the source point coordinates.
	 *            Each point is stored as a pair of x,y coordinates.
	 * @param srcOff The offset to the first point to be transformed
	 *            in the source array.
	 * @param dstPts The array where the transformed point coordinates are
	 *            returned. Each point is stored as a pair of x,y coordinates.
	 * @param dstOff The offset to the location where the first transformed
	 *            point is stored in the destination array.
	 * @param numPts The number of points to be transformed.
	 * @throws IllegalArgumentException if srcPts is null
	 * @throws ArrayIndexOutOfBoundsException if srcPts is too small
	 */
	@Override
	public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
	{

		if (srcPts == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		if (dstPts == null)
			dstPts = new float[numPts * 2 + dstOff];

		while (numPts-- > 0)
		{
			float x = srcPts[srcOff++];
			float y = srcPts[srcOff++];
			double w = this.m20 * x + this.m21 * y + this.m22;

			if (w == 0)
			{
				dstPts[dstOff++] = x;
				dstPts[dstOff++] = y;
			}
			else
			{
				dstPts[dstOff++] = (float) ((this.m00 * x + this.m01 * y + this.m02) / w);
				dstPts[dstOff++] = (float) ((this.m10 * x + this.m11 * y + this.m12) / w);
			}
		}
	}

	/**
	 * Transforms the specified ptSrc and stores the result in ptDst.
	 * If ptDst is null, a new Point2D object will be allocated before
	 * storing. In either case, ptDst containing the transformed point
	 * is returned for convenience.
	 * Note that ptSrc and ptDst can the same. In this case, the input
	 * point will be overwritten with the transformed point.
	 *
	 * @param ptSrc The array containing the source point objects.
	 * @param ptDst The array where the transform point objects are returned.
	 * @throws IllegalArgumentException if ptSrc is null
	 */
	@Override
	public Point2D transform(Point2D ptSrc, Point2D ptDst)
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
		double w = this.m20 * x + this.m21 * y + this.m22;
		ptDst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w,
				(this.m10 * x + this.m11 * y + this.m12) / w);

		return ptDst;
	}

	/**
	 * Transforms an array of point objects by this transform.
	 *
	 * @param ptSrc The array containing the source point objects.
	 * @param ptDst The array where the transform point objects are returned.
	 * @param srcOff The offset to the first point object to be transformed
	 *            in the source array.
	 * @param dstOff The offset to the location where the first transformed
	 *            point object is stored in the destination array.
	 * @param numPts The number of point objects to be transformed.
	 * @throws IllegalArgumentException if ptSrc is null
	 * @throws IllegalArgumentException if ptDst is null
	 * @throws ArrayIndexOutOfBoundsException if ptSrc is too small
	 */
	@Override
	public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts)
	{

		if (ptSrc == null || ptDst == null)
			throw new IllegalArgumentException("The input source cannot be null.");

		while (numPts-- > 0)
		{
			/* Copy source coords into local variables in case src == dst. */
			Point2D src = ptSrc[srcOff++];
			Point2D dst = ptDst[dstOff++];
			if (dst == null)
			{
				if (src instanceof Point2D.Double)
					dst = new Point2D.Double();
				else
					dst = new Point2D.Float();
				ptDst[dstOff - 1] = dst;
			}

			double x = src.getX();
			double y = src.getY();
			double w = this.m20 * x + this.m21 * y + this.m22;

			if (w == 0)
				dst.setLocation(x, y);
			else
				dst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w,
						(this.m10 * x + this.m11 * y + this.m12) / w);
		}
	}

	/**
	 * Concatenates this transform with a translation transformation.
	 * This is equivalent to calling concatenate(T), where T is an
	 * PerspectiveTransform represented by the following matrix:
	 *
	 * <pre>
	 *		[   1    0    tx  ]
	 *		[   0    1    ty  ]
	 *		[   0    0    1   ]
	 * </pre>
	 */
	@Override
	public void translate(double tx, double ty)
	{
		PerspectiveTransform Tx = new PerspectiveTransform();
		Tx.setToTranslation(tx, ty);
		this.concatenate(Tx);
	}
}

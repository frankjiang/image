/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Test.java is PROPRIETARY/CONFIDENTIAL built in 4:47:56 PM, Jul 1, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.util.FastMath;

import com.github.frankjiang.image4j.ImagingLib;
import com.github.frankjiang.image4j.color.ColorConvertUtils;

/**
 * The test class.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Test
{
	public static void f()
	{
		BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, 256, 256);
		g.setColor(Color.RED);
		g.fillRect(64, 64, 128, 128);

		ImagingLib.display("source", image);

		RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		AffineTransform xform = AffineTransform.getScaleInstance(0.5, 0.5);
		AffineTransformOp op = new AffineTransformOp(xform, hints);
		image = op.filter(image, null);

		ImagingLib.display("result", image);
	}

	public static double getSquared(double dr, double dg, double db)
	{
		return Math.sqrt(dr * dr + dg * dg + db * db);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Clusterer<DoublePoint> clusterer = new DBSCANClusterer<>(getSquared(32, 32, 32), 20);
		BufferedImage image = ImageIO
				.read(new File("/Users/frank/Desktop/quick_test.png"));
		image = ColorConvertUtils.getGrayImage(image);
		WritableRaster raster = image.getRaster();
		ColorModel model = image.getColorModel();
		int a, r, g, b;
		Collection<DoublePoint> points = new ArrayList<>();
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++)
			{
				Object inData = raster.getDataElements(x, y, null);
				a = model.getAlpha(raster.getDataElements(x, y, null));
				r = model.getRed(inData);
				g = model.getGreen(inData);
				b = model.getBlue(inData);
				System.out.println(Arrays.toString(new double[] { a, r, g, b }));
				if (a > 0)
					points.add(new DoublePoint(new double[] { r, g, b }));
			}
		List<? extends Cluster<DoublePoint>> clusters = clusterer.cluster(points);
		for (Cluster<DoublePoint> cluster : clusters)
		{
			double[] center;
			if (cluster instanceof CentroidCluster)
				center = ((CentroidCluster<DoublePoint>) cluster).getCenter().getPoint();
			else
			{
				center = new double[3];
				double[] tmp = new double[3];
				List<DoublePoint> list = cluster.getPoints();
				for (DoublePoint pt : list)
				{
					System.arraycopy(pt.getPoint(), 0, tmp, 0, 3);
					for (int i = 0; i < 3; i++)
						center[i] += tmp[i];
				}
				for (int i = 0; i < 3; i++)
					center[i] /= list.size();
			}
			Color color = new Color((int) FastMath.round(center[0]),
					(int) FastMath.round(center[1]), (int) FastMath.round(center[2]));
			System.out.println(color);
		}
	}

}

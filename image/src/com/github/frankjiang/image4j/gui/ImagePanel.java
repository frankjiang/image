/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ImagePanel.java is PROPRIETARY/CONFIDENTIAL built in 4:39:20 PM, Jul 1, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

/**
 * The image display panel.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ImagePanel extends JPanel implements ComponentListener
{
	/**
	 * serialVersionUID.
	 */
	private static final long	serialVersionUID	= 4855011657134254420L;

	protected Image				image;

	private int					gap;

	private int					imageWidth;
	private int					imageHeight;

	public ImagePanel(Image image)
	{
		this.image = image;
		this.imageWidth = image.getWidth(this);
		this.imageHeight = image.getHeight(this);
		this.gap = 5;
		this.setPreferredSize(
				new Dimension(this.imageWidth + 2 * this.gap, this.imageHeight + 2 * this.gap));
	}

	/**
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e)
	{

	}

	/**
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent e)
	{

	}

	/**
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e)
	{
		this.repaint();
	}

	/**
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e)
	{

	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		Image display = null;
		int width = this.getWidth() - this.gap * 2;
		int height = this.getHeight() - this.gap * 2;
		if (this.imageWidth < width || this.imageHeight < height)
		{
			float fscale = Math.min(width, height) / Math.min(this.imageWidth, this.imageHeight);
			if (fscale < Float.MIN_VALUE)
				display = this.image;
			else
				display = this.image.getScaledInstance(Math.round(fscale * this.imageWidth),
						Math.round(fscale * this.imageHeight), Image.SCALE_DEFAULT);
		}
		else
			display = this.image;
		g.drawImage(display, this.gap, this.gap, this);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(this.gap, this.gap, width, height);
	}
}

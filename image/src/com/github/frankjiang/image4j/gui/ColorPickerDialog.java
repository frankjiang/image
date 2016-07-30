/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * ColorPickerDialog.java is PROPRIETARY/CONFIDENTIAL built in 10:13:35 AM, Jul
 * 6, 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.util.FastMath;

/**
 * The color picker dialog.
 * <p>
 * </p>
 *
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class ColorPickerDialog extends JDialog
{

	private class Canvas extends JPanel
	{
		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = -8535323381699101420L;

		public Canvas()
		{

		}

		@Override
		public void paint(Graphics g)
		{
			g.setColor(ColorPickerDialog.this.getColor());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6110985443935278093L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			ColorPickerDialog dialog = new ColorPickerDialog();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private final JPanel	contentPanel	= new JPanel();
	private JSlider			sliderBr;
	private JSlider			sliderH;
	private JLabel			lblH;
	private JSlider			sliderS;
	private JLabel			lblBr;
	private JLabel			lblS;
	private JLabel			lblB;
	private JSlider			sliderB;
	private JLabel			lblG;
	private JSlider			sliderG;
	private JLabel			lblR;

	private JSlider			sliderR;

	private JPanel			canvas;

	protected Color			color			= Color.RED;

	/**
	 * Create the dialog.
	 */
	public ColorPickerDialog()
	{
		this.setTitle("Color Picker");
		this.setBounds(100, 100, 450, 300);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			this.contentPanel.add(panel, BorderLayout.WEST);
			panel.setLayout(new GridLayout(2, 0, 0, 0));
			{
				JPanel panelRGB = new JPanel();
				panelRGB.setBorder(new TitledBorder(null, "RGB", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				panel.add(panelRGB);
				GridBagLayout gbl_panelRGB = new GridBagLayout();
				gbl_panelRGB.columnWidths = new int[] { 0, 0, 0, 0 };
				gbl_panelRGB.rowHeights = new int[] { 0, 0, 0, 0 };
				gbl_panelRGB.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
				gbl_panelRGB.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
				panelRGB.setLayout(gbl_panelRGB);
				{
					JLabel lblRed = new JLabel("R:");
					lblRed.setToolTipText("Red");
					GridBagConstraints gbc_lblRed = new GridBagConstraints();
					gbc_lblRed.insets = new Insets(0, 0, 5, 5);
					gbc_lblRed.gridx = 0;
					gbc_lblRed.gridy = 0;
					panelRGB.add(lblRed, gbc_lblRed);
				}
				{
					this.sliderR = new JSlider();
					this.sliderR.setMaximum(255);
					this.sliderR.setValue(255);
					this.sliderR.addChangeListener(e -> {
						int r = ColorPickerDialog.this.sliderR.getValue();
						ColorPickerDialog.this.lblR.setText(Integer.toString(r));
						Color c = ColorPickerDialog.this.getColor();
						int g = c.getGreen(), b = c.getBlue();
						ColorPickerDialog.this.setColor(new Color(r, g, b));
						float[] hsb = Color.RGBtoHSB(r, g, b, null);
						ColorPickerDialog.this.setHSB(FastMath.round(hsb[0] * 255),
								FastMath.round(hsb[1] * 255), FastMath.round(hsb[2] * 255));
					});
					GridBagConstraints gbc_sliderR = new GridBagConstraints();
					gbc_sliderR.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderR.insets = new Insets(0, 0, 5, 5);
					gbc_sliderR.gridx = 1;
					gbc_sliderR.gridy = 0;
					panelRGB.add(this.sliderR, gbc_sliderR);
				}
				{
					this.lblR = new JLabel("255");
					GridBagConstraints gbc_lblR = new GridBagConstraints();
					gbc_lblR.insets = new Insets(0, 0, 5, 0);
					gbc_lblR.gridx = 2;
					gbc_lblR.gridy = 0;
					panelRGB.add(this.lblR, gbc_lblR);
				}
				{
					JLabel lblGreen = new JLabel("G:");
					lblGreen.setToolTipText("Green");
					GridBagConstraints gbc_lblGreen = new GridBagConstraints();
					gbc_lblGreen.insets = new Insets(0, 0, 5, 5);
					gbc_lblGreen.gridx = 0;
					gbc_lblGreen.gridy = 1;
					panelRGB.add(lblGreen, gbc_lblGreen);
				}
				{
					this.sliderG = new JSlider();
					this.sliderG.setMaximum(255);
					this.sliderG.setValue(0);
					this.sliderG.addChangeListener(e -> {
						int g = ColorPickerDialog.this.sliderG.getValue();
						ColorPickerDialog.this.lblG.setText(Integer.toString(g));
						Color c = ColorPickerDialog.this.getColor();
						int r = c.getRed(), b = c.getBlue();
						ColorPickerDialog.this.setColor(new Color(r, g, b));
						float[] hsb = Color.RGBtoHSB(r, g, b, null);
						ColorPickerDialog.this.setHSB(FastMath.round(hsb[0] * 255),
								FastMath.round(hsb[1] * 255), FastMath.round(hsb[2] * 255));
					});

					GridBagConstraints gbc_sliderG = new GridBagConstraints();
					gbc_sliderG.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderG.insets = new Insets(0, 0, 5, 5);
					gbc_sliderG.gridx = 1;
					gbc_sliderG.gridy = 1;
					panelRGB.add(this.sliderG, gbc_sliderG);
				}
				{
					this.lblG = new JLabel("0");
					GridBagConstraints gbc_lblG = new GridBagConstraints();
					gbc_lblG.insets = new Insets(0, 0, 5, 0);
					gbc_lblG.gridx = 2;
					gbc_lblG.gridy = 1;
					panelRGB.add(this.lblG, gbc_lblG);
				}
				{
					JLabel lblBlue = new JLabel("B:");
					lblBlue.setToolTipText("Blue");
					GridBagConstraints gbc_lblBlue = new GridBagConstraints();
					gbc_lblBlue.insets = new Insets(0, 0, 0, 5);
					gbc_lblBlue.gridx = 0;
					gbc_lblBlue.gridy = 2;
					panelRGB.add(lblBlue, gbc_lblBlue);
				}
				{
					this.sliderB = new JSlider();
					this.sliderB.setMaximum(255);
					this.sliderB.setValue(0);
					this.sliderB.addChangeListener(e -> {
						int b = ColorPickerDialog.this.sliderB.getValue();
						ColorPickerDialog.this.lblB.setText(Integer.toString(b));
						Color c = ColorPickerDialog.this.getColor();
						int r = c.getRed(), g = c.getGreen();
						ColorPickerDialog.this.setColor(new Color(r, g, b));
						float[] hsb = Color.RGBtoHSB(r, g, b, null);
						ColorPickerDialog.this.setHSB(FastMath.round(hsb[0] * 255),
								FastMath.round(hsb[1] * 255), FastMath.round(hsb[2] * 255));
					});

					GridBagConstraints gbc_sliderB = new GridBagConstraints();
					gbc_sliderB.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderB.insets = new Insets(0, 0, 0, 5);
					gbc_sliderB.gridx = 1;
					gbc_sliderB.gridy = 2;
					panelRGB.add(this.sliderB, gbc_sliderB);
				}
				{
					this.lblB = new JLabel("0");
					GridBagConstraints gbc_lblB = new GridBagConstraints();
					gbc_lblB.gridx = 2;
					gbc_lblB.gridy = 2;
					panelRGB.add(this.lblB, gbc_lblB);
				}
			}
			{
				JPanel panelHSB = new JPanel();
				panelHSB.setBorder(new TitledBorder(null, "HSB", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				panel.add(panelHSB);
				GridBagLayout gbl_panelHSB = new GridBagLayout();
				gbl_panelHSB.columnWidths = new int[] { 0, 0, 0, 0 };
				gbl_panelHSB.rowHeights = new int[] { 0, 0, 0, 0 };
				gbl_panelHSB.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
				gbl_panelHSB.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
				panelHSB.setLayout(gbl_panelHSB);
				{
					JLabel lblHue = new JLabel("H:");
					lblHue.setToolTipText("Hue");
					GridBagConstraints gbc_lblHue = new GridBagConstraints();
					gbc_lblHue.insets = new Insets(0, 0, 5, 5);
					gbc_lblHue.gridx = 0;
					gbc_lblHue.gridy = 0;
					panelHSB.add(lblHue, gbc_lblHue);
				}
				{
					this.sliderH = new JSlider();
					this.sliderH.setMaximum(255);
					this.sliderH.setValue(0);

					this.sliderH.addChangeListener(e -> {
						ColorPickerDialog.this.lblH.setText(
								Integer.toString(ColorPickerDialog.this.sliderH.getValue()));
						int rgb = Color.HSBtoRGB(ColorPickerDialog.this.sliderH.getValue() / 255f,
								ColorPickerDialog.this.sliderS.getValue() / 255f,
								ColorPickerDialog.this.sliderBr.getValue() / 255f);
						ColorPickerDialog.this.setRGB(rgb >> 16 & 0xff, rgb >> 8 & 0xff,
								rgb & 0xff);
					});
					GridBagConstraints gbc_sliderH = new GridBagConstraints();
					gbc_sliderH.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderH.insets = new Insets(0, 0, 5, 5);
					gbc_sliderH.gridx = 1;
					gbc_sliderH.gridy = 0;
					panelHSB.add(this.sliderH, gbc_sliderH);
				}
				{
					this.lblH = new JLabel("0");
					GridBagConstraints gbc_lblH = new GridBagConstraints();
					gbc_lblH.insets = new Insets(0, 0, 5, 0);
					gbc_lblH.gridx = 2;
					gbc_lblH.gridy = 0;
					panelHSB.add(this.lblH, gbc_lblH);
				}
				{
					JLabel lblSaturation = new JLabel("S:");
					lblSaturation.setToolTipText("Saturation");
					GridBagConstraints gbc_lblSaturation = new GridBagConstraints();
					gbc_lblSaturation.insets = new Insets(0, 0, 5, 5);
					gbc_lblSaturation.gridx = 0;
					gbc_lblSaturation.gridy = 1;
					panelHSB.add(lblSaturation, gbc_lblSaturation);
				}
				{
					this.sliderS = new JSlider();
					this.sliderS.setMaximum(255);
					this.sliderS.setValue(255);
					this.sliderS.addChangeListener(e -> {
						ColorPickerDialog.this.lblS.setText(
								Integer.toString(ColorPickerDialog.this.sliderS.getValue()));
						int rgb = Color.HSBtoRGB(ColorPickerDialog.this.sliderH.getValue() / 255f,
								ColorPickerDialog.this.sliderS.getValue() / 255f,
								ColorPickerDialog.this.sliderBr.getValue() / 255f);
						ColorPickerDialog.this.setRGB(rgb >> 16 & 0xff, rgb >> 8 & 0xff,
								rgb & 0xff);
					});
					GridBagConstraints gbc_sliderS = new GridBagConstraints();
					gbc_sliderS.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderS.insets = new Insets(0, 0, 5, 5);
					gbc_sliderS.gridx = 1;
					gbc_sliderS.gridy = 1;
					panelHSB.add(this.sliderS, gbc_sliderS);
				}
				{
					this.lblS = new JLabel("255");
					GridBagConstraints gbc_lblS = new GridBagConstraints();
					gbc_lblS.insets = new Insets(0, 0, 5, 0);
					gbc_lblS.gridx = 2;
					gbc_lblS.gridy = 1;
					panelHSB.add(this.lblS, gbc_lblS);
				}
				{
					JLabel lblBrightness = new JLabel("B:");
					lblBrightness.setToolTipText("Brightness");
					GridBagConstraints gbc_lblBrightness = new GridBagConstraints();
					gbc_lblBrightness.insets = new Insets(0, 0, 0, 5);
					gbc_lblBrightness.gridx = 0;
					gbc_lblBrightness.gridy = 2;
					panelHSB.add(lblBrightness, gbc_lblBrightness);
				}
				{
					this.sliderBr = new JSlider();
					this.sliderBr.setMaximum(255);
					this.sliderBr.setValue(255);
					this.sliderBr.addChangeListener(e -> {
						ColorPickerDialog.this.lblBr.setText(
								Integer.toString(ColorPickerDialog.this.sliderBr.getValue()));
						int rgb = Color.HSBtoRGB(ColorPickerDialog.this.sliderH.getValue() / 255f,
								ColorPickerDialog.this.sliderS.getValue() / 255f,
								ColorPickerDialog.this.sliderBr.getValue() / 255f);
						ColorPickerDialog.this.setRGB(rgb >> 16 & 0xff, rgb >> 8 & 0xff,
								rgb & 0xff);
					});
					GridBagConstraints gbc_sliderBr = new GridBagConstraints();
					gbc_sliderBr.insets = new Insets(0, 0, 0, 5);
					gbc_sliderBr.fill = GridBagConstraints.HORIZONTAL;
					gbc_sliderBr.gridx = 1;
					gbc_sliderBr.gridy = 2;
					panelHSB.add(this.sliderBr, gbc_sliderBr);
				}
				{
					this.lblBr = new JLabel("255");
					GridBagConstraints gbc_lblBr = new GridBagConstraints();
					gbc_lblBr.gridx = 2;
					gbc_lblBr.gridy = 2;
					panelHSB.add(this.lblBr, gbc_lblBr);
				}
			}
		}
		{
			this.canvas = new Canvas();
			this.contentPanel.add(this.canvas, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(e -> ColorPickerDialog.this.dispose());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				this.getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(e -> {
					ColorPickerDialog.this.dispose();
					ColorPickerDialog.this.setColor(null);
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public synchronized Color getColor()
	{
		return this.color;
	}

	protected synchronized void setColor(Color color)
	{
		this.color = color;
		this.canvas.repaint();
	}

	private void setHSB(int h, int s, int b)
	{
		if (this.sliderH.getValue() != h)
		{
			this.sliderH.setValue(h);
			this.lblH.setText(Integer.toString(h));
		}
		if (this.sliderS.getValue() != s)
		{
			this.sliderS.setValue(s);
			this.lblS.setText(Integer.toString(s));
		}
		if (this.sliderBr.getValue() != b)
		{
			this.sliderBr.setValue(b);
			this.lblBr.setText(Integer.toString(b));
		}
	}

	private void setRGB(int r, int g, int b)
	{
		this.setColor(new Color(r, g, b));
		if (this.sliderR.getValue() != r)
		{
			this.sliderR.setValue(r);
			this.lblR.setText(Integer.toString(r));
		}
		if (this.sliderG.getValue() != g)
		{
			this.sliderG.setValue(g);
			this.lblG.setText(Integer.toString(g));
		}
		if (this.sliderB.getValue() != b)
		{
			this.sliderB.setValue(b);
			this.lblB.setText(Integer.toString(b));
		}
	}
}

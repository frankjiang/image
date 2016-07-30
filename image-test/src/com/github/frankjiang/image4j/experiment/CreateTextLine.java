/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CreateTextLine.java is PROPRIETARY/CONFIDENTIAL built in 6:29:16 PM, Jul 11,
 * 2016.
 * Use is subject to license terms.
 */

package com.github.frankjiang.image4j.experiment;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

/**
 * TODO
 * <p>
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CreateTextLine
{
	public static final String[]	NAMES	= { "15497206", "3205002184,1", "test" };

	static Map<Integer, Group>		groups	= new TreeMap<>();

	static class Text
	{
		public int			id;
		public Rectangle	rect;
		public char			ch;
		public double		conf;
		public int			code;

		public Text(String str)
		{
			StringTokenizer st = new StringTokenizer(str, " \t");
			int group = Integer.valueOf(st.nextToken());
			id = Integer.valueOf(st.nextToken());
			conf = Double.valueOf(st.nextToken());
			rect = new Rectangle();
			rect.x = Integer.valueOf(st.nextToken());
			rect.y = Integer.valueOf(st.nextToken());
			rect.width = Integer.valueOf(st.nextToken());
			rect.height = Integer.valueOf(st.nextToken());
			code = Integer.valueOf(st.nextToken());
			ch = st.nextToken().charAt(0);

			groups.get(group).texts.add(this);
		}
	}

	static class Group implements Comparable<Group>
	{
		public int				id;
		public Rectangle		rect;
		public ArrayList<Text>	texts;

		public Group(String str)
		{
			StringTokenizer st = new StringTokenizer(str, " \t");
			id = Integer.valueOf(st.nextToken());
			rect = new Rectangle();
			rect.x = Integer.valueOf(st.nextToken());
			rect.y = Integer.valueOf(st.nextToken());
			rect.width = Integer.valueOf(st.nextToken());
			rect.height = Integer.valueOf(st.nextToken());
			texts = new ArrayList<>();
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Group o)
		{
			return id - o.id;
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String name = NAMES[1];
		File fileGroup = new File("test_out", String.format("group_%s.txt", name));
		File fileChar = new File("test_out", String.format("char_%s.txt", name));

		String strGroup = FileUtils.readFileToString(fileGroup);
		StringTokenizer stGroup = new StringTokenizer(strGroup, "\r\n\f");
		while (stGroup.hasMoreTokens())
		{
			Group group = new Group(stGroup.nextToken());
			groups.put(group.id, group);
		}

		String strChars = FileUtils.readFileToString(fileChar);
		StringTokenizer stChars = new StringTokenizer(strChars, "\r\n\f");
		while (stChars.hasMoreTokens())
			new Text(stChars.nextToken());

		for (Group group : groups.values())
			check(group);
	}

	/**
	 * @param group
	 */
	private static void check(Group group)
	{
		Comparator<Text> comp = (Text t1, Text t2)->
		{
			return t1.rect.x - t2.rect.x;
		};
		Collections.sort(group.texts, comp);
		for(Text text:group.texts)
			System.out.print(text.ch);
		System.out.println();
	}

}

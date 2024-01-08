/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.desktop.controller;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// picked up from: http://stackoverflow.com/questions/4577424/distinguish-between-a-single-click-and-a-double-click-in-java

public class ClickListener extends MouseAdapter implements ActionListener
{
//    private final static int clickInterval = (Integer)Toolkit.getDefaultToolkit().
//        getDesktopProperty("awt.multiClickInterval");
    private final static int clickInterval;

	static
	{
		int interval2 = 500;
		if( Toolkit.getDefaultToolkit() != null )
		{
			Integer interval =  (Integer)Toolkit.getDefaultToolkit().
        getDesktopProperty("awt.multiClickInterval");

			if( interval != null )
				interval2 = interval;
		}

		clickInterval = interval2;
	}
	
    protected MouseEvent lastLeftClickEvent;
    protected Timer timer;

    public ClickListener()
    {
        this(clickInterval);
    }

    public ClickListener(int delay)
    {
        timer = new Timer( delay, this);
    }

	@Override
	public void mouseClicked (MouseEvent e)
	{
//		System.out.println( "ClickListener.mouseClicked" );
		if (e.getClickCount() > 2) return;

		if( SwingUtilities.isLeftMouseButton(e) )
		{
//			System.out.println( "ClickListener.mouseClicked left" );
			lastLeftClickEvent = e;

			if (timer.isRunning())
			{
				timer.stop();
				doubleClick(lastLeftClickEvent );
			}
			else
			{
				timer.restart();
			}
		}
		else if( SwingUtilities.isRightMouseButton(e) )
		{
//			System.out.println( "ClickListener.mouseClicked right" );
			rightClick( e );
		}
	}

    public void actionPerformed(ActionEvent e)
	{
		timer.stop();
		singleClick(lastLeftClickEvent );
	}

	public void singleClick(MouseEvent e) {}
	public void doubleClick(MouseEvent e) {}
	public void rightClick(MouseEvent e) {}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame( "Double Click Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.addMouseListener( new ClickListener()
		{
			public void singleClick(MouseEvent e)
			{
				System.out.println("single");
			}

			public void doubleClick(MouseEvent e)
			{
				System.out.println("double");
			}
		});
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
}
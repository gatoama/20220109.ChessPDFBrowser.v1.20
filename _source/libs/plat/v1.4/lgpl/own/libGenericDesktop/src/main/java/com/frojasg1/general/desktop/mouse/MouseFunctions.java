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
package com.frojasg1.general.desktop.mouse;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class MouseFunctions
{

	protected static MouseEvent _lastMainMouseButtonEvent;
	protected static long _lastTimestampOfMouseEvent = 0;

	// function got from:
	// http://stackoverflow.com/questions/2941324/how-do-i-set-the-position-of-the-mouse-in-java
	public static void moveMouse(Point p)
	{
		GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Search the devices for the one that draws the specified point.
		for (GraphicsDevice device: gs)
		{ 
			GraphicsConfiguration[] configurations =
				device.getConfigurations();
			for (GraphicsConfiguration config: configurations)
			{
				Rectangle bounds = config.getBounds();
				if(bounds.contains(p))
				{
					// Set point to screen coordinates.
					Point b = bounds.getLocation(); 
					Point s = new Point(p.x - b.x, p.y - b.y);

					try
					{
						Robot r = new Robot(device);
						r.mouseMove(s.x, s.y);
					}
					catch (AWTException e)
					{
						e.printStackTrace();
					}

					return;
				}
			}
		}
		// Couldn't move to the point, it may be off screen.
		return;
	}

	public static Point getMouseLocation()
	{
		return( MouseInfo.getPointerInfo().getLocation() );
	}

	public static void storeLastMouseEvent( MouseEvent event )
	{
		if( ( event != null ) && ( event.getButton() == MouseEvent.BUTTON1 ) )
		{
			_lastMainMouseButtonEvent = event;
			_lastTimestampOfMouseEvent = System.currentTimeMillis();
		}
	}

	protected static boolean isUpdated()
	{
		return( _lastTimestampOfMouseEvent >= ( System.currentTimeMillis() - 200 ) );
	}

	public static boolean isMainButtonPressed()
	{
		return( isMainButtonPressed( _lastMainMouseButtonEvent ) );
	}

	public static boolean isMainButtonPressed(MouseEvent me)
	{
		boolean result = false;
		
//		if( isUpdated() )
		result = SwingUtilities.isLeftMouseButton( me );

		return( result );
	}

	public static boolean isMouseInsideComponent( Component comp, MouseEvent evt )
	{
		boolean result = false;
		if( comp != null )
		{
			Point point = comp.getLocationOnScreen();

			int dx = evt.getXOnScreen() - point.x;
			int dy = evt.getYOnScreen() - point.y;

			if( ( dx >= 0 ) && ( dy >= 0 ) )
			{
				Dimension size = comp.getSize();
				if( ( dx < size.width ) && ( dy < size.height ) )
					result = true;
			}
		}

		return( result );
	}

	public static void triggerMouseMoveEvent( JComponent component )
	{
		Point mousePosition = component.getMousePosition();
		if (component.isShowing() && mousePosition != null) {
			component.dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_MOVED, 0, 0, mousePosition.x,
					mousePosition.y, 0, false));
		}
	}
}

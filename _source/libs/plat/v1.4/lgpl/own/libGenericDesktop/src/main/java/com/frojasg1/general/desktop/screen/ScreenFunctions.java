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
package com.frojasg1.general.desktop.screen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 *
 * @author Usuario
 */
public class ScreenFunctions
{
	public static Dimension getScreenSize( Component comp, boolean withoutTaskBar )
	{
		//size of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if( withoutTaskBar && ( comp != null ) )
		{
			//height of the task bar
			Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets( comp.getGraphicsConfiguration() );
			int taskBarSize = scnMax.bottom;

			screenSize = new Dimension( (int) screenSize.getWidth(), (int) screenSize.getHeight() - taskBarSize );
		}

		return( screenSize );
	}

	public static Rectangle getBoundsOfMaxWindow()
	{
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		return( winSize );
	}

	public static boolean isInsideComponent( Component comp, Point locationOnScreenToCheck )
	{
		boolean result = false;
		try
		{
			Point leftTopCorner = comp.getLocationOnScreen();
			Dimension dim = comp.getSize();

			result = ( (locationOnScreenToCheck.getX() >= leftTopCorner.getX()) &&
						(locationOnScreenToCheck.getY() >= leftTopCorner.getY()) &&
						(locationOnScreenToCheck.getX() < (leftTopCorner.getX() + dim.getWidth() ) ) &&
						(locationOnScreenToCheck.getY() < (leftTopCorner.getY() + dim.getHeight() ) ) );
		}
		catch( Throwable th )
		{
			result = false;
		}

		return( result );
	}

}

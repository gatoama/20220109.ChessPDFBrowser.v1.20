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
package com.frojasg1.general.desktop.view.layers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JLayerUtils
{
	public static String toString( MouseWheelEvent mwe )
	{
		String result = "null";

		if( mwe != null )
		{
			result = String.format( "[%s] - when = %d, modifiers = %d, " +
									"id = %d, location.on.component = ( %d, %d ), " +
									"location.on.screen = ( %d, %d ), clickCount = %d, " +
									"scrollType = %d, scrollAmount = %d, wheelRotation = %d, " +
									"preciseWheelRotation = %f0.4",
									mwe,
									mwe.getWhen(),
									mwe.getModifiers(),
									mwe.getID(),
									mwe.getX(),
									mwe.getY(),
									mwe.getXOnScreen(),
									mwe.getYOnScreen(),
									mwe.getClickCount(),
									mwe.getScrollType(),
									mwe.getScrollAmount(),
									mwe.getWheelRotation(),
									mwe.getPreciseWheelRotation() );
		}

		return( result );
	}

	public static String toString( MouseEvent me )
	{
		String result = "null";
		if( me instanceof MouseWheelEvent )
		{
			MouseWheelEvent mwe = (MouseWheelEvent) me;
			result = toString( mwe );
		}
		else if( me != null )
		{
			result = String.format( "[%s] - when = %d, modifiers = %d, " +
									"id = %d, location.on.component = ( %d, %d ), " +
									"location.on.screen = ( %d, %d ), clickCount = %d, " +
									"button = %d",
									me,
									me.getWhen(),
									me.getModifiers(),
									me.getID(),
									me.getX(),
									me.getY(),
									me.getXOnScreen(),
									me.getYOnScreen(),
									me.getClickCount(),
									me.getButton() );
		}

		return( result );
	}
}

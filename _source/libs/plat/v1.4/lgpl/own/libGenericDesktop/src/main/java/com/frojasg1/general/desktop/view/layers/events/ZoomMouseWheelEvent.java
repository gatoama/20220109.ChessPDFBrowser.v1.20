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
package com.frojasg1.general.desktop.view.layers.events;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
@Deprecated
public class ZoomMouseWheelEvent extends MouseWheelEvent implements ZoomEvent
{
	public ZoomMouseWheelEvent (Component source, int id, long when, int modifiers,
					int x, int y, int clickCount, boolean popupTrigger,
					int scrollType, int scrollAmount, int wheelRotation)
	{
		super(source, id, when, modifiers, x, y, 0, 0, clickCount,
			popupTrigger, scrollType, scrollAmount, wheelRotation);
	}

	public ZoomMouseWheelEvent (Component source, int id, long when, int modifiers,
							int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger,
							int scrollType, int scrollAmount, int wheelRotation) {

		super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger,
				scrollType, scrollAmount, wheelRotation, wheelRotation);
	}

	public ZoomMouseWheelEvent (Component source, int id, long when, int modifiers,
								int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger,
								int scrollType, int scrollAmount, int wheelRotation, double preciseWheelRotation)
	{
		super( source, id, when, modifiers,	x, y, xAbs, yAbs, clickCount, popupTrigger,
							scrollType, scrollAmount, wheelRotation, preciseWheelRotation);
	}
}

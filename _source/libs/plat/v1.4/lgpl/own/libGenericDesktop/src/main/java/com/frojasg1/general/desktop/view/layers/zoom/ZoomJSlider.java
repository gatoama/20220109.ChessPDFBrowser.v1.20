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
package com.frojasg1.general.desktop.view.layers.zoom;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import static javax.swing.SwingConstants.HORIZONTAL;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJSlider extends JSlider implements ZoomComponent
{
    public ZoomJSlider() {
        super(HORIZONTAL, 0, 100, 50);
    }

	public ZoomJSlider(int orientation) {
        super(orientation, 0, 100, 50);
    }

    public ZoomJSlider(int min, int max) {
        super(HORIZONTAL, min, max, (min + max) / 2);
    }

    public ZoomJSlider(int min, int max, int value) {
        super(HORIZONTAL, min, max, value);
    }

    public ZoomJSlider(int orientation, int min, int max, int value)
    {
        super(orientation, min, max, value);
    }

    public ZoomJSlider(BoundedRangeModel brm)
    {
		super( brm );
	}

	public void disableEvents_public( long eventsToDisable )
	{
		super.disableEvents( eventsToDisable );
	}

	public void enableEvents_public( long eventsToEnable )
	{
		super.disableEvents( eventsToEnable );
	}
}

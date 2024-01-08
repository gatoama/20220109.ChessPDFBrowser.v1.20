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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.ZoomFunctions;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentForZoomOverriden implements ChangeListener
{
	protected ZoomComponentInterface _component = null;
	protected DoubleReference _zoomFactor = null;

	public ComponentForZoomOverriden( ZoomComponentInterface component, DoubleReference zoomFactor )
	{
		_component = component;
		_zoomFactor = zoomFactor;
		
		if( _component instanceof JButton )
			( (JButton) _component ).addChangeListener(this);
	}

	public void setZoomFactor( double zoomFactor )
	{
		_zoomFactor._value = zoomFactor;
	}

	public DoubleReference getZoomFactor()
	{
		return( _zoomFactor );
	}

	public void switchToZoomUI()
	{
		ZoomFunctions.instance().switchToZoomUI(_component, _zoomFactor);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		( (Component)_component ).repaint();
	}
}

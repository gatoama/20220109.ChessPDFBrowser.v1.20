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
package com.frojasg1.applications.common.components.data;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InfoForResizingPanels
{
	protected int _widthDifference = -1;
	protected int _heightDifference = -1;
	protected Component _component = null;
		
	public InfoForResizingPanels( Component comp )
	{
		_component = comp;
		Component parent = comp.getParent();
		if( parent != null )
		{
			if( ( parent instanceof JFrame ) || ( parent instanceof JDialog ) )
			{
				_widthDifference = (int) ( parent.getPreferredSize().getWidth() - comp.getWidth() );
				_heightDifference = (int) ( parent.getPreferredSize().getHeight() - comp.getHeight() );
			}
			else
			{
				_widthDifference = parent.getWidth() - comp.getWidth();
				_heightDifference = parent.getHeight() - comp.getHeight();
			}
		}
	}

	public void resize()
	{
		Component parent = _component.getParent();
		if( parent != null )
		{
			_component.setSize( parent.getWidth() - _widthDifference, parent.getHeight() - _heightDifference);
		}
	}
}

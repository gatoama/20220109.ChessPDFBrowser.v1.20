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
package com.frojasg1.general.desktop.view.splitpane;

import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.ObjectFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Component;
import javax.swing.JSplitPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SplitPaneFunctions
{
	protected static SplitPaneFunctions _instance;

	public static void changeInstance( SplitPaneFunctions instance )
	{
		_instance = instance;
	}

	public static SplitPaneFunctions instance()
	{
		if( _instance == null )
			_instance = new SplitPaneFunctions();
		
		return( _instance );
	}

	public Component getDivider( JSplitPane splitPane )
	{
		Component result = null;
		if( splitPane != null )
			result = (Component) ReflectionFunctions.instance().invokeMethod("getDivider", splitPane.getUI());

		return( result );
	}

	public boolean isDivider( Component comp )
	{
		boolean result = false;
		if( comp != null )
		{
			result = getDivider( ClassFunctions.instance().cast( comp.getParent(), JSplitPane.class ) ) == comp;
		}
		return( result );
	}
}

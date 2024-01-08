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
package com.frojasg1.general.desktop.classes;

import com.frojasg1.general.ClassFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Classes {

	public static Class<?> getClassForName( String className )
	{
		return( ClassFunctions.instance().classForName(className) );
	}

	public static Class<?> getFilePaneClass()
	{
		return( getClassForName( "sun.swing.FilePane" ) );	// JRE 7, 8, ...
	}

	public static Class<?> getAppContextClass()
	{
		return( getClassForName( "sun.awt.AppContext" ) );	// JRE 7, 8, ...
	}

	public static Class<?> getMenuItemLayoutHelperClass()
	{
		return( getClassForName( "sun.swing.MenuItemLayoutHelper" ) );
	}

	public static Class<?> getSwingUtilities2Class()
	{
		return( getClassForName( "sun.swing.SwingUtilities2" ) );	// JRE 7, 8, ...
	}

	public static Class<?> getDefaultLookupClass()
	{
		return( getClassForName( "sun.swing.DefaultLookup" ) );	// JRE 7, 8, ...
	}

	public static Class<?> getSunDragSourceContextPeerClass()
	{
		return( getClassForName( "sun.awt.dnd.DefaultLookup" ) );	// JRE 7, 8, ...
	}
}

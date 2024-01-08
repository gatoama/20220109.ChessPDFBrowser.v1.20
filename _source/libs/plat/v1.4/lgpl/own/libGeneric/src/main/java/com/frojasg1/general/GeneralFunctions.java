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
package com.frojasg1.general;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GeneralFunctions
{
	protected static GeneralFunctions _instance;

	public static void changeInstance( GeneralFunctions inst )
	{
		_instance = inst;
	}

	public static GeneralFunctions instance()
	{
		if( _instance == null )
			_instance = new GeneralFunctions();
		return( _instance );
	}

	public boolean equals( Object obj1, Object obj2 )
	{
		boolean result = ( obj1 == obj2 );	// in case they are the same object, or they are both null.
		if( ! result && ( obj1 != null ) && (obj2 != null ) )
		{
			result = obj1.equals(obj2);
		}
		return( result );
	}

	public void beep()
	{
		System.out.print("\007");
		System.out.flush();
	}
}

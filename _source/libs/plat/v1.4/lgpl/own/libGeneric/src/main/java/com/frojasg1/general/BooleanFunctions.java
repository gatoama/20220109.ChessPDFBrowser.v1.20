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
 * @author fjavier.rojas
 */
public class BooleanFunctions
{
	protected static BooleanFunctions _instance;

	public static void changeInstance( BooleanFunctions inst )
	{
		_instance = inst;
	}

	public static BooleanFunctions instance()
	{
		if( _instance == null )
			_instance = new BooleanFunctions();
		return( _instance );
	}

	public Boolean stringToBoolean( String str )
	{
		Boolean result = null;

		if( str != null )
		{
			if( str.equals("0") || str.equals("false" ) )
				result = false;
			else if( str.equals( "1" ) || str.equals( "true" ) )
				result = true;
		}

		return( result );
	}

	public String booleanToString( Boolean value )
	{
		String result = "null";
		if( value != null )
		{
			if( value )
				result = "1";
			else
				result = "0";
		}

		return( result );
	}
}

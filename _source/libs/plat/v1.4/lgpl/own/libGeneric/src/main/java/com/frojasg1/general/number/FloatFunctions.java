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
package com.frojasg1.general.number;

/**
 *
 * @author Usuario
 */
public class FloatFunctions
{
	public static float max( float i1, float i2 )
	{
		return( i1>i2 ? i1 : i2 );
	}

	public static float min( float i1, float i2 )
	{
		return( i1<i2 ? i1 : i2 );
	}

	public static float limit( float value, float lowerBound, float upperBound )
	{
		return( max( lowerBound, min( upperBound, value ) ) );
	}

	public static float abs( float ii )
	{
		return( ii>=0 ? ii : -ii );
	}

	public static float sgn( float ii )
	{
		return( ii>0 ? 1 : ( ii<0 ? -1 : 0 ) );
	}
	
	public static Float parseInt( String str )
	{
		Float result = null;

		try
		{
			result = Float.parseFloat(str);
		}
		catch( Throwable th )
		{}

		return( result );
	}
	
}

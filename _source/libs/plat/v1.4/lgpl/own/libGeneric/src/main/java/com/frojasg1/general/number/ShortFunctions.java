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
public class ShortFunctions
{
	public static short max( short i1, short i2 )
	{
		return( i1>i2 ? i1 : i2 );
	}

	public static short min( short i1, short i2 )
	{
		return( i1<i2 ? i1 : i2 );
	}

	public static short abs( short ii )
	{
		return( (short) ( ii>=0 ? ii : -ii ) );
	}

	public static short sgn( short ii )
	{
		return( (short) ( ii>0 ? 1 : ( ii<0 ? -1 : 0 ) ) );
	}
}

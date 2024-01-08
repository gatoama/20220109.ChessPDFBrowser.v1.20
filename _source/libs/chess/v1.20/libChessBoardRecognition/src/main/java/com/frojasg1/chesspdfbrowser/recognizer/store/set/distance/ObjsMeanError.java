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
package com.frojasg1.chesspdfbrowser.recognizer.store.set.distance;

import com.frojasg1.general.number.DoubleFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ObjsMeanError<CC>
{
	protected static final String SEPARATOR = ";";

	protected CC _elem1;
	protected CC _elem2;

	public ObjsMeanError( CC elem1, CC elem2 )
	{
		_elem1 = elem1;
		_elem2 = elem2;
	}

	public CC getElement1()
	{
		return( _elem1 );
	}

	public CC getElement2()
	{
		return( _elem2 );
	}

	protected String format( double value )
	{
		return( DoubleFunctions.instance().format( value ) );
	}
}

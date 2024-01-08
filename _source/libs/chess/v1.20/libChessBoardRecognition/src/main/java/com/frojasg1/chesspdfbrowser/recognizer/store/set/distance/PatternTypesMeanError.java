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

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PatternTypesMeanError extends ObjsMeanError<String>
{
	protected double _minMeanError = 0d;
	protected double _maxMeanError = 0d;

	public PatternTypesMeanError( String elem1, String elem2,
							double minMeanError, double maxMeanError )
	{
		super( elem1, elem2 );

		_minMeanError = minMeanError;
		_maxMeanError = maxMeanError;
	}

	public double getMinMeanError()
	{
		return( _minMeanError );
	}

	public double getMaxMeanError()
	{
		return( _maxMeanError );
	}

	@Override
	public String toString()
	{
		String result = getElement1() + SEPARATOR + getElement2() + SEPARATOR;
		result += format( getMinMeanError() ) + SEPARATOR + format( getMaxMeanError() );

		return( result );
	}
}

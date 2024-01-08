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

import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PatternsMeanError extends ObjsMeanError<ChessFigurePattern>
{
	protected double _meanError = 0d;

	public PatternsMeanError( ChessFigurePattern elem1, ChessFigurePattern elem2,
									double meanError )
	{
		super( elem1, elem2 );

		_meanError = meanError;
	}

	public double getMeanError()
	{
		return( _meanError );
	}

	@Override
	public String toString()
	{
		return( getElement1().getName() + SEPARATOR + getElement2().getName() +
				SEPARATOR + format( getMeanError() ) );
	}
}

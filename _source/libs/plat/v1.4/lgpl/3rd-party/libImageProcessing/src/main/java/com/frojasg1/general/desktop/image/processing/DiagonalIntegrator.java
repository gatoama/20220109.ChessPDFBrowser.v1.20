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
package com.frojasg1.general.desktop.image.processing;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DiagonalIntegrator
{
/*	protected double _threshold = 0.5D;

	public StraightLineIntegrator( double threshold )
	{
		_threshold = threshold;
	}
*/
	// thick should be odd
	public Map<Point, Long> process( Map<Point, Long> inputPointValues, int xxTolerance )
	{
		Map<Point, Long> result = new HashMap<>();

		AtomicLong atomicLong = new AtomicLong(0);
		for( Map.Entry<Point, Long> entry: inputPointValues.entrySet() )
		{
			Point pointFinal = entry.getKey();
			atomicLong.set(0);
			inputPointValues.keySet().stream()
				.filter( (pt) -> matches( pt, pointFinal, xxTolerance ) )
				.forEach( (pt) -> atomicLong.addAndGet( inputPointValues.get(pt) ) );

			result.put(pointFinal, atomicLong.get() );
		}

		return( result );
	}

	protected int abs( int value )
	{
		return( value > 0 ? value : -value );
	}

	protected boolean matches( Point pt1, Point pt2, int xxTolerance )
	{
		int deltaX = abs( pt1.x - pt2.x );
		int deltaY = abs( pt1.y - pt2.y );

		return( abs( deltaX - deltaY ) <= xxTolerance );
	}
}

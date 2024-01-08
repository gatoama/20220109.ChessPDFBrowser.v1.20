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
package com.frojasg1.desktop.liblens.graphics.lens.tx.accurate;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AccuratePixelElement implements Comparable
{
	protected Coordinate2D _coor = null;
	protected double _factor = 0.0D;

	public AccuratePixelElement( Coordinate2D coor )
	{
		_coor = coor;
	}

	public Coordinate2D getCoordinate2D()
	{
		return( _coor );
	}

	public double getFactor()
	{
		return( _factor );
	}

	public void setFactor( double factor )
	{
		_factor = factor;
	}

	public void incrementFactor( double value )
	{
		_factor += value;
	}

	@Override
	public int compareTo( Object obj )
	{
		int result = -1;
		if( obj instanceof AccuratePixelElement )
		{
			AccuratePixelElement other = (AccuratePixelElement) obj;
			result = (int) Math.signum(_factor - other._factor );
		}

		return( result );
	}
}

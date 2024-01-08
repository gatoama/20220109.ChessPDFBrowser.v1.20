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
package com.frojasg1.desktop.liblens.graphics;

/**
 *
 * @author Usuario
 */
public class Coordinate2D implements Comparable
{
	protected int a_xx = -1;
	protected int a_yy = -1;
	
	public Coordinate2D( int xx, int yy )
	{
		a_xx = xx;
		a_yy = yy;
	}
	
	public Coordinate2D( Coordinate2D other )
	{
		a_xx = other.M_getX();
		a_yy = other.M_getY();
	}

	public void copy( Coordinate2D other )
	{
		a_xx = other.a_xx;
		a_yy = other.a_yy;
	}

	public int M_getX()	{	return( a_xx );	}
	public int M_getY()	{	return( a_yy );	}

	public void M_setX( int xx )		{	a_xx = xx; }
	public void M_setY( int yy )		{	a_yy = yy; }

	@Override
	public boolean equals( Object obj )
	{
		boolean result = false;
		if( obj != null )
		{
			if( obj instanceof Coordinate2D )
			{
				Coordinate2D other = (Coordinate2D) obj;

				result = (a_xx == other.M_getX() ) && (a_yy == other.M_getY() );
			}
		}
		return( result );
	}

	@Override
	public int compareTo( Object obj )
	{
		int result = -1;

		if( obj instanceof Coordinate2D )
		{
			Coordinate2D other = (Coordinate2D) obj;
			result = a_xx - other.M_getX();
			
			if( result == 0 )
				result = a_yy - other.M_getY();
		}

		return( result );
	}

	@Override
	public String toString()
	{
		return( String.format( "( %d, %d )", a_xx, a_yy ) );
	}
}

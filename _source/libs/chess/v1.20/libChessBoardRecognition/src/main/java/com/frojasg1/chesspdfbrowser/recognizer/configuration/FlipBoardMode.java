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
package com.frojasg1.chesspdfbrowser.recognizer.configuration;

import java.util.EnumSet;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public enum FlipBoardMode {
	
	AUTO( "Auto" ),
	WHITE_ON_THE_BOTTOM( "WhiteOnTheBottom" ),
	BLACK_ON_THE_BOTTOM( "BlackOnTheBottom" );


	protected String _name;

	FlipBoardMode( String name )
	{
		_name = name;
	}

	public String getName()
	{
		return( _name );
	}

	public static FlipBoardMode forName( String name )
	{
		FlipBoardMode result = null;
		for( FlipBoardMode elem: EnumSet.allOf( FlipBoardMode.class ) )
		{
			if( Objects.equals( name, elem.getName() ) )
			{
				result = elem;
				break;
			}
		}

		return( result );
	}
}

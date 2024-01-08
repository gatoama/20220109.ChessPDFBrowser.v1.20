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
package com.frojasg1.general.completion;

import com.frojasg1.general.containers.DoubleComparator;
import com.frojasg1.general.containers.OrderedArrayMap;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PreTextStringOrderedArrayMap< VV > extends OrderedArrayMap<String, VV, String >
{
	protected static final DoubleComparator< String, String > defaultSimplifiedKeyDoubleComparator = ( s1, s2 ) -> {
			int result = s1.compareTo( s2 );

			if( ( result < 0 ) && ( s1.length() < s2.length() ) &&
				( s2.substring( 0, s1.length() ).compareTo( s1 ) == 0 ) )
			{
				result = 0;
			}

			return( result );
		};

	public PreTextStringOrderedArrayMap( Class<VV> vClass )
	{
		super( defaultSimplifiedKeyDoubleComparator, vClass );
	}
}

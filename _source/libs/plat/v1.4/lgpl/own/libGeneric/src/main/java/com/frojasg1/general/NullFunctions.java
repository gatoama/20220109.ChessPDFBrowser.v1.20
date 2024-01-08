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
package com.frojasg1.general;

import com.frojasg1.general.number.IntegerFunctions;
import java.util.function.Function;

/**
 *
 * @author fjavier.rojas
 */
public class NullFunctions
{
	protected static NullFunctions _instance;

	public static void changeInstance( NullFunctions inst )
	{
		_instance = inst;
	}

	public static NullFunctions instance()
	{
		if( _instance == null )
			_instance = new NullFunctions();
		return( _instance );
	}

	public <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		RR result = null;
		if( ( obj != null ) && ( getter != null ) )
			result = getter.apply(obj);

		return( result );
	}

	public <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter, RR defaultValue )
	{
		RR result = getIfNotNull(obj, getter);

		if( result == null )
			result = defaultValue;

		return( result );
	}

	public <CC extends Comparable<CC>> CC getOptimumInternal( int comparisonSignTarget, CC obj1, CC obj2 )
	{
		CC result = null;
		if( obj1 == null )
			result = obj2;
		else if( obj2 == null )
			result = obj1;
		else
		{
			int comparisonSign = IntegerFunctions.sgn( obj1.compareTo(obj2) );

			if( comparisonSign == comparisonSignTarget )
				result = obj1;
			else
				result = obj2;
		}

		return result;
	}

	public <CC extends Comparable<CC>> CC getOptimum( int comparisonSignTarget, CC ... values )
	{
		CC result = null;
		for( CC value: values )
			result = getOptimumInternal( comparisonSignTarget, result, value );

		return( result );
	}
}

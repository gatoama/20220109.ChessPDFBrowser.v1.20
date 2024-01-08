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
package com.frojasg1.general.string.translator;

import com.frojasg1.general.BooleanFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericStringTranslator
{
	protected static GenericStringTranslator _instance;

	protected Map<Class<?>, FromString> _fromStringMap = new HashMap<>();
	protected Map<Class<?>, ToString> _toStringMap = new HashMap<>();


	public static synchronized void changeInstance( GenericStringTranslator inst )
	{
		_instance = inst;
	}

	public static synchronized GenericStringTranslator instance()
	{
		if( _instance == null )
		{
			_instance = new GenericStringTranslator();
			_instance.init();
		}

		return( _instance );
	}

	public void init()
	{
		putToFromStringMap( Integer.class, (str) -> IntegerFunctions.parseInt( str ) );
		putToFromStringMap( Long.class, (str) -> IntegerFunctions.parseLong( str ) );
		putToFromStringMap( Boolean.class, (str) -> BooleanFunctions.instance().stringToBoolean(str) );
		putToFromStringMap( Float.class, (str) -> Float.valueOf( str ) );
		putToFromStringMap( Double.class, (str) -> Double.valueOf( str ) );
		putToFromStringMap( Byte.class, (str) -> Byte.valueOf( str ) );
		putToFromStringMap( Short.class, (str) -> Short.valueOf( str ) );
		putToFromStringMap( String.class, str -> str );

		putToToStringMap( Boolean.class, (value) -> BooleanFunctions.instance().booleanToString(value) );
	}

	public <CC> void putToFromStringMap( Class<CC> clazz, FromString<CC> translator )
	{
		_fromStringMap.put( clazz, translator );
	}

	public <CC> void putToToStringMap( Class<CC> clazz, ToString<CC> translator )
	{
		_toStringMap.put( clazz, translator );
	}

	public <CC> CC fromStringWithException( String str, Class<CC> clazz ) throws Exception
	{
		CC result = null;
		if( ( str != null ) && ( clazz != null ) )
		{
			FromString<CC> translator = _fromStringMap.get(clazz);
			if( translator != null )
				result = translator.fromString(str);
		}

		return( result );
	}

	public <CC> CC fromString( String str, Class<CC> clazz )
	{
		CC result = null;
		try
		{
			result = fromStringWithException( str, clazz );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public <CC> String toString( CC value )
	{
		String result = null;

		if( value != null )
		{
			ToString<CC> translator = _toStringMap.get(value.getClass());
			if( translator != null )
				result = translator.toString(value);

			if( result == null )
				result = value.toString();
		}

		return( result );
	}
}

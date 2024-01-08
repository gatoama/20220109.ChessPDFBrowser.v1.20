/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.matchers.impl;

import com.frojasg1.general.matchers.GenMatcher;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class IntegerToleranceMatcher {

	protected static final Class<?> TOLERANCE_CLASS = Integer.class;

	protected static IntegerToleranceMatcher _instance = null;

	protected Map< Class<?>, GenMatcher > _map;

	public static IntegerToleranceMatcher instance()
	{
		if( _instance == null )
			_instance = new IntegerToleranceMatcher();

		return( _instance );
	}

	public IntegerToleranceMatcher()
	{
		_map = new HashMap<>();

		put( Integer.class, IntegerFunctions::match );
	}

	public <CC> void put( Class<CC> clazz, GenMatcher<CC, Integer> matcher )
	{
		_map.put(clazz, matcher);
	}

	protected <CC> GenMatcher<CC, Integer> createGenMatcher( Class<CC> clazz,
		Function<CC, Integer> ... getters )
	{
		GenMatcher<CC, Integer> result = (c1, c2, to) -> {
			boolean res = false;
			if( ( c1 != null ) && ( c2 != null ) && ( getters != null ) && (getters.length > 0 ) )
			{
				res = true;
				for( Function<CC, Integer> getter: getters )
					if( getter != null )
					{
						res = match( getter.apply(c1), getter.apply(c2), to );
						if( res == false )
							break;
					}
			}
			return( res );
		};

		return( result );
	}

	public <CC> GenMatcher<CC, Integer> createAndPutGenMatcher( Class<CC> clazz,
		Function<CC, Integer> ... getters )
	{
		GenMatcher<CC, Integer> result = createGenMatcher( clazz, getters );
		put( clazz, result );

		return( result );
	}

	public <CC> boolean match( CC one, CC two, int tolerance )
	{
		boolean result = false;

		Class<?> clazz = getCommonClass( one, two );
		GenMatcher matcher = getMatcher( clazz );
		
		if( matcher != null )
			result = matcher.match( one, two, tolerance );

		return( result );
	}

	protected Class<?> getCommonClass( Object one, Object two )
	{
		Class<?> result = null;
		if( ( one != null ) && ( two != null ) )
		{
			Class<?> class1 = one.getClass();
			Class<?> class2 = two.getClass();
			
			if( class1.isInstance(two) )
				result = class1;
			else if( class2.isInstance(one) )
				result = class2;
		}

		return( result );
	}

	protected GenMatcher getMatcher( Class<?> clazz )
	{
		return( _map.get( clazz ) );
	}
}

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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ArrayFunctions
{
	public static String[] STRING_ARRAY = new String[0];

	protected static ArrayFunctions _instance;

	public static void changeInstance( ArrayFunctions inst )
	{
		_instance = inst;
	}

	public static ArrayFunctions instance()
	{
		if( _instance == null )
			_instance = new ArrayFunctions();
		return( _instance );
	}

	public Integer getFirst(int[] array)
	{
		Integer result = null;
		if( ( array != null ) && ( array.length > 0 ) )
			result = array[0];

		return( result );
	}

	public <CC> CC getFirst(CC[] array)
	{
		CC result = null;
		if( ( array != null ) && ( array.length > 0 ) )
			result = array[0];

		return( result );
	}

	public String[] joinStringArrays(String[] array1, String[] array2) {
		String[] result = null;
		if (array1 == null) {
			result = array2;
		} else if (array2 == null) {
			result = array1;
		} else {
			result = new String[array1.length + array2.length];
			System.arraycopy(array1, 0, result, 0, array1.length);
			System.arraycopy(array2, 0, result, array1.length, array2.length);
		}
		return result;
	}

	public String[] getArrayFromList( List<String> list )
	{
		String[] result = null;

		if( list != null )
		{
			result = new String[ list.size() ];

			Iterator<String> it = list.iterator();
			for( int ii=0; it.hasNext(); ii++ )
			{
				result[ii]=it.next();
			}
		}

		return( result );
	}

	public String[] getArrayJoiningLists( List<String> list1, List<String> list2 )
	{
		return( joinStringArrays( getArrayFromList(list1), getArrayFromList(list2 ) ) );
	}

	public int getFirstIndexOfReference( String[] array, String reference )
	{
		int index = -1;
		
		if( array != null )
		{
			for( int ii=0; (index==-1) && (ii<array.length); ii++ )
			{
				if( array[ii] == reference )
					index = ii;
			}
		}

		return( index );
	}

	public <T> int getFirstIndexOfEquals( T[] array, T elementToFind )
	{
		return( getFirstIndexOfEquals( array, elementToFind, Function.identity() ) );
	}

	public <T> int getFirstIndexOfEquals( T[] array, T elementToFind,
										Function<T, ?> functionForEquals )
	{
		int index = -1;
		
		if( ( array != null ) && (elementToFind != null ) )
		{
			for( int ii=0; (index==-1) && (ii<array.length); ii++ )
			{
				if( Objects.equals( getIfNotNull( elementToFind, functionForEquals ),
									getIfNotNull( array[ii], functionForEquals ) ) )
				{
					index = ii;
				}
			}
		}

		return( index );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC, RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	public int getFirstIndexOf( int[] array, int elementToFind )
	{
		int index = -1;
		
		if( array != null )
		{
			for( int ii=0; (index==-1) && (ii<array.length); ii++ )
				if( array[ii] == elementToFind )
					index = ii;
		}

		return( index );
	}

	public <T> T[] addElement( T[] array, T newElem )
	{
		T[] result = Arrays.copyOf(array, array.length + 1);
		result[array.length] = newElem;

		return( result );
	}

	public <T> boolean contains( T[] array, T elementToFind )
	{
		return( getFirstIndexOfEquals( array, elementToFind ) >= 0 );
	}

	// https://stackoverflow.com/questions/7863792/compilation-error-generic-array-creation
	public <E> E[] createArray(E... elements) {
		return Arrays.copyOf(elements, elements.length);
	}

	// https://stackoverflow.com/questions/7863792/compilation-error-generic-array-creation
	public <E> E[] createArrayWithLength(int length, E... elements) {
		return Arrays.copyOf(elements, length);
	}

	public <E> E[] createEmptyArray(E... elements)
	{
		E[] result = null;
		if( elements != null )
		{
			result = createArray(elements);
			for(int ii=0; ii<result.length; ii++)
				result[ii] = null;
		}
		return( result );
	}

	public <E> void cleanArray( E[] array )
	{
		if( array != null )
			for( int ii=0; ii<array.length; ii++ )
				array[ii] = null;
	}

	public <E> boolean equals( E[] array1, E[] array2 )
	{
		boolean result = true;

		if( array1 != array2 )
		{
			if( ( array1 == null ) || ( array2 == null ) ||
				( array1.length != array2.length ) )
				result = false;
			else
			{
				for( int ii=0; result && (ii<array1.length); ii++ )
					result = ( array1[ii] == array2[ii] );
			}
		}

		return( result );
	}

	public <CC> int countEqualElems( CC[] array, CC model )
	{
		int result = 0;
		if( array != null )
			for( CC elem: array )
				if( Objects.equals(model, elem) )
					result++;

		return( result );
	}

	public <CC> CC getMostRepeated( CC[] array )
	{
		CC result = null;
		if( array != null )
		{
			int max = 0;
			int tmp = 0;
			for( CC elem: array )
				if( ( tmp = countEqualElems(array, elem) ) > max )
				{
					max = tmp;
					result = elem;
				}
		}

		return( result );
	}

	public int[] translateArray( int[] viewIndexArray,
									Function<Integer, Integer> elementTranslatorFunction )
	{
		int[] result = null;
		if( viewIndexArray != null )
		{
			result = new int[viewIndexArray.length];
			for( int ii=0; ii<viewIndexArray.length; ii++ )
				result[ii] = elementTranslatorFunction.apply( viewIndexArray[ii] );
		}
		return( result );
	}

	public int getIndexForOrderedInsertion( int[] array, int value )
	{
		int result = 0;
		
		if( array != null )
		{
			for( int ii=0; ii<array.length; ii++ )
				if( value < array[ii] )
				{
					result = ii;
					break;
				}
		}

		return( result );
	}

	public int[] copy( int[] input )
	{
		int[] result = null;
		if( input != null )
			result = Arrays.copyOf(input, input.length);

		return( result );
	}

	public byte[] copy( byte[] input )
	{
		byte[] result = null;
		if( input != null )
			result = Arrays.copyOf(input, input.length);

		return( result );
	}

	public int[] createIntervalSelection( int indexWhereToInsert, int size )
	{
		int[] result = new int[size];
		for( int ii=0; ii<size; ii++ )
			result[ii] = ii+indexWhereToInsert;

		return( result );
	}

	public boolean isEmpty( int[] array )
	{
		return( getLength( array ) == 0 );
	}

	public int getLength( int[] array )
	{
		return( ( array == null ) ? 0 : array.length );
	}
}

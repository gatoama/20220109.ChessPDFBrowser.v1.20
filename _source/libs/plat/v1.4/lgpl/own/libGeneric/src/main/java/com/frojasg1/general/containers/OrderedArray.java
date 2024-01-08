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
package com.frojasg1.general.containers;

import com.frojasg1.general.number.IntegerFunctions;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OrderedArray< CC extends Comparable<CC>, SS >
{
	protected static final int FIND_FIRST = 0;
	protected static final int FIND_LAST = 1;

	protected static final int DEFAULT_INITIAL_SIZE = 1000;
	protected static final int DEFAULT_STEP_INCREMENT = 1000;

	protected final Class<CC> _class;

	protected DoubleComparator<CC, CC> _elemComparator = ( cc1, cc2 ) -> { return( cc1.compareTo( cc2 ) ); };
	protected DoubleComparator<CC, CC> _exactElemComparator = null;

	protected CC[] _array = null;

	protected int _initialSize = DEFAULT_INITIAL_SIZE;
	protected int _stepIncrement = -1;
	protected int _size = 0;

	protected DoubleComparator< SS, CC > _doubleComparator = null;

	public OrderedArray( Class<CC> clazz, DoubleComparator< SS, CC > doubleComparator )
	{
		this( clazz, doubleComparator, DEFAULT_INITIAL_SIZE, DEFAULT_STEP_INCREMENT );
	}

	public OrderedArray( Class<CC> clazz, DoubleComparator< SS, CC > doubleComparator,
							int initialSize, int stepIncrement )
	{
		_class = clazz;
		_doubleComparator = doubleComparator;

		_initialSize = initialSize;
		_array = createInitialArray();

		_stepIncrement = stepIncrement;
	}

	public Class<? extends OrderedArray<CC, SS> > getGenericClass()
	{
		return ((Class<? extends OrderedArray<CC, SS> >) ((ParameterizedType) getClass().getGenericSuperclass()).
							getActualTypeArguments()[0]);
	}

	public CC[] createInitialArray()
	{
		return( createArray( _initialSize ) );
	}

	public void clear()
	{
		_size = 0;
		_array = createInitialArray();
	}

	public void setExactElemComparator( DoubleComparator<CC, CC> comparator )
	{
		_exactElemComparator = comparator;
	}

	public void setElemComparator( Comparator<CC> comparator )
	{
		_elemComparator = ( cc1, cc2 ) -> comparator.compare( cc1, cc2 );
	}

	public int getCapacity()
	{
		int result = 0;
		if( _array != null )
			result = _array.length;

		return( result );
	}

	public int size()
	{
		return( _size );
	}

	protected CC[] createArray( int size )
	{
		return( ( CC[] ) Array.newInstance( _class, size) );
	}

	protected CC[] increaseArraySize( int sizeToIncrease )
	{
		int oldCapacity = getCapacity();
		CC[] newArray = createArray( oldCapacity + sizeToIncrease );

		System.arraycopy( _array, 0, newArray, 0, size() );

		return( newArray );
	}

	protected void incrementCapacityIfNecessary()
	{
		if( size() >= getCapacity() )
		{
			_array = increaseArraySize( _stepIncrement );
		}
	}

/*	public void addElementAtTheEnd( CC elem )
	{
		insertAtPosition( elem, _size );
	}
*/
	public void addOrdered( CC elem )
	{
		int index = findPositionToInsert( elem, _elemComparator );

		insertAtPosition( elem, index );
	}

	public void insertAtPosition( CC elem, int index )
	{
		incrementCapacityIfNecessary();

		if( ( index < 0 ) ||
			( index > _size ) ||
			( index >= getCapacity() ) )
		{
			throw( new IllegalArgumentException( "Index out of bounds." ) );
		}

		onlyInsert( elem, index );
	}

	protected void onlyInsert( CC elem, int index )
	{
		CC current = elem;
		for( int ii=index; ii<(_size+1); ii++ )
		{
			CC next = _array[ii];
			_array[ii] = current;
			current = next;
		}
		_size++;
	}

	protected CC remove_internal( int index )
	{
		CC result = _array[index];

		for( int ii=index; ii<(_size-1); ii++ )
		{
			_array[ii] = _array[ii+1];
		}

		_size--;

		return( result );
	}

	protected <KK> int findFirst( KK elem, DoubleComparator< KK, CC > comparator )
	{
		return( find( elem, comparator, FIND_FIRST ) );
	}

	protected <KK> int findLast( KK elem, DoubleComparator< KK, CC > comparator )
	{
		return( find( elem, comparator, FIND_LAST ) );
	}

	protected <KK> int findPositionToInsert( KK elem, DoubleComparator< KK, CC > comparator )
	{
		int result = findLast( elem, comparator );
		if( ( result == -1 ) ||
			( result < _size ) && ( comparator.compare( elem, _array[ result ]  ) >= 0 ) )
		{
			result++;
		}

		return( result );
	}

	public CC getFirstElementFromSimplifiedKey( SS simplifiedKey )
	{
		CC result = null;
		int index = findFirst( simplifiedKey, _doubleComparator );

		if( index >= 0 )
			result = _array[ index ];

		return( result );
	}

	public CC getElementFromSimplifiedKey( SS simplifiedKey )
	{
		CC result = null;
		int index = findFirstExact( simplifiedKey, _doubleComparator, _exactElemComparator );

		if( index >= 0 )
			result = _array[ index ];

		return( result );
	}

	public CC getElementFromKey( CC key )
	{
		CC result = null;
		int index = findFirstExact( key, _elemComparator, _exactElemComparator );

		if( index >= 0 )
			result = _array[ index ];

		return( result );
	}

	public CC getFirstElementFromKey( CC key )
	{
		CC result = null;
		int index = findFirstExact( key, _elemComparator, _elemComparator );

		if( index >= 0 )
			result = _array[ index ];

		return( result );
	}

	public CC[] getRangeFromKey( CC initialElem, CC finalElem )
	{
		return( getRange( initialElem, finalElem, _elemComparator ) );
	}

	public CC[] getRangeFromSimplifiedKey( SS initialElem, SS finalElem )
	{
		return( getRange( initialElem, finalElem, _doubleComparator ) );
	}

	public <KK> CC[] getRange( KK initialElem, KK finalElem,
								DoubleComparator< KK, CC > comparator )
	{
		int index1 = this.findFirst( initialElem, comparator );
		int index2 = this.findLast( finalElem, comparator );

		if( comparator.compare( initialElem, _array[index1] ) > 0 )
			index1++;

		if( comparator.compare( finalElem, _array[index2] ) < 0 )
			index2--;

		int size = index2 - index1 + 1;

		if( ( index1 >= size() ) || ( index2 < 0 ) )
			size = 0;

		CC[] result = createArray( size );

		for( int ii=index1; ii<=index2 && ii>=0 && ii<size(); ii++ )
		{
			result[ii-index1] = _array[ii];
		}

		return( result );
	}

	// type can be:
	// FIND_FIRST or FIND_LAST
	protected <KK> int find( KK elem, DoubleComparator< KK, CC > comparator, int type )
	{
		int result = -2;

		int low = 0;
		int high = _size-1;
		int middle = -1;

		if( _size == 0 )
			result = -1;
		else if( matchesToFind( elem, low, comparator, type ) == 0 )
			result = low;
		else if( matchesToFind( elem, high, comparator, type ) == 0 )
			result = high;

		while( ( result < -1 ) && ( low < high ) )
		{
			middle = ( low + high ) / 2;

			int middleComparisonResult = matchesToFind( elem, middle, comparator, type );

			if( middleComparisonResult == 0 )
				result = middle;
			else if( middleComparisonResult < 0 )
				high = middle;
			else if( middleComparisonResult > 0)
				low = middle;
		}

		if( low == high )
			result = high;

		return( result );
	}

	/**
	 * it returns:
	 *		0 - if index is the desired position
	 *		-1 - if elem is lesser than the element at index (_array[index])
	 *		1 - if elem is greater than the element at index (_array[index])
	*/
	protected <KK> int matchesToFind( KK elem, int index,
									DoubleComparator< KK, CC > comparator,
									int type )
	{
		int result = 0;

		int comparison = compare( elem, index, comparator );

		result = comparison;

		if( type == FIND_FIRST )
		{
			int previousComparison = compare( elem, index -1, comparator );
			if( ( previousComparison > comparison ) &&
				( previousComparison > 0 ) )
			{
				result = 0;
			}
			else if( comparison == 0 )
				result = -1;
			else if( ( comparison > 0 ) && ( index == ( _size -1 ) ) )
				result = 0;
		}
		else if( type == FIND_LAST )
		{
			int followingComparison = compare( elem, index + 1, comparator );

			if( followingComparison < 0 )
			{
				if( comparison >= 0 )
					result = 0;
				else if( index == 0 )
					result = 0;
			}
			else if( comparison == 0 )
			{
				result = 1;
			}
			else if( ( comparison < 0 ) && ( index == 0 ) )
				result = 0;
		}

		return( result );
	}

	/**
	 * it returns:
	 *		0 - if index is the desired position
	 *		-1 - if elem is lesser than the element at index (_array[index])
	 *		1 - if elem is greater than the element at index (_array[index])
	*/
	protected <KK> int compare( KK elem, int index, DoubleComparator< KK, CC > comparator )
	{
		int result = 0;
		if( index < 0 )
			result = 1;
		else if( index >= _size )
			result = -1;
		else
			result = IntegerFunctions.sgn( comparator.compare( elem, _array[index] ) );

		return( result );
	}

	public CC removeElement( CC elem )
	{
		return( remove( elem, _elemComparator ) );
	}

	public CC removeFirstElement( CC elem )
	{
		return( removeFirst( elem, _elemComparator ) );
	}

	protected <KK> CC remove( KK elem, DoubleComparator< KK, CC > comparator )
	{
		CC result = null;
		int index = findFirstExact( elem, comparator, _exactElemComparator );

		if( index >= 0 )
			result = remove_internal( index );

		return( result );
	}

	protected <KK> CC removeFirst( KK elem, DoubleComparator< KK, CC > comparator )
	{
		CC result = null;
		int index = findFirstExact( elem, comparator, _elemComparator );

		if( index >= 0 )
			result = remove_internal( index );

		return( result );
	}

	public <KK> int findFirstExact( KK elem, DoubleComparator< KK, CC > comparator,
									DoubleComparator< CC, CC > exactComparator )
	{
		int result = -1;

		int ii = findFirst(elem, comparator);
		if( ii >= 0 )
		{
			boolean found = false;
			while( !found && ( ii<_size ) &&
					( comparator.compare( elem, _array[ii] ) == 0 ) )
			{
				if( !elem.getClass().getName().equals( _array[ii].getClass().getName() ) )
					found = false;
				else
				{
					if( exactComparator != null )
						found = ( exactComparator.compare( (CC) elem, _array[ii] ) == 0 );
					else
						found = ( _array[ii] == elem );
				}

				if( !found )
					ii++;
			}
			if( found )
				result = ii;
		}

		return( result );
	}

	public CC find( SS simplifiedKey )
	{
		CC result = null;
		
		if( _size > 0 )
		{
			int index = this.findFirst(simplifiedKey, _doubleComparator );

			if( _doubleComparator.compare(simplifiedKey, _array[index] ) == 0 )
				result = _array[index];
		}

		return( result );
	}
}

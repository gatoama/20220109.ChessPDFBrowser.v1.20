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

import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Usuario
 */
public class CollectionFunctions 
{
	protected static CollectionFunctions _instance;

	public final Comparator<Object> _ASCENDANT_NO_CASE_SENSITIVE = getLocaleStringComparatorNoCaseSensitive( null );

	protected Map< Locale, Comparator<Object> > _mapOfComparatorsNoCaseSensitive = new HashMap<>();

	public static void changeInstance( CollectionFunctions inst )
	{
		_instance = inst;
	}

	public static CollectionFunctions instance()
	{
		if( _instance == null )
			_instance = new CollectionFunctions();
		return( _instance );
	}

	public Comparator<Object> getLocaleStringComparatorNoCaseSensitive( Locale locale )
	{
		Comparator<Object> result = _ASCENDANT_NO_CASE_SENSITIVE;

		if( locale != null )
		{
			result = _mapOfComparatorsNoCaseSensitive.get( locale );

			if( result == null )
			{
				result = new ComparatorNoCaseSensitive( locale );
				_mapOfComparatorsNoCaseSensitive.put( locale, result );
			}
		}

		return( result );
	}

	public void sortNoCaseSensitive( List list )
	{
		Collections.sort( list, _ASCENDANT_NO_CASE_SENSITIVE );
	}

	public void sortNoCaseSensitive( List list, Locale locale )
	{
		Collections.sort( list, getLocaleStringComparatorNoCaseSensitive( locale ) );
	}

	public int indexOfReference( Collection col, Object obj )
	{
		int result=-1;
		
		int index = 0;
		Iterator it = col.iterator();
		while( (result==-1) && it.hasNext() )
		{
			if( it.next() == obj )
				result = index;
			index++;
		}
		return( result );
	}

	public void removeRepeatedSortedPreviously( List list )
	{
		Object[] array = list.toArray();

		for( int ii=array.length-2; ii>=0; ii-- )
		{
			if( array[ii+1].equals( array[ii] ) )
			list.remove(ii);
		}
	}

	public boolean itemEqualsExistsInList( List list, Object item )
	{
		boolean result = false;
		
		if( list != null )
		{
			Iterator it = list.iterator();
			while( !result && it.hasNext() )
			{
				Object current = it.next();
				result = ( current == item ) || ( current != null ) && ( current.equals(item) );
			}
		}

		return( result );
	}
	
	public List removeItemsThatExistInTheSecondList( List list, List itemsToRemove )
	{
		List result = new ArrayList();
		
		Object[] array = list.toArray();
		for( int ii=array.length-1; ii>=0; ii-- )
		{
			if( itemEqualsExistsInList( itemsToRemove, array[ii] ) )
			{
				result.add( list.remove(ii) );
			}
		}

		return( result );
	}

	public List leaveOnlyExistingInTheSecondList( List list, List itemsToKeep )
	{
		List result = new ArrayList();
		
		Iterator it = list.iterator();
		while( it.hasNext() )
		{
			Object current = it.next();
			if( itemEqualsExistsInList( itemsToKeep, current ) )
			{
				result.add( current );
			}
		}

		return( result );
	}

	protected void copyCoolectionException( Collection input, Collection output, boolean exceptionUponFailure )
	{
		Collection copy = output;
		try
		{
			if( ( output != null ) && ( input != null ) )
			{
				output.clear();
				output.addAll( input );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			if( exceptionUponFailure )
			{
				exceptionUponFailure = false;
				copyCoolectionException( copy, output, exceptionUponFailure );
				throw( th );
			}
		}
	}

	public void copyCollection( Collection input, Collection output )
	{
		boolean exceptionUponFailure = true;
		copyCoolectionException( input, output, exceptionUponFailure );
	}

	public <CC> List<CC> reverseList( List<CC> list )
	{
		List<CC> result = new ArrayList<>(list);
		Collections.reverse(result);

		return( result );
	}

	public void reverseOrder( Collection col ) throws NoSuchMethodException, InstantiationException,
														IllegalAccessException, IllegalArgumentException,
														InvocationTargetException
	{
		Collection result = null;

		if( col != null )
		{
			result = col.getClass().getConstructor().newInstance();

			LinkedList ll = new LinkedList();
			ll.addAll(col);

			Iterator it = ll.descendingIterator();
			while( it.hasNext() )
				result.add( it.next() );

			copyCollection( result, col );
		}
	}

	public <T> int getNumElems( Collection< T >[] array )
	{
		int result = 0;
		if( array != null )
		{
			for( int ii=0; ii<array.length; ii++ )
			{
				if( array[ii] != null )
					result = array[ii].size();
			}
		}
		return( result );
	}

	public <T> boolean elementExists(List<T> list, T elem )
	{
		boolean result = false;

		if( ( list != null ) && ( elem != null ) )
			result = list.contains(elem);

		return( result );
	}

	public <T> boolean elementExists(Set<T> set, T elem )
	{
		boolean result = false;

		if( ( set != null ) && ( elem != null ) )
			result = set.contains(elem);

		return( result );
	}

	public <CC> LinkedList<CC> copyLimitingNumElems( List<CC> input, Integer maxNumElems )
	{
		LinkedList<CC> result = null;
		if( input != null )
		{
			result = new LinkedList<>();
			result.addAll(input);

			if( maxNumElems != null )
				while( result.size() > maxNumElems )
					result.removeLast();
		}
		return( result );
	}

	protected static class ComparatorNoCaseSensitive implements Comparator<Object>
	{
		Collator _collator = null;
		
		public ComparatorNoCaseSensitive( Locale locale )
		{
			if( locale != null )
				_collator = Collator.getInstance( locale );
		}

		@Override
		public int compare( Object one, Object two )
		{
			int result = 0;
			if( ( one == null ) && ( two == null ) )
				result = 0;
			else if( one != null )
			{
				result = 1;

				if( two != null )
				{
					if( ( one.toString() == null ) && (two.toString() == null ) )
						result = 0;
					else if( two.toString() != null )
					{
						result = -1;
						if( one.toString() != null )
						{
							if( _collator != null )
								result = _collator.compare( one.toString().toLowerCase(),
															two.toString().toLowerCase() );
							else
								result = - two.toString().toLowerCase().compareTo( one.toString().toLowerCase() );
						}
					}
				}
			}

			return( result );
		}
	}

	public <CC> List<CC> transformList( List<CC> list, TransformObject<CC> transformer )
	{
		List<CC> result = null;

		if( list != null )
		{
			if( transformer == null )
				result = list;
			else
			{
				result = new ArrayList<>();
				for( CC item: list )
					result.add( transformer.transform(item) );
			}
		}

		return( result );
	}

	protected boolean isInsideBounds( List list, Integer index )
	{
		return( ( index != null ) && ( list != null ) &&
				( index >= 0 ) && ( index < list.size() ) );
	}

	public <CC> CC get( List<CC> list, Integer index )
	{
		CC result = null;
		if( isInsideBounds( list, index ) )
			result = list.get(index);

		return( result );
	}

	public <CC> CC getFirstOf( List<CC> list )
	{
		CC result = null;
		if( ( list != null ) && ! list.isEmpty() )
			result = list.get(0);

		return( result );
	}

	public <CC> CC getLastOf( List<CC> list )
	{
		CC result = null;
		if( ( list != null ) && ! list.isEmpty() )
		{
			if( list instanceof LinkedList )
				result = ( (LinkedList<CC>) list ).getLast();
			else
				result = list.get( list.size() - 1 );
		}
		return( result );
	}

	public <CC> void threadSafeAdd( List<? super CC> list, CC elem )
	{
		if( list != null )
			synchronized( list )
			{
				list.add(elem);
			}
	}

	public <CC> List<CC> copyList( List<CC> source )
	{
		return( copyToList( source, ArrayList::new ) );
	}

	public <RR> List<RR> copyToList( Collection<RR> source )
	{
		return( copyToList( source, ArrayList::new ) );
	}

	public <RR, CC extends List<? super RR>> CC copyToList( Collection<RR> source,
															Supplier<CC> emptyListBuilder )
	{
		CC result = null;
		if( source != null )
		{
			result = emptyListBuilder.get();
			addAll( source, result );
		}
		return( result );
	}

	public <RR> List<RR> copyToList( Enumeration<RR> source )
	{
		return( copyToList( source, ArrayList::new ) );
	}

	public <RR, CC extends List<? super RR>> CC copyToList( Enumeration<RR> source,
															Supplier<CC> emptyListBuilder )
	{
		CC result = null;
		if( source != null )
		{
			result = emptyListBuilder.get();
			addAll( source, result );
		}
		return( result );
	}

	/*
		<RR> - Record
		<CC> - Container
	*/
	public <RR> void addAll( Enumeration<RR> source, List<? super RR> result )
	{
		threadSafeListMethod( source, src -> addAllInternal(src, result) );
	}

	// comes from thread safe
	protected <RR> void addAllInternal( Enumeration<RR> source, List<? super RR> result )
	{
		if( source != null )
		{
			while( source.hasMoreElements() )
				result.add( source.nextElement() );
		}
	}

	/*
		<RR> - Record
		<CC> - Container
	*/
	public <RR> void addAll( Collection<RR> source, List<? super RR> result )
	{
		threadSafeListMethod( source, src -> result.addAll(src) );
	}

	public <CC> void threadSafeListMethod( CC source, Consumer<CC> method )
	{
		if( source != null )
		{
			synchronized( source )
			{
				method.accept(source);
			}
		}
	}

	/*
		<CC> - Class of source
		<RR> - Result
	*/
	public <CC, RR> RR threadSafeListFunction( CC source, Function<CC, RR> function )
	{
		RR result = null;
		if( source != null )
		{
			synchronized( source )
			{
				result = function.apply(source);
			}
		}

		return( result );
	}

	public <RR> Integer getIndexOfSame( List<RR> list, RR obj )
	{
		Integer result = null;
		int index = 0;
		for( RR elem: list )
		{
			if( elem == obj )
			{
				result = index;
				break;
			}
			index++;
		}

		return( result );
	}

	public <RR> int translateIndex( List<RR> fromList, List<RR> toList, int fromIndex )
	{
		int result = -1;

		RR record = get( fromList, fromIndex );
		if( record != null )
			result = toList.indexOf(record);

		return( result );
	}

	public <RR> List<RR> translateArrayIntoList( int[] array,
												Function<Integer, RR> translationFunction )
	{
		List<RR> result = null;
		if( array != null )
		{
			result = new ArrayList<>();
			for( int index: array )
				result.add( translationFunction.apply(index) );
		}

		return( result );
	}

	public <R> void eraseFromIndexToEnd( List<R> list, int index )
	{
		if( list != null )
			while( list.size() > index )
				list.remove(index);
	}

	public interface TransformObject<CC>
	{
		public CC transform( CC input );
	}
}



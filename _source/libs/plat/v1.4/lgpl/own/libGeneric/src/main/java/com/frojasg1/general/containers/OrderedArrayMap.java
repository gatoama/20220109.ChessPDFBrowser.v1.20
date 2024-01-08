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

import java.lang.reflect.Array;
import java.util.Comparator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OrderedArrayMap< KK extends Comparable<KK>, VV, SS >
{
	protected OrderedArray< GenericMapElement<KK,VV>, SS > _array = null;

	protected Class<VV> _vClass = null;

	protected ExactMapElemComparator _exactMapElemComparator = null;

	public OrderedArrayMap( DoubleComparator<SS, KK> doubleComparator,
							Class<VV> vClass )
	{
		_vClass = vClass;
		_array = new OrderedArray<GenericMapElement< KK, VV >, SS>( (Class<GenericMapElement< KK, VV >> )createMapElement().getClass(),
																	createSimplifiedDoubleComparator( doubleComparator) );
	}

	public void setExactValueComparator( DoubleComparator< VV, VV > exactValueComparator )
	{
		_exactMapElemComparator = new ExactMapElemComparator( exactValueComparator );
		_array.setExactElemComparator( _exactMapElemComparator );
	}

	public void setElemComparator( Comparator< KK > comparator )
	{
		_array.setElemComparator( createKeyComparator( comparator ) );
	}

	protected VV[] createArrayVV( int size )
	{
		return( ( VV[] ) Array.newInstance( _vClass, size) );
	}

	protected VV[] getVVarray( GenericMapElement< KK, VV >[] pairArray )
	{
		VV[] result = null;
		if( pairArray != null )
		{
			result = createArrayVV( pairArray.length );
			for( int ii=0; ii<result.length; ii++ )
				result[ii] = pairArray[ii]._value;
		}

		return( result );
	}

	protected VV getValue( GenericMapElement< KK, VV > me )
	{
		VV result = null;
		if( me != null )
			result = me._value;

		return( result );
	}

	protected GenericMapElement< KK, VV > getKeyValuePair( KK key )
	{
		GenericMapElement< KK, VV > keyGMP = createKeyValuePair( key );
		GenericMapElement< KK, VV > result = _array.getElementFromKey(keyGMP);

		return( result );
	}

	public VV getValueFromKey( KK key )
	{
		GenericMapElement< KK, VV > me = getKeyValuePair(key);

		return( getValue(me) );
	}

	protected GenericMapElement< KK, VV > getFirstKeyValuePair( KK key )
	{
		GenericMapElement< KK, VV > keyGMP = createKeyValuePair( key );
		GenericMapElement< KK, VV > result = _array.getFirstElementFromKey(keyGMP);

		return( result );
	}

	public VV getFirstValueFromKey( KK key )
	{
		GenericMapElement< KK, VV > me = getFirstKeyValuePair(key);

		return( getValue(me) );
	}

	protected GenericMapElement< KK, VV > createKeyValuePair( KK key )
	{
		GenericMapElement< KK, VV > result = createMapElement();
		result._key = key;

		return( result );
	}

	public VV getValueFromSimplifiedKey( SS objectToLookFor )
	{
		GenericMapElement< KK, VV > me = _array.getElementFromSimplifiedKey(objectToLookFor);

		return( getValue(me) );
	}

	public VV getFirstValueFromSimplifiedKey( SS objectToLookFor )
	{
		GenericMapElement< KK, VV > me = _array.getFirstElementFromSimplifiedKey(objectToLookFor);

		return( getValue(me) );
	}

	public VV[] getRangeFromSimplifiedKey( SS initialSK, SS finalSK )
	{
		VV[] result = null;

		GenericMapElement< KK, VV >[] mapElemRange = _array.getRangeFromSimplifiedKey( initialSK, finalSK );

		return( getVVarray( mapElemRange ) );
	}

	public KK getKeyFromSimplifiedKey( SS objectToLookFor )
	{
		GenericMapElement< KK, VV > me = _array.getElementFromSimplifiedKey(objectToLookFor);

		KK result = null;
		if( me != null )
			result = me._key;

		return( result );
	}

	protected GenericMapElement< KK, VV > createMapElement()
	{
		return( new GenericMapElement<>() );
	}

	protected GenericMapElement< KK, VV > createMapElement( KK key, VV value )
	{
		GenericMapElement< KK, VV > elem = createMapElement();
		elem._key = key;
		elem._value = value;

		return( elem );
	}

	protected DoubleComparator< SS, GenericMapElement< KK, VV > > createSimplifiedDoubleComparator( DoubleComparator<SS, KK> doubleComparator )
	{
		return( ( mm, me ) -> doubleComparator.compare( mm, me._key ) );
	}

	protected Comparator< GenericMapElement< KK, VV > > createKeyComparator( Comparator<KK> keyComparator )
	{
		return( ( kk1, kk2 ) -> keyComparator.compare( kk1._key, kk2._key ) );
	}

	public class GenericMapElement< KK extends Comparable<KK>, VV > implements Comparable<GenericMapElement< KK, VV >>
	{
		KK _key;
		VV _value;

		public int compareTo( GenericMapElement< KK, VV > other )
		{
			int result = 1;
			if( other != null )
				result = _key.compareTo( other._key );

			return( result );
		}
	}

	public GenericMapElement< KK, VV > put( KK key, VV value )
	{
		GenericMapElement< KK, VV > result = getKeyValuePair( key );
		
		if( result == null )
		{
			result = createMapElement( key, value );
			_array.addOrdered( result );
		}
		else
		{
			result._value = value;
		}

		return( result );
	}

	public VV remove( KK key )
	{
		GenericMapElement< KK, VV > elemToRemove = createMapElement( key, null );
		elemToRemove = _array.removeElement(elemToRemove);

		VV result = null;
		if( elemToRemove != null)
			result = elemToRemove._value;

		return( result );
	}

	public VV removeFirst( KK key )
	{
		GenericMapElement< KK, VV > elemToRemove = createMapElement( key, null );
		elemToRemove = _array.removeFirstElement(elemToRemove);

		VV result = null;
		if( elemToRemove != null)
			result = elemToRemove._value;

		return( result );
	}

	public VV removeExact( KK key, VV val )
	{
		GenericMapElement< KK, VV > elemToRemove = createMapElement( key, val );
		elemToRemove = _array.removeElement(elemToRemove);

		VV result = null;
		if( elemToRemove != null)
			result = elemToRemove._value;

		return( result );
	}

	public int size()
	{
		return( _array.size() );
	}

	public void clear()
	{
		_array.clear();
	}

	public class MapDoubleComparator implements DoubleComparator< GenericMapElement<KK, VV>, GenericMapElement<KK, VV> >
	{
		protected DoubleComparator< KK, KK > _internalComparator = null;

		public MapDoubleComparator( DoubleComparator< KK, KK > internalComparator )
		{
			_internalComparator = internalComparator;
		}

		@Override
		public int compare(GenericMapElement<KK, VV> mm, GenericMapElement<KK, VV> cc)
		{
			return( _internalComparator.compare( mm._key, cc._key ) );
		}
	}

	public class ExactMapElemComparator implements DoubleComparator< GenericMapElement<KK, VV>, GenericMapElement<KK, VV> >
	{
		protected DoubleComparator< VV, VV > _internalComparator = null;

		public ExactMapElemComparator( DoubleComparator< VV, VV > internalComparator )
		{
			_internalComparator = internalComparator;
		}

		@Override
		public int compare(GenericMapElement<KK, VV> mm, GenericMapElement<KK, VV> cc)
		{
			int result = mm._key.compareTo( cc._key );

			if( result == 0 )
				result = _internalComparator.compare( mm._value, cc._value );

			return( result );
		}
	}
}

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
package com.frojasg1.general.combohistory.impl;

import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.string.StringFunctions;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextComboBoxHistory implements TextComboBoxContent
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected List<String> _listOfItems = null;

	protected Integer _maxItemsToSave = null;

	protected String[] _elementsForCombo = null;

	// function for DefaultConstructorInitCopier
	public TextComboBoxHistory( )
	{
		
	}

	public TextComboBoxHistory( Integer maxItemsToSave )
	{
		_maxItemsToSave = maxItemsToSave;
	}

	public void init( List<String> initialList )
	{
		if( initialList == null )
			_listOfItems = new ArrayList<> ();
		else
			_listOfItems = _copier.copy( initialList );
	}

	// function for DefaultConstructorInitCopier
	public void init(TextComboBoxHistory that)
	{
		_listOfItems = _copier.copyCollection( that._listOfItems );

		_maxItemsToSave = that._maxItemsToSave;

		_elementsForCombo = _copier.copyArray( String[].class, that._elementsForCombo );
	}

	@Override
	public boolean contains( String value )
	{
		return( _listOfItems.contains( value ) );
	}

	public void setMaxItemsToSave( Integer maxItemsToSave )
	{
		_maxItemsToSave = maxItemsToSave;
	}

	public Integer getMaxItemsToSave()
	{
		return( _maxItemsToSave );
	}

	@Override
	public void setColectionOfItems( Collection<String> listOfItems )
	{
		if( listOfItems != null )
		{
			_listOfItems.clear();
			_listOfItems.addAll( listOfItems );
			_elementsForCombo = null;
		}
	}

	@Override
	public List<String> getListOfItems()
	{
		return( _listOfItems );
	}

	@Override
	public String[] getElementsForCombo()
	{
		if( _elementsForCombo == null )
		{
			_elementsForCombo = _listOfItems.toArray(new String[_listOfItems.size()] );
		}
		return( _elementsForCombo );
	}

	protected void removeItem_internal( String item )
	{
		int repeatedIndex = getRepeatedIndex(_listOfItems, item );

		if( repeatedIndex >= 0 )
		{
			_listOfItems.remove( repeatedIndex );
			_elementsForCombo = null;
		}
	}

	protected <CC> CC getFirstOf( List<CC> list )
	{
		return( CollectionFunctions.instance().getFirstOf( list ) );
	}

	protected void changeSelectedItemList( String item )
	{
		removeItem_internal( item );

		if( !StringFunctions.instance().isEmpty(item) &&
			!Objects.equals(item, getFirstOf(_listOfItems) ) )
		{
			_listOfItems.add( 0, item );
		}

		if( _maxItemsToSave != null )
		{
			while( _listOfItems.size() > _maxItemsToSave )
				_listOfItems.remove( (int) _maxItemsToSave );
		}

		_elementsForCombo = null;
	}

	protected int getRepeatedIndex( Collection<String> col, String item )
	{
		int result = -1;
		if( item != null )
		{
			int index = 0;
			Iterator<String> it = col.iterator();
			while( (result == -1 ) && it.hasNext() )
			{
				String current = (String) it.next();
				if( item.equals( current ) )
					result = index;
				index++;
			}
		}
		return( result );
	}

	@Override
	public void newItemSelected( String newItem )
	{
		changeSelectedItemList( newItem );
	}

	@Override
	public void addItem( String newItem )
	{
		changeSelectedItemList( newItem );
	}

	@Override
	public void removeItem( String item )
	{
		removeItem_internal( item );
	}
}

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

import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.Collection;
import java.util.List;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import java.util.Arrays;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FixedTextComboBoxContent implements TextComboBoxContent
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected List<String> _listOfItems = null;
	protected String[] _elementsForCombo = null;

	// function for DefaultConstructorInitCopier
	public FixedTextComboBoxContent( )
	{
		
	}

	public FixedTextComboBoxContent( String[] elementsForCombo )
	{
		_elementsForCombo = elementsForCombo;
	}

	// function for DefaultConstructorInitCopier
	public void init( FixedTextComboBoxContent that )
	{
		_elementsForCombo = _copier.copyArray( String[].class, that._elementsForCombo);
		_listOfItems = _copier.copyCollection(that._listOfItems);
	}

	public void init()
	{
		_listOfItems = Arrays.asList(_elementsForCombo);
	}

	@Override
	public boolean contains( String value )
	{
		return( _listOfItems.contains( value ) );
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

	@Override
	public void newItemSelected( String newItem )
	{
	}

	@Override
	public void addItem( String newItem )
	{
		_listOfItems.add( newItem );
		_elementsForCombo = null;
	}

	@Override
	public void removeItem( String item )
	{
		if( _listOfItems.remove( item ) )
			_elementsForCombo = null;
	}
}

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
package com.frojasg1.chesspdfbrowser.model.regex;

import com.frojasg1.general.combohistory.impl.TextComboBoxHistory;
import com.frojasg1.general.listeners.map.MapChangeListener;
import com.frojasg1.general.listeners.map.MapChangeObserved;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextComboBoxHistoryCompletionServer extends TextComboBoxHistory
	 implements MapChangeObserved< String, String >
{
	protected MapChangeListener<String, String> _mapChangeListener = null;

	// function for DefaultConstructorInitCopier
	public TextComboBoxHistoryCompletionServer()
	{
		super();
	}

	// function for DefaultConstructorInitCopier
	public void init( TextComboBoxHistoryCompletionServer that )
	{
		super.init( (TextComboBoxHistory) that );

		_mapChangeListener = that._mapChangeListener;
	}


	@Override
	public void addItem( String newItem )
	{
		super.addItem( newItem );

		if( _mapChangeListener != null )
			_mapChangeListener.elementPut(this, newItem, newItem);
	}

	@Override
	public void removeItem( String item )
	{
		super.removeItem( item );

		if( _mapChangeListener != null )
			_mapChangeListener.elementRemoved(this, item );
	}

	@Override
	public void addListenerGen(MapChangeListener<String, String> listener) {
		_mapChangeListener = listener;
	}

	@Override
	public void removeListenerGen(MapChangeListener<String, String> listener) {
		if( _mapChangeListener == listener )
			_mapChangeListener = null;
	}
}

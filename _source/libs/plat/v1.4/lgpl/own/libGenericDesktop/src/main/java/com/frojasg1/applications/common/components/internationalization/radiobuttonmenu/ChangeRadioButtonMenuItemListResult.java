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
package com.frojasg1.applications.common.components.internationalization.radiobuttonmenu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChangeRadioButtonMenuItemListResult
{
	protected JPopupMenu _popupMenu = null;
	protected Map< Component, Component > _oldElementsMap = new HashMap<>();
	protected Collection< Component > _newElementsCol = new ArrayList<>();

	public ChangeRadioButtonMenuItemListResult( JPopupMenu popupMenu,
												Map<String, Component> oldList,
												Map<String, Component> newList )
	{
		_popupMenu = popupMenu;
		updateResult( oldList, newList );
	}

	public JPopupMenu getPopupMenu()
	{
		return( _popupMenu );
	}
	
	public Map< Component, Component > getMapOfOldElements()
	{
		return( _oldElementsMap );
	}

	public Collection<Component> getColOfNewElements()
	{
		return( _newElementsCol );
	}

	protected void updateResult( Map<String, Component> oldMap,
								Map<String, Component> newMap )
	{
		Iterator< Map.Entry< String, Component > > it = oldMap.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry<String, Component> entry = it.next();
			_oldElementsMap.put( entry.getValue(), newMap.get( entry.getKey() ) );	// can be null
		}

		for( int ii=1; ; ii++ )
		{
			Component comp = oldMap.get( "Separator" + ii );
			if( comp == null )
				break;
			_oldElementsMap.put( comp, null );
		}

		it = newMap.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry<String, Component> entry = it.next();

			if( oldMap.get( entry.getKey() ) == null )
				_newElementsCol.add( entry.getValue() );
		}

		for( int ii=1; ; ii++ )
		{
			Component comp = newMap.get( "Separator" + ii );
			if( comp == null )
				break;
			_newElementsCol.add( comp );
		}
	}
}

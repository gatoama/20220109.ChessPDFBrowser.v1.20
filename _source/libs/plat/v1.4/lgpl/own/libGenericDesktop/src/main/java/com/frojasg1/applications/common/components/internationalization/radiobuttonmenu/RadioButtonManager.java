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

import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RadioButtonManager implements ItemListener, InternallyMappedComponent
{
	protected Map< ButtonGroup, RadioButtonManagerInstance > _map = new HashMap<>();

	protected boolean _alreadyMapped = false;

	public RadioButtonManager()
	{}

	public void add( RadioButtonManagerInstance rbmi )
	{
		_map.put( rbmi.getButtonGroup(), rbmi );

		rbmi.setListener( this );
		rbmi.setRadioButtonSubmenu();
	}

	public void updateRadioButtonMenus()
	{
		Iterator<RadioButtonManagerInstance> it = _map.values().iterator();
		while( it.hasNext() )
			it.next().setRadioButtonSubmenu();
	}

	public ChangeRadioButtonMenuItemListResult addItem( ButtonGroup bg, String newItem )
	{
		ChangeRadioButtonMenuItemListResult result = null;

		RadioButtonManagerInstance elem = _map.get( bg );
		
		if( elem != null )
			result = elem.addItemToMenu( newItem );

		return( result );
	}

	public void updateSelectionInMenu( ButtonGroup bg )
	{
		RadioButtonManagerInstance elem = _map.get( bg );
		
		if( elem != null )
			elem.updateSelectionInMenu();
	}
	
	public void setSelectedItem( ButtonGroup bg, String newSelectedItem )
	{
		RadioButtonManagerInstance elem = _map.get( bg );
		
		if( elem != null )
			elem.setSelectionInMenu( newSelectedItem );
	}

	@Override
	public void itemStateChanged(ItemEvent evt)
	{
		if( evt.getItem() instanceof JRadioButtonMenuItem )
		{
			JRadioButtonMenuItem btn = (JRadioButtonMenuItem) evt.getItem();

			ButtonModel bm = btn.getModel();
			if( bm instanceof DefaultButtonModel )
			{
				RadioButtonManagerInterface rbm = _map.get( ( (DefaultButtonModel ) bm ).getGroup() );
				if( rbm != null )
					rbm.distpatchEvent(evt);
			}
		}
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		for( Map.Entry< ButtonGroup, RadioButtonManagerInstance > entry: _map.entrySet() )
		{
			RadioButtonManagerInstance rbmi = entry.getValue();
			rbmi.setMenu( mapper.mapComponent(rbmi.getMenu()) );
		}

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}
}

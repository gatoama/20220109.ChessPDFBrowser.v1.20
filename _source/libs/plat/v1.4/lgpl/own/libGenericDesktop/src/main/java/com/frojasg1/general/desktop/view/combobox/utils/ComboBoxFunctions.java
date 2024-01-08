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
package com.frojasg1.general.desktop.view.combobox.utils;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.desktop.view.combobox.JComboBoxContainer;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComboBoxFunctions
{
	protected static ComboBoxFunctions _instance;

	public static void changeInstance( ComboBoxFunctions instance )
	{
		_instance = instance;
	}

	public static ComboBoxFunctions instance()
	{
		if( _instance == null )
			_instance = new ComboBoxFunctions();
		
		return( _instance );
	}

	public <CC> void fillComboBoxGen( JComboBox combo, CC selection, CC ... elements )
	{
		int selectedIndex = -1;

		if( selection == null )
			selection = (CC) combo.getSelectedItem();

		selectedIndex = ArrayFunctions.instance().getFirstIndexOfEquals( elements, selection );

		combo.setModel(new javax.swing.DefaultComboBoxModel(elements));

//		if( selectedIndex >= 0 ) combo.setSelectedIndex(selectedIndex);
		combo.setSelectedIndex(selectedIndex);
	}

	public void fillComboBox( JComboBox combo, String[] elements, String selection )
	{
		fillComboBoxGen( combo, selection, elements );
	}
/*
	public void fillComboBox( JComboBox combo, String[] elements, String selection )
	{
		int selectedIndex = -1;

		if( selection == null )
			selection = (String) combo.getSelectedItem();

		selectedIndex = ArrayFunctions.instance().getFirstIndexOfEquals( elements, selection );

		combo.setModel(new javax.swing.DefaultComboBoxModel(elements));

//		if( selectedIndex >= 0 ) combo.setSelectedIndex(selectedIndex);
		combo.setSelectedIndex(selectedIndex);
	}
*/
	public JPopupMenu getComboPopup( JComboBox combo )
	{
		return( (JPopupMenu) combo.getUI().getAccessibleChild( combo, 0) );
	}

	public <VV> List<VV> getListOfItems( JComboBox<VV> combo, Class<VV> clazz )
	{
		List<VV> result = new ArrayList<>();
		if( ( combo != null ) && ( combo.getItemCount() > 0 ) )
		{
			for( int ii=0; ii<combo.getItemCount(); ii++ )
				result.add( combo.getItemAt(ii) );
		}
		return( result );
	}

	public class BasicJComboBoxContainer implements JComboBoxContainer, InternallyMappedComponent
	{
		protected List<JComboBox> _comboList = new ArrayList<>();

		protected boolean _alreadyMapped = false;

		@Override
		public List<JComboBox> getComboBoxList()
		{
			return( _comboList );
		}

		@Override
		public void updateCombos()
		{
			for( JComboBox combo: _comboList )
				combo.repaint();
		}

		public void addCombo( JComboBox combo )
		{
			_comboList.add( combo );
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper)
		{
			List<JComboBox> result = new ArrayList<>();

			for( JComboBox combo: _comboList )
				result.add( mapper.mapComponent(combo) );

			_comboList = result;

			_alreadyMapped = true;
		}

		@Override
		public boolean hasBeenAlreadyMapped()
		{
			return( _alreadyMapped );
		}
	}
}

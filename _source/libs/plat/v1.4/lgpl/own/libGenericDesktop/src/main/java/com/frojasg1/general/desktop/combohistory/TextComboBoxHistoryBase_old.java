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
package com.frojasg1.general.desktop.combohistory;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxSelectionChangedListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedChildComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class TextComboBoxHistoryBase_old implements ChainedChildComboBoxGroupManager,
											ChainedParentComboBoxGroupManager, ActionListener
//public abstract class TextComboBoxHistoryBase_old<CC> implements ChainedChildComboBoxGroupManager<CC>,
//											ChainedParentComboBoxGroupManager<CC>, ActionListener
{
/*
	protected List<JComboBox> _comboList = new ArrayList<>();
	protected Vector<String> _vectorOfItems = null;

	protected boolean _modifiedByProgram = false;
	protected Integer _maxItemsToSave = null;

	protected ParameterListConfiguration _conf = null;

	protected List<ChainedChildComboBoxGroupManager<CC>> _childrenList = null;

	protected List<ComboBoxSelectionChangedListener> _selectionChangedListeners = null;

	protected String _selectedItem = null;

	public TextComboBoxHistoryBase_old( Integer maxItemsToSave,
								ParameterListConfiguration conf )
	{
		this( maxItemsToSave );

		_conf = conf;
	}

	public TextComboBoxHistoryBase_old( Integer maxItemsToSave )
	{
		_maxItemsToSave = maxItemsToSave;
	}

	public void init()
	{
		_vectorOfItems = new Vector<String> ();
	}

	public List<JComboBox> getComboList()
	{
		return( CollectionFunctions.instance().copyLimitingNumElems( _comboList, null ) );
	}

	public boolean contains( CC value )
	{
		return( _vectorOfItems.contains( value ) );
	}

	public void addSelectionChangedListener( ComboBoxSelectionChangedListener selectionChangedListener )
	{
		_selectionChangedListeners.add( selectionChangedListener );
	}

	public List<ComboBoxSelectionChangedListener> getSelectionChangedListeners( )
	{
		return( _selectionChangedListeners );
	}

	@Override
	public void addCombo( JComboBox combo )
	{
		if( !_comboList.contains(combo) )
		{
			ListCellRenderer renderer = createRendererForCombos(combo);
			if( renderer != null )
				combo.setRenderer(renderer);

			_comboList.add( combo );
			combo.addActionListener( this );
			updateComboBox( true, combo, getElementsForCombo() );
		}
	}

	protected List<String> loadItems_internal()
	{
		List<String> result = null;
		if( _conf != null )
			result = _conf.getList();

		return( result );
	}

	public void loadItems()
	{
		List<String> list = loadItems_internal();

		setCollectionOfItemsForCombo( list );
	}

	public void save()
	{
		if( _conf != null )
		{
			List<String> list = _vectorOfItems;

			if( ( _maxItemsToSave != null ) && ( _vectorOfItems.size() > _maxItemsToSave ) )
				list = CollectionFunctions.instance().copyLimitingNumElems(_vectorOfItems, _maxItemsToSave);

			_conf.setList(list);
		}
//		else
//			throw( new RuntimeException( "save function called and _conf is null. You must set _conf in the constructor or to override save function." ) );
	}

	public void setMaxItemsToSave( Integer maxItemsToSave )
	{
		_maxItemsToSave = maxItemsToSave;
	}

	public int getMaxItemsToSave()
	{
		return( _maxItemsToSave );
	}

	public void setCollectionOfItemsForCombo( Collection<String> listOfItems )
	{
		if( listOfItems != null )
		{
			_vectorOfItems.clear();
			_vectorOfItems.addAll( listOfItems );

			updateComboBoxes( false );
		}
	}

	protected ListCellRenderer createRendererForCombos( JComboBox combo )
	{
		return( null );
	}

	public Collection<String> getCollectionOfItems()
	{
		return( _vectorOfItems );
	}

	public void updateCombos()
	{
		_selectedItem = null;

		boolean setSelectedElement = true;
		updateComboBoxes( setSelectedElement );
	}

	protected String[] getElementsForCombo()
	{
		return( _vectorOfItems.toArray( new String[_vectorOfItems.size()] ) );
	}

	protected void updateComboBoxes( boolean setSelectedElement )
	{
		_modifiedByProgram = true;
		String[] elements = getElementsForCombo();

		for( JComboBox combo: _comboList )
		{
			updateComboBox( setSelectedElement, combo, elements );
		}

		_modifiedByProgram = false;
	}

	protected void updateComboBox( boolean setSelectedElement,
									JComboBox combo,
									String[] elements)
	{
		DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>( elements );
		combo.setModel( dcbm );

		if( setSelectedElement && ( _vectorOfItems.size() > 0 ) )
		{
			combo.setSelectedIndex(0);
		}
		else
		{
			combo.getEditor().setItem("");
		}
	}

	protected void removeItem_internal( String item )
	{
		int repeatedIndex = getRepeatedIndex(_vectorOfItems, item );

		if( repeatedIndex > 0 )
			_vectorOfItems.remove( repeatedIndex );
	}

	protected void changeSelectedItemVector( String item )
	{
		removeItem_internal( item );

		_vectorOfItems.insertElementAt(item, 0 );

		while( _vectorOfItems.size() > _maxItemsToSave )
			_vectorOfItems.remove( _maxItemsToSave );
	}

	protected int getRepeatedIndex( Vector<String> vector, String item )
	{
		int result = -1;
		if( item != null )
		{
			int index = 0;
			Iterator<String> it = vector.iterator();
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

	protected void updateComboBox_newItemSelected( String item )
	{
		changeSelectedItemVector( item );

		updateComboBoxes( true );
	}

	public String getSelectedItem()
	{
		return( _selectedItem );
	}

	public void newItemSelected( String newItem )
	{
		String previousSelectedItem = _selectedItem;

		if( !newItem.equals( previousSelectedItem )  )
		{
			_selectedItem = newItem;
			updateComboBox_newItemSelected( newItem );

			for( ComboBoxSelectionChangedListener listener: getSelectionChangedListeners() )
				listener.comboBoxSelectionChanged(null, previousSelectedItem, newItem);
		}
	}

	public void removeItem( String item )
	{
		removeItem_internal( item );

		updateComboBoxes( true );
	}

	@Override
	public boolean removeCombo( JComboBox combo )
	{
		boolean result = _comboList.remove(combo);
		if( result )
			combo.removeActionListener(this);

		return( result );
	}

	public void saveCurrentItem()
	{
		if( _comboList.size() != 1 )
			throw( new RuntimeException( "_comboList did not have exactly 1 item, so we cannot infer which combo to use." ) );

		saveCurrentItem( _comboList.get(0) );
	}

	protected void saveCurrentItem( JComboBox combo )
	{
		try
		{
			ComboBoxModel<String> dcbm = combo.getModel();

			String selectedItem = (String) dcbm.getSelectedItem();
			if( ( selectedItem != null ) &&
				!( ( _vectorOfItems.size() > 0 ) && _vectorOfItems.get(0).equals( selectedItem ) ) )
			{
				newItemSelected( selectedItem );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if( ! _modifiedByProgram )
		{
			saveCurrentItem( (JComboBox) e.getSource() );
		}
	}

	protected void removeListeners()
	{
		for( JComboBox combo: _comboList )
		{
			combo.removeActionListener(this);
		}
	}

	public void dispose()
	{
		removeListeners();
		_comboList.clear();
	}

	@Override
	public void setListOfItems(List<String> list)
	{
		setCollectionOfItemsForCombo(list);
	}

	@Override
	public abstract void setContainerOfItems(CC container);

	@Override
	public void comboBoxSelectionChanged(Object sender, String oldSelection, String newSelection)
	{
		newItemSelected( newSelection );
	}

	@Override
	public void addChainedChild(ChainedChildComboBoxGroupManager<CC> childManager)
	{
		_childrenList.add(childManager);
	}

	@Override
	public boolean removeChainedChild(ChainedChildComboBoxGroupManager<CC> childManager)
	{
		return( _childrenList.remove(childManager) );
	}


	@Override
	public List<ChainedChildComboBoxGroupManager<CC>> getChainedChildList()
	{
		return( CollectionFunctions.instance().copyLimitingNumElems(_childrenList, null) );
	}
*/
}

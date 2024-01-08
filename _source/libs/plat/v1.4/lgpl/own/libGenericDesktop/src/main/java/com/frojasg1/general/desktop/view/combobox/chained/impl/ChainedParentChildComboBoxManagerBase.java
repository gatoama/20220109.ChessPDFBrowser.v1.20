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
package com.frojasg1.general.desktop.view.combobox.chained.impl;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemNewSelectionController;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemResult;
import com.frojasg1.general.desktop.view.combobox.JComboBoxContainer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedChildComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import java.util.HashMap;
import java.util.Map;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentChildComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChainedParentChildComboBoxManagerBase
								implements ChainedParentChildComboBoxGroupManager,
											ActionListener
{
	protected AddRemoveModifyItemNewSelectionController _controller = null;

	protected ChainedParentForChildComboContentServer _contentServer = null;

	protected List<Component> _comboCompList = null;
	protected TextComboBoxContent _cbContents = null;

	protected boolean _modifiedByProgram = false;

	protected Map<String, ChainedChildComboBoxGroupManager> _childrenMap = null;

	protected String _key = null;

	protected String _selectedItem = null;

	protected ChainedParentComboBoxGroupManager _parent = null;

	protected long _lastUpdateTimestamp = 0;
	protected long _lastUpdateTimestamp2 = 0;

	protected boolean _alreadyMapped = false;

	public ChainedParentChildComboBoxManagerBase( String key, TextComboBoxContent contents,
						ChainedParentForChildComboContentServer contentServer )
	{
		this( key, contents, contentServer, null );
	}

	public ChainedParentChildComboBoxManagerBase( String key, TextComboBoxContent contents,
						ChainedParentForChildComboContentServer contentServer,
						ChainedParentComboBoxGroupManager parent )
	{
		_key = key;
		_cbContents = contents;
		_contentServer = contentServer;

		_parent = parent;
		if( _parent != null )
			_parent.addChainedChild( this );
	}

	public void init()
	{
		_comboCompList = new ArrayList<>();
		_childrenMap = new HashMap<>();
	}

	@Override
	public String getKey()
	{
		return( _key );
	}

	@Override
	public void setController( AddRemoveModifyItemNewSelectionController controller )
	{
		_controller = controller;
	}

	@Override
	public TextComboBoxContent getComboBoxContent()
	{
		return( _cbContents );
	}

	@Override
	public List<Component> getComboCompList()
	{
		return( CollectionFunctions.instance().copyLimitingNumElems(_comboCompList, null ) );
	}

	protected boolean isCorrectComboComp( Component comboComp )
	{
		return( ( comboComp instanceof JComboBox ) || ( comboComp instanceof JComboBoxContainer ) );
	}

	@Override
	public void addComboComp( Component comboComp )
	{
		if( !_comboCompList.contains(comboComp) )
		{
			if( isCorrectComboComp( comboComp ) )
			{
				genericInvocationForCombo( comboComp, (combo) -> addCombo( combo ) );
				_comboCompList.add( comboComp );
			}
		}
	}

	protected void genericInvocationForComboList( ComboBoxInvoker invoker )
	{
		for( Component comboComp: _comboCompList )
			genericInvocationForCombo( comboComp, invoker );
	}

	protected void genericInvocationForCombo( Component comboComp, ComboBoxInvoker invoker )
	{
		if( comboComp instanceof JComboBox )
		{
			invoker.invoke( (JComboBox) comboComp );
		}
		else if( comboComp instanceof JComboBoxContainer )
		{
			JComboBoxContainer jcbc = (JComboBoxContainer) comboComp;
			for( JComboBox combo: jcbc.getComboBoxList() )
				invoker.invoke( combo );
		}
	}
/*
	@Override
	public void addListeners()
	{
		for( Component comboComp: _comboCompList )
		{
			genericInvocationForCombo( comboComp, (combo) -> combo.addActionListener( this ) );
		}
	}
*/
	protected void addCombo( JComboBox combo )
	{
		ListCellRenderer renderer = createRendererForCombos(combo);
		if( renderer != null )
			combo.setRenderer(renderer);

		_comboCompList.add( combo );
		combo.addActionListener( this );
		updateSingleComboBox( combo );
	}

	protected void updateSingleComboBox( JComboBox combo )
	{
		String[] elements = getElementsForCombo();
		String selectedItem = getSelectedItem( elements );

		updateComboBox( combo, elements, selectedItem );
	}

	protected ListCellRenderer createRendererForCombos( JComboBox combo )
	{
		return( null );
	}

	public Collection<String> getCollectionOfItems()
	{
		return( _cbContents.getListOfItems() );
	}

	@Override
	public void updateCombosKeepingSelection()
	{
		_lastUpdateTimestamp = 0;
		_lastUpdateTimestamp2 = 0;

		boolean setSelectedElement = false;
		updateCombos( setSelectedElement );
	}

	@Override
	public void updateCombos()
	{
		_lastUpdateTimestamp = 0;
		_lastUpdateTimestamp2 = 0;

		boolean setSelectedElement = true;
		updateCombos( setSelectedElement );
	}

	protected String getSelectedItem( String[] elements )
	{
		return( ArrayFunctions.instance().getFirst( elements ) );
	}

	protected boolean hasToUpdateCombos( String[] elements, String selectedItem )
	{
		AtomicBoolean result = new AtomicBoolean(false);

		genericInvocationForComboList( (combo) -> {
				if( !result.get() )
					result.set( !matchesModel( combo, elements, selectedItem ) );
			});

		return( result.get() );
	}

	protected boolean matchesModel( JComboBox combo, String[] elements, String selectedItem )
	{
		boolean result = false;

		result = ArrayFunctions.instance().equals( getArrayOfItems( combo ), elements );
		result = result && ( ( selectedItem == null ) || selectedItem.equals( combo.getSelectedItem() ) );

		return( result );
	}

	protected String[] getArrayOfItems( JComboBox<String> combo )
	{
		return( ComboBoxFunctions.instance().getListOfItems( combo, String.class ).toArray( ArrayFunctions.STRING_ARRAY ) );
	}

	protected void updateCombos(boolean setSelectedElement)
	{
//		_selectedItem = null;

		if( _comboCompList != null )
		{
			String[] elements = getElementsForCombo();

			if( elements != null )
			{
				String tmp = null;
				if( setSelectedElement )
					tmp = getSelectedItem( elements );

				String selectedItem = tmp;
				if( hasToUpdateCombos( elements, selectedItem ) )
				{
					_modifiedByProgram = true;
					genericInvocationForComboList( (combo) -> updateComboBox( combo, elements, selectedItem ) );

					if( ( selectedItem == null ) && ( selectedItem != _selectedItem ) )
						newItemSelected( null );

					_modifiedByProgram = false;
				}
			}
		}
	}

	protected String[] getElementsForCombo()
	{
		String[] result = null;
		if( _cbContents != null )
			result = _cbContents.getElementsForCombo();

		return( result );
	}

	protected void updateComboBox( JComboBox combo,
									String[] elements,
									String selectedItem)
	{
		if( selectedItem == null )
			selectedItem = (String) combo.getSelectedItem();

		DefaultComboBoxModel<String> dcbm = null;
		
		if( elements != null )
			dcbm = new DefaultComboBoxModel<String>( elements );
		else
			dcbm = new DefaultComboBoxModel<String>();

		combo.setModel( dcbm );

		combo.setSelectedItem( selectedItem );
/*
		if( setSelectedElement && (_cbContents != null ) && !_cbContents.getListOfItems().isEmpty() )
		{
			combo.setSelectedIndex(0);
		}
		else
		{
//			combo.getEditor().setItem("");
			combo.setSelectedIndex(-1);
		}
*/
	}

	protected void updateComboBox_newItemSelected( String item )
	{
		_cbContents.newItemSelected( item );

		updateCombos( true );
	}

	@Override
	public String getSelectedItem()
	{
		return( _selectedItem );
	}

	public void newItemSelected( String newItem )
	{
		String previousSelectedItem = _selectedItem;
		_selectedItem = newItem;

		long now = System.currentTimeMillis();
		if( ( now -_lastUpdateTimestamp > 2000 ) ||
			!Objects.equals( newItem, previousSelectedItem )  )
		{
			_lastUpdateTimestamp = now;

			_modifiedByProgram = true;
			updateComboBox_newItemSelected( newItem );
			_modifiedByProgram = false;

			if( _controller != null )
				_controller.comboBoxSelectionChanged(this, previousSelectedItem, newItem);

			List<String> chainedNewItem = createChainedNewItem( newItem );

			changeChildren( chainedNewItem );
		}
	}

	protected  void addParentSelection( ChainedChildComboBoxGroupManager current, List<String> result )
	{
		ChainedParentComboBoxGroupManager parent = current.getParent();
		if( parent != null )
		{
			if( parent instanceof ChainedChildComboBoxGroupManager )
				addParentSelection( (ChainedChildComboBoxGroupManager) parent, result );

			result.add( parent.getSelectedItem() );
		}
	}

	protected List<String> createChainedNewItem( String newItem )
	{
		List<String> result = new ArrayList<>();

		addParentSelection( this, result );
		result.add( newItem );

		return( result );
	}

	protected void changeChildren( List<String> chainedNewItem )
	{
		if( _contentServer != null )
		{
			for( Map.Entry<String, ChainedChildComboBoxGroupManager > entry: _childrenMap.entrySet() )
			{
				TextComboBoxContent cbContent = _contentServer.getContentForChildCombos( entry.getKey(), chainedNewItem );
				if( cbContent != null )
				{
					ChainedChildComboBoxGroupManager child = entry.getValue();
					child.setComboBoxContent(cbContent);
					child.updateCombos();
				}
			}
		}
	}

	public void removeItem( String item )
	{
		_cbContents.removeItem(item );

		updateCombos( true );
	}

	public void addItem( String item )
	{
		_cbContents.addItem(item );

		updateCombos( true );
	}

	@Override
	public boolean removeComboComp( Component comboComp )
	{
		boolean result = _comboCompList.remove(comboComp);
		if( result )
			removeActionListener( comboComp );

		return( result );
	}
/*
	public void saveCurrentItem()
	{
		if( _comboCompList.size() != 1 )
			throw( new RuntimeException( "_comboList did not have exactly 1 item, so we cannot infer which combo to use." ) );

		saveCurrentItem(_comboCompList.get(0) );
	}
*/
	protected void removeActionListener( Component comboComp )
	{
		genericInvocationForCombo( comboComp, (combo) -> combo.removeActionListener(this) );
	}

	protected void saveCurrentItem( JComboBox combo )
	{
		try
		{
			ComboBoxModel<String> dcbm = combo.getModel();

			String selectedItem = (String) dcbm.getSelectedItem();
			if( selectedItem != null )
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
		long now = System.currentTimeMillis();
		if( ( now - _lastUpdateTimestamp2 > 2000 ) || ! _modifiedByProgram )
		{
			_lastUpdateTimestamp2 = now;
			saveCurrentItem( (JComboBox) e.getSource() );
		}
	}

	protected void removeListeners()
	{
		for( Component comboComp: _comboCompList )
			removeActionListener( comboComp );
	}

	@Override
	public void dispose()
	{
		removeListeners();
		_comboCompList.clear();
	}

	@Override
	public void setListOfItems(List<String> list)
	{
		_cbContents.setColectionOfItems(list);;
	}

	@Override
	public void setComboBoxContent(TextComboBoxContent comboBoxContents)
	{
		if( _cbContents != comboBoxContents )
		{
			_cbContents = comboBoxContents;
			updateCombos();
		}
	}

	@Override
	public void addChainedChild( ChainedChildComboBoxGroupManager childManager )
	{
		if( childManager != null )
			_childrenMap.put( childManager.getKey(), childManager );
	}

	@Override
	public boolean removeChainedChild( String key )
	{
		return( _childrenMap.remove(key) != null );
	}

	@Override
	public Map<String, ChainedChildComboBoxGroupManager> getChainedChildMap()
	{
		return( _childrenMap );
	}

	@Override
	public void setContentServerForChildren(ChainedParentForChildComboContentServer contentServer) {
		_contentServer = contentServer;
	}

	@Override
	public ChainedParentForChildComboContentServer getContentServerForChildren() {
		return( _contentServer );
	}

	@Override
	public void added(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
						Consumer<String> callback)
	{
		String result = null;
		if( _controller != null )
		{
			SwingUtilities.invokeLater( () -> {
				_controller.added( this, eventData, (str) -> itemAdded(str, callback) );
			} );
		}
	}

	protected void itemAdded( String addedItem, Consumer<String> callback )
	{
		if( addedItem != null )
			addItem( addedItem );

		if( callback != null )
			callback.accept(addedItem);
	}

	@Override
	public void removed(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
							Consumer<String> callback)
	{
		String result = null;
		if( _controller != null )
		{
			SwingUtilities.invokeLater( () -> {
				_controller.removed( this, eventData,
									(str) -> itemRemoved( str, callback ) );
			} );
		}
	}

	protected void itemRemoved( String addedItem, Consumer<String> callback )
	{
		if( addedItem != null )
			removeItem( addedItem );

		if( callback != null )
			callback.accept(addedItem);
	}

	@Override
	public void modify(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData)
	{
		if( _controller != null )
		{
			_controller.modify( this, eventData );
		}
	}

	@Override
	public void comboBoxSelectionChanged(ComboBoxGroupManager sender, String previousSelectedItem, String newSelection)
	{
		if( _controller != null )
		{
			_controller.comboBoxSelectionChanged( this, previousSelectedItem, newSelection );
		}
	}

	@Override
	public void saveCurrentItem()
	{
		saveCurrentItem( getFirstCombo() );
	}

	protected JComboBox getFirstCombo()
	{
		return( getFirstCombo( getFirstOf(_comboCompList) ) );
	}

	protected JComboBox getFirstCombo( Component comboComp )
	{
		JComboBox result = null;
		if( comboComp instanceof JComboBox )
			result = (JComboBox) comboComp;
		else if( comboComp instanceof JComboBoxContainer )
			result = getFirstComboOfContainer( (JComboBoxContainer) comboComp );

		return( result );
	}

	protected JComboBox getFirstComboOfContainer( JComboBoxContainer comboCont )
	{
		JComboBox result = getFirstOf(comboCont.getComboBoxList());

		return( result );
	}

	protected <CC> CC getFirstOf( List<CC> list )
	{
		return( CollectionFunctions.instance().getFirstOf( list ) );
	}

	public ChainedParentComboBoxGroupManager getParent()
	{
		return( _parent );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		genericInvocationForComboList( (combo) -> replaceRenderer( combo, mapper ) );

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	protected void replaceRenderer( JComboBox combo, ComponentMapper mapper )
	{
		ListCellRenderer renderer = combo.getRenderer();
		if( renderer instanceof InternallyMappedComponent )
			( (InternallyMappedComponent) renderer ).setComponentMapper(mapper);
	}

	protected static interface ComboBoxInvoker
	{
		public void invoke( JComboBox combo );
	}
}

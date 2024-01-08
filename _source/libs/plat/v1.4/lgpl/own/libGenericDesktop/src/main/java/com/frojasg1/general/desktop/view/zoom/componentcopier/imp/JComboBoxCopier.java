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
package com.frojasg1.general.desktop.view.zoom.componentcopier.imp;

import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopierBase;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JComboBoxCopier extends CompCopierBase<JComboBox>
{

	@Override
	protected List<CompCopier<JComboBox>> createCopiers() {

		List<CompCopier<JComboBox>> result = new ArrayList<>();

		result.add( createEditableCopier() );
		result.add( createSelectedItemCopier() );
		result.add( createActionCopier() );
		result.add( createActionCommandCopier() );
		result.add( createActionListenersCopier() );
		result.add( createEditorCopier() );
		result.add( createItemListenersCopier() );
		result.add( createKeySelectionManagerCopier() );
		result.add( createMaximumRowCountCopier() );
		result.add( createModelCopier() );
		result.add( createPopupMenuListenersCopier() );
		result.add( createPrototypeDisplayValueCopier() );
		result.add( createRendererCopier() );
//		result.add( createSelectedObjectsCopier() );

		return( result );
	}

	protected CompCopier<JComboBox> createActionCopier()
	{
		return( (originalComponent, newComponent) -> copyAction( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createActionCommandCopier()
	{
		return( (originalComponent, newComponent) -> copyActionCommand( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createActionListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyActionListeners( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createEditorCopier()
	{
		return( (originalComponent, newComponent) -> copyEditor( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createItemListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyItemListeners( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createKeySelectionManagerCopier()
	{
		return( (originalComponent, newComponent) -> copyKeySelectionManager( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createMaximumRowCountCopier()
	{
		return( (originalComponent, newComponent) -> copyMaximumRowCount( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createModelCopier()
	{
		return( (originalComponent, newComponent) -> copyModel( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createPopupMenuListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyPopupMenuListeners( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createPrototypeDisplayValueCopier()
	{
		return( (originalComponent, newComponent) -> copyPrototypeDisplayValue( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createRendererCopier()
	{
		return( (originalComponent, newComponent) -> copyRenderer( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createSelectedItemCopier()
	{
		return( (originalComponent, newComponent) -> copySelectedItem( originalComponent, newComponent ) );
	}

	protected CompCopier<JComboBox> createEditableCopier()
	{
		return( (originalComponent, newComponent) -> copyEditable( originalComponent, newComponent ) );
	}
/*
	protected CompCopier<JComboBox> createSelectedObjectsCopier()
	{
		return( (originalComponent, newComponent) -> copySelectedObjects( originalComponent, newComponent ) );
	}
*/
	@Override
	public Class<JComboBox> getParameterClass() {
		return( JComboBox.class );
	}

	protected void copyAction( JComboBox originalComponent, JComboBox newComponent )
	{
		Action value = originalComponent.getAction();
		if( !isClassOfJdk( value ) )
			newComponent.setAction( value );
	}

	protected void copyActionCommand( JComboBox originalComponent, JComboBox newComponent )
	{
		String value = originalComponent.getActionCommand();
		newComponent.setActionCommand( value );
	}

	protected void copyActionListeners( JComboBox originalComponent, JComboBox newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ActionListener.class,
						(c) -> c.getActionListeners(),
						(c,l) -> c.addActionListener(l),
						(c,l) -> c.removeActionListener(l) );
	}

	protected void copyEditor( JComboBox originalComponent, JComboBox newComponent )
	{
		ComboBoxEditor value = originalComponent.getEditor();
		if( !isClassOfJdk( value ) )
			newComponent.setEditor( value );
	}

	protected void copyItemListeners( JComboBox originalComponent, JComboBox newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ItemListener.class,
						(c) -> c.getItemListeners(),
						(c,l) -> c.addItemListener(l),
						(c,l) -> c.removeItemListener(l) );
	}

	protected void copyKeySelectionManager( JComboBox originalComponent, JComboBox newComponent )
	{
		KeySelectionManager value = originalComponent.getKeySelectionManager();
		if( !isClassOfJdk( value ) )
			newComponent.setKeySelectionManager( value );
	}

	protected void copyMaximumRowCount( JComboBox originalComponent, JComboBox newComponent )
	{
		int value = originalComponent.getMaximumRowCount();
		newComponent.setMaximumRowCount( value );
	}

	protected void copyModel( JComboBox originalComponent, JComboBox newComponent )
	{
		ComboBoxModel value = originalComponent.getModel();
//		if( !isInnerClass( value ) ) // for example, FileChooser, needs it.
			newComponent.setModel( value );
	}

	protected void copyPopupMenuListeners( JComboBox originalComponent, JComboBox newComponent )
	{
		copyListeners( originalComponent, newComponent,
						PopupMenuListener.class,
						(c) -> c.getPopupMenuListeners(),
						(c,l) -> c.addPopupMenuListener(l),
						(c,l) -> c.removePopupMenuListener(l) );
	}

	protected void copyPrototypeDisplayValue( JComboBox originalComponent, JComboBox newComponent )
	{
		Object value = originalComponent.getPrototypeDisplayValue();
		if( !isClassOfJdk( value ) )
			newComponent.setPrototypeDisplayValue( value );
	}

	protected void copyRenderer( JComboBox originalComponent, JComboBox newComponent )
	{
		ListCellRenderer value = originalComponent.getRenderer();
		if( !isClassOfJdk( value ) )
			newComponent.setRenderer( value );
	}

	protected void copySelectedItem( JComboBox originalComponent, JComboBox newComponent )
	{
		Object value = originalComponent.getSelectedItem();
		if( !isClassOfJdk( value ) )
			newComponent.setSelectedItem( value );
	}

	protected void copyEditable( JComboBox originalComponent, JComboBox newComponent )
	{
		boolean value = originalComponent.isEditable();
		newComponent.setEditable( value );
	}
/*
	protected void copySelectedObjects( JComboBox originalComponent, JComboBox newComponent )
	{
		Object[] array = originalComponent.getSelectedObjects();
		
		newComponent.setSelectedItem( );
	}
*/
}

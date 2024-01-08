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
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTextComponentCopier extends CompCopierBase<JTextComponent>
{

	@Override
	protected List<CompCopier<JTextComponent>> createCopiers() {

		List<CompCopier<JTextComponent>> result = new ArrayList<>();

		result.add( createCaretCopier() );
		result.add( createCaretColorCopier() );
		result.add( createCaretListenersListCopier() );
		result.add( createDisabledTextColorCopier() );
		result.add( createDocumentCopier() );
		result.add( createDragEnabledCopier() );
		result.add( createDropModeCopier() );
		result.add( createFocusAcceleratorCopier() );
		result.add( createHighlighterCopier() );
		result.add( createKeymapCopier() );
		result.add( createMarginCopier() );
		result.add( createNavigationFilterCopier() );
		result.add( createSelectedTextColorCopier() );
		result.add( createSelectionColorCopier() );
		result.add( createTextCopier() );
		result.add( createEditableCopier() );

		return( result );
	}

	protected CompCopier<JTextComponent> createCaretCopier()
	{
		return( (originalComponent, newComponent) -> copyCaret( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createCaretColorCopier()
	{
		return( (originalComponent, newComponent) -> copyCaretColor( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createCaretListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyCaretListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createDisabledTextColorCopier()
	{
		return( (originalComponent, newComponent) -> copyDisabledTextColor( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createDocumentCopier()
	{
		return( (originalComponent, newComponent) -> copyDocument( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createDragEnabledCopier()
	{
		return( (originalComponent, newComponent) -> copyDragEnabled( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createDropModeCopier()
	{
		return( (originalComponent, newComponent) -> copyDropMode( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createFocusAcceleratorCopier()
	{
		return( (originalComponent, newComponent) -> copyFocusAccelerator( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createHighlighterCopier()
	{
		return( (originalComponent, newComponent) -> copyHighlighter( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createKeymapCopier()
	{
		return( (originalComponent, newComponent) -> copyKeymap( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createMarginCopier()
	{
		return( (originalComponent, newComponent) -> copyMargin( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createNavigationFilterCopier()
	{
		return( (originalComponent, newComponent) -> copyNavigationFilter( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createSelectedTextColorCopier()
	{
		return( (originalComponent, newComponent) -> copySelectedTextColor( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createSelectionColorCopier()
	{
		return( (originalComponent, newComponent) -> copySelectionColor( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createTextCopier()
	{
		return( (originalComponent, newComponent) -> copyText( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextComponent> createEditableCopier()
	{
		return( (originalComponent, newComponent) -> copyEditable( originalComponent, newComponent ) );
	}

	@Override
	public Class<JTextComponent> getParameterClass() {
		return( JTextComponent.class );
	}

	protected void copyCaret( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setCaret( originalComponent.getCaret() );
	}

	protected void copyCaretColor( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setCaretColor( originalComponent.getCaretColor() );
	}

	protected void copyCaretListenersList( JTextComponent originalComponent, JTextComponent newComponent )
	{
		copyListeners( originalComponent, newComponent,
						CaretListener.class,
						(c) -> c.getCaretListeners(),
						(c,l) -> c.addCaretListener(l),
						(c,l) -> c.removeCaretListener(l) );
	}

	protected void copyDisabledTextColor( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setDisabledTextColor( originalComponent.getDisabledTextColor() );
	}

	protected void copyDocument( JTextComponent originalComponent, JTextComponent newComponent )
	{
		Document docu = originalComponent.getDocument();
		if( !isClassOfJdk( docu ) )
			newComponent.setDocument( docu );
	}

	protected void copyDragEnabled( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setDragEnabled( originalComponent.getDragEnabled() );
	}

	protected void copyDropMode( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setDropMode( originalComponent.getDropMode() );
	}

	protected void copyFocusAccelerator( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setFocusAccelerator( originalComponent.getFocusAccelerator() );
	}

	protected void copyHighlighter( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setHighlighter( originalComponent.getHighlighter() );
	}

	protected void copyKeymap( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setKeymap( originalComponent.getKeymap() );
	}

	protected void copyMargin( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setMargin( originalComponent.getMargin() );
	}

	protected void copyNavigationFilter( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setNavigationFilter( originalComponent.getNavigationFilter() );
	}

	protected void copySelectedTextColor( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setSelectedTextColor( originalComponent.getSelectedTextColor() );
	}

	protected void copySelectionColor( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setSelectionColor( originalComponent.getSelectionColor() );
	}

	protected void copyText( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setText( originalComponent.getText() );
	}

	protected void copyEditable( JTextComponent originalComponent, JTextComponent newComponent )
	{
		newComponent.setEditable( originalComponent.isEditable() );
	}
}

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
import java.awt.event.ComponentListener;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JComponentCopier extends CompCopierBase<JComponent>
{

	@Override
	protected List<CompCopier<JComponent>> createCopiers() {

		List<CompCopier<JComponent>> result = new ArrayList<>();

		result.add( createAncestorListenerListCopier() );
		result.add( createAlignmentXCopier() );
		result.add( createAlignmentYCopier() );
		result.add( createBorderCopier() );
		result.add( createComponentPopupMenuCopier() );
		result.add( createInheritsPopupMenuCopier() );
//		result.add( createInsetsCopier() );
//		result.add( createRegisteredKeystrokesCopier() );
		result.add( createRequestFocusEnabledCopier() );
		result.add( createVetoableChangeListenerListCopier() );
		result.add( createOpaqueCopier() );
		result.add( createAutoscrollsCopier() );

		return( result );
	}

	protected CompCopier<JComponent> createAncestorListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyAncestorListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createVetoableChangeListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyVetoableChangeListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createAlignmentXCopier()
	{
		return( (originalComponent, newComponent) -> copyAlignmentX( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createAlignmentYCopier()
	{
		return( (originalComponent, newComponent) -> copyAlignmentY( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createBorderCopier()
	{
		return( (originalComponent, newComponent) -> copyBorder( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createComponentPopupMenuCopier()
	{
		return( (originalComponent, newComponent) -> copyComponentPopupMenu( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createInheritsPopupMenuCopier()
	{
		return( (originalComponent, newComponent) -> copyInheritsPopupMenu( originalComponent, newComponent ) );
	}
/*
	protected CompCopier<JComponent> createInsetsCopier()
	{
		CompCopier<JComponent> result = new CompCopier<JComponent>() {
			@Override
			public void copy(JComponent originalComponent, JComponent newComponent) {
				copyInsets( originalComponent, newComponent );
			}
		};

		return( result );
	}
*/
	protected CompCopier<JComponent> createRequestFocusEnabledCopier()
	{
		return( (originalComponent, newComponent) -> copyRequestFocusEnabled( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createOpaqueCopier()
	{
		return( (originalComponent, newComponent) -> copyOpaque( originalComponent, newComponent ) );
	}

	protected CompCopier<JComponent> createAutoscrollsCopier()
	{
		return( (originalComponent, newComponent) -> copyAutoscrolls( originalComponent, newComponent ) );
	}

	@Override
	public Class<JComponent> getParameterClass() {
		return( JComponent.class );
	}

	protected void copyAncestorListenerList( JComponent originalComponent, JComponent newComponent )
	{
		copyListeners( originalComponent, newComponent,
						AncestorListener.class,
						(c) -> c.getAncestorListeners(),
						(c,l) -> c.addAncestorListener(l),
						(c,l) -> c.removeAncestorListener(l) );
	}

	protected void copyVetoableChangeListenerList( JComponent originalComponent, JComponent newComponent )
	{
		copyListeners( originalComponent, newComponent,
						VetoableChangeListener.class,
						(c) -> c.getVetoableChangeListeners(),
						(c,l) -> c.addVetoableChangeListener(l),
						(c,l) -> c.removeVetoableChangeListener(l) );
	}

	protected void copyAlignmentX( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setAlignmentX( originalComponent.getAlignmentX() );
	}

	protected void copyAlignmentY( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setAlignmentY( originalComponent.getAlignmentY() );
	}

	protected void copyBorder( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setBorder( originalComponent.getBorder() );
	}

	protected void copyComponentPopupMenu( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setComponentPopupMenu( originalComponent.getComponentPopupMenu() );
	}

	protected void copyInheritsPopupMenu( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setInheritsPopupMenu( originalComponent.getInheritsPopupMenu() );
	}
/*
	protected void copyInsets( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setInsets( originalComponent.getInsets() );
	}
*/
	protected void copyRequestFocusEnabled( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setRequestFocusEnabled( originalComponent.isRequestFocusEnabled() );
	}

	protected void copyOpaque( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setOpaque( originalComponent.isOpaque() );
	}

	protected void copyAutoscrolls( JComponent originalComponent, JComponent newComponent )
	{
		newComponent.setAutoscrolls( originalComponent.getAutoscrolls() );
	}
}

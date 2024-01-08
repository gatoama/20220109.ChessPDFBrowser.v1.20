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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTextFieldCopier extends CompCopierBase<JTextField>
{

	@Override
	protected List<CompCopier<JTextField>> createCopiers() {

		List<CompCopier<JTextField>> result = new ArrayList<>();

		result.add( createActionListenersListCopier() );
		result.add( createActionCopier() );
		result.add( createColumnsCopier() );
		result.add( createHorizontalAlignmentCopier() );
		result.add( createScrollOffsetCopier() );

		return( result );
	}

	protected CompCopier<JTextField> createActionListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyActionListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextField> createActionCopier()
	{
		return( (originalComponent, newComponent) -> copyAction( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextField> createColumnsCopier()
	{
		return( (originalComponent, newComponent) -> copyColumns( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextField> createHorizontalAlignmentCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalAlignment( originalComponent, newComponent ) );
	}

	protected CompCopier<JTextField> createScrollOffsetCopier()
	{
		return( (originalComponent, newComponent) -> copyScrollOffset( originalComponent, newComponent ) );
	}

	@Override
	public Class<JTextField> getParameterClass() {
		return( JTextField.class );
	}

	protected void copyActionListenersList( JTextField originalComponent, JTextField newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ActionListener.class,
						(c) -> c.getActionListeners(),
						(c,l) -> c.addActionListener(l),
						(c,l) -> c.removeActionListener(l) );
	}

	protected void copyAction( JTextField originalComponent, JTextField newComponent )
	{
		newComponent.setAction( originalComponent.getAction() );
	}

	protected void copyColumns( JTextField originalComponent, JTextField newComponent )
	{
		newComponent.setColumns( originalComponent.getColumns() );
	}

	protected void copyHorizontalAlignment( JTextField originalComponent, JTextField newComponent )
	{
		newComponent.setHorizontalAlignment( originalComponent.getHorizontalAlignment() );
	}

	protected void copyScrollOffset( JTextField originalComponent, JTextField newComponent )
	{
		newComponent.setScrollOffset( originalComponent.getScrollOffset() );
	}
}

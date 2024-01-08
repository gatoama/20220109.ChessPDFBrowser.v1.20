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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JMenuItemCopier extends CompCopierBase<JMenuItem>
{

	@Override
	protected List<CompCopier<JMenuItem>> createCopiers() {

		List<CompCopier<JMenuItem>> result = new ArrayList<>();

		result.add( createAcceleratorCopier() );
		result.add( createMenuDragMouseListenersCopier() );
		result.add( createMenuKeyListenersCopier() );
		result.add( createArmedCopier() );

		return( result );
	}

	protected CompCopier<JMenuItem> createAcceleratorCopier()
	{
		return( (originalComponent, newComponent) -> copyAccelerator( originalComponent, newComponent ) );
	}

	protected CompCopier<JMenuItem> createMenuDragMouseListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyMenuDragMouseListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<JMenuItem> createMenuKeyListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyMenuKeyListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<JMenuItem> createArmedCopier()
	{
		return( (originalComponent, newComponent) -> copyArmed( originalComponent, newComponent ) );
	}

	@Override
	public Class<JMenuItem> getParameterClass() {
		return( JMenuItem.class );
	}

	protected void copyAccelerator( JMenuItem originalComponent, JMenuItem newComponent )
	{
		if( !( newComponent instanceof JMenu ) )
			newComponent.setAccelerator( originalComponent.getAccelerator() );
	}

	protected void copyMenuDragMouseListenersList( JMenuItem originalComponent, JMenuItem newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MenuDragMouseListener.class,
						(c) -> c.getMenuDragMouseListeners(),
						(c,l) -> c.addMenuDragMouseListener(l),
						(c,l) -> c.removeMenuDragMouseListener(l) );
	}

	protected void copyMenuKeyListenersList( JMenuItem originalComponent, JMenuItem newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MenuKeyListener.class,
						(c) -> c.getMenuKeyListeners(),
						(c,l) -> c.addMenuKeyListener(l),
						(c,l) -> c.removeMenuKeyListener(l) );
	}

	protected void copyArmed( JMenuItem originalComponent, JMenuItem newComponent )
	{
		newComponent.setArmed( originalComponent.isArmed() );
	}
}

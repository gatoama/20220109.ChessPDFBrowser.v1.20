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
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.MenuListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JMenuCopier extends CompCopierBase<JMenu>
{

	@Override
	protected List<CompCopier<JMenu>> createCopiers() {

		List<CompCopier<JMenu>> result = new ArrayList<>();

		result.add( createDelayCopier() );
		result.add( createMenuListenersCopier() );
		result.add( createMenuComponentsCopier() );

		return( result );
	}

	protected CompCopier<JMenu> createDelayCopier()
	{
		return( (originalComponent, newComponent) -> copyDelay( originalComponent, newComponent ) );
	}

	protected CompCopier<JMenu> createMenuListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyMenuListeners( originalComponent, newComponent ) );
	}

	protected CompCopier<JMenu> createMenuComponentsCopier()
	{
		return( (originalComponent, newComponent) -> copyMenuComponents( originalComponent, newComponent ) );
	}

	@Override
	public Class<JMenu> getParameterClass() {
		return( JMenu.class );
	}

	protected void copyDelay( JMenu originalComponent, JMenu newComponent )
	{
		newComponent.setDelay( originalComponent.getDelay() );
	}

	protected void copyMenuListeners( JMenu originalComponent, JMenu newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MenuListener.class,
						(c) -> c.getMenuListeners(),
						(c,l) -> c.addMenuListener(l),
						(c,l) -> c.removeMenuListener(l) );
	}

	protected void copyMenuComponents( JMenu originalComponent, JMenu newComponent )
	{
		Component[] arr = originalComponent.getMenuComponents();
		for( Component comp: arr )
		{
			if( comp instanceof JMenuItem )
			{
				JMenuItem mi = (JMenuItem) comp;
				originalComponent.remove( mi );
				newComponent.add( mi );
			}
			else
			{
				originalComponent.remove( comp );
				newComponent.add( comp );
			}
		}
	}

}

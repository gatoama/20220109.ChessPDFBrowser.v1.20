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
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentCopier extends CompCopierBase<Component>
{

	@Override
	protected List<CompCopier<Component>> createCopiers() {

		List<CompCopier<Component>> result = new ArrayList<>();

		result.add( createBackgroundCopier() );
		result.add( createComponentListenerListCopier() );
		result.add( createFocusListenerListCopier() );
		result.add( createHierarchyBoundsListenerListCopier() );
		result.add( createHierarchyListenerListCopier() );
		result.add( createKeyListenerListCopier() );
		result.add( createMouseListenerListCopier() );
		result.add( createMouseMotionListenerListCopier() );
		result.add( createMouseWheelListenerListCopier() );
		result.add( createPropertyChangeListenerListCopier() );
//		result.add( createPropertyChangeByPropertyListenerListCopier() );
		result.add( createComponentOrientationCopier() );
//		result.add( createAlignmentXCopier() );
//		result.add( createAlignmentYCopier() );
		result.add( createBoundsCopier() );
		result.add( createCursorCopier() );
//		result.add( createFocusTraversalKeysRangeCopier() );
//		result.add( createFocusTraversalKeysEnabledCopier() );
		result.add( createFontCopier() );
		result.add( createForegroundCopier() );
		result.add( createLocaleCopier() );
		result.add( createMaximumSizeCopier() );
		result.add( createMinimumSizeCopier() );
		result.add( createNameCopier() );
		result.add( createPreferredSizeCopier() );
		result.add( createEnabledCopier() );
		result.add( createFocusableCopier() );
		result.add( createVisibleCopier() );

		return( result );
	}

	protected CompCopier<Component> createBackgroundCopier()
	{
		return( (originalComponent, newComponent) -> copyBackground( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createComponentListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyComponentListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createFocusListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyFocusListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createHierarchyBoundsListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyHierarchyBoundsListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createHierarchyListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyHierarchyListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createKeyListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyKeyListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createMouseListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyMouseListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createMouseMotionListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyMouseMotionListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createMouseWheelListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyMouseWheelListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createPropertyChangeListenerListCopier()
	{
		return( (originalComponent, newComponent) -> copyPropertyChangeListenerList( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createComponentOrientationCopier()
	{
		return( (originalComponent, newComponent) -> copyComponentOrientation( originalComponent, newComponent ) );
	}
/*
	protected CompCopier<Component> createAlignmentXCopier()
	{
		CompCopier<Component> result = new CompCopier<Component>() {
			@Override
			public void copy(Component originalComponent, Component newComponent) {
				copyAlignmentX( originalComponent, newComponent );
			}
		};

		return( result );
	}

	protected CompCopier<Component> createAlignmentYCopier()
	{
		CompCopier<Component> result = new CompCopier<Component>() {
			@Override
			public void copy(Component originalComponent, Component newComponent) {
				copyAlignmentY( originalComponent, newComponent );
			}
		};

		return( result );
	}
*/
	protected CompCopier<Component> createBoundsCopier()
	{
		return( (originalComponent, newComponent) -> copyBounds( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createCursorCopier()
	{
		return( (originalComponent, newComponent) -> copyCursor( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createFontCopier()
	{
		return( (originalComponent, newComponent) -> copyFont( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createForegroundCopier()
	{
		return( (originalComponent, newComponent) -> copyForeground( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createLocaleCopier()
	{
		return( (originalComponent, newComponent) -> copyLocale( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createMaximumSizeCopier()
	{
		return( (originalComponent, newComponent) -> copyMaximumSize( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createMinimumSizeCopier()
	{
		return( (originalComponent, newComponent) -> copyMinimumSize( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createNameCopier()
	{
		return( (originalComponent, newComponent) -> copyName( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createPreferredSizeCopier()
	{
		return( (originalComponent, newComponent) -> copyPreferredSize( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createEnabledCopier()
	{
		return( (originalComponent, newComponent) -> copyEnabled( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createFocusableCopier()
	{
		return( (originalComponent, newComponent) -> copyFocusable( originalComponent, newComponent ) );
	}

	protected CompCopier<Component> createVisibleCopier()
	{
		return( (originalComponent, newComponent) -> copyVisible( originalComponent, newComponent ) );
	}

	@Override
	public Class<Component> getParameterClass() {
		return( Component.class );
	}

	protected void copyComponentListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ComponentListener.class,
						(c) -> c.getComponentListeners(),
						(c,l) -> c.addComponentListener(l),
						(c,l) -> c.removeComponentListener(l) );
	}

	protected void copyFocusListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						FocusListener.class,
						(c) -> c.getFocusListeners(),
						(c,l) -> c.addFocusListener(l),
						(c,l) -> c.removeFocusListener(l) );
	}

	protected void copyHierarchyBoundsListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						HierarchyBoundsListener.class,
						(c) -> c.getHierarchyBoundsListeners(),
						(c,l) -> c.addHierarchyBoundsListener(l),
						(c,l) -> c.removeHierarchyBoundsListener(l) );
	}

	protected void copyHierarchyListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						HierarchyListener.class,
						(c) -> c.getHierarchyListeners(),
						(c,l) -> c.addHierarchyListener(l),
						(c,l) -> c.removeHierarchyListener(l) );
	}

	protected void copyKeyListenerList( Component originalComponent, Component newComponent )
	{
/*
		if( ( orig != null ) && ( output != null ) )
		{
			KeyListener[] listeners = orig.getKeyListeners();
			if( listeners != null )
			{
				for( KeyListener listener: listeners )
				{
					orig.removeKeyListener(listener);

//					if( !isClassOfJdk( listener ) )
//						output.addKeyListener(listener);
				}
			}
		}
*/
		copyListeners( originalComponent, newComponent,
						KeyListener.class,
						(c) -> c.getKeyListeners(),
						(c,l) -> c.addKeyListener(l),
						(c,l) -> c.removeKeyListener(l) );
	}

	protected void copyMouseListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MouseListener.class,
						(c) -> c.getMouseListeners(),
						(c,l) -> c.addMouseListener(l),
						(c,l) -> c.removeMouseListener(l) );
	}

	protected void copyMouseMotionListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MouseMotionListener.class,
						(c) -> c.getMouseMotionListeners(),
						(c,l) -> c.addMouseMotionListener(l),
						(c,l) -> c.removeMouseMotionListener(l) );
	}

	protected void copyMouseWheelListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						MouseWheelListener.class,
						(c) -> c.getMouseWheelListeners(),
						(c,l) -> c.addMouseWheelListener(l),
						(c,l) -> c.removeMouseWheelListener(l) );
	}

	protected void copyPropertyChangeListenerList( Component originalComponent, Component newComponent )
	{
		copyListeners( originalComponent, newComponent,
						PropertyChangeListener.class,
						(c) -> c.getPropertyChangeListeners(),
						(c,l) -> c.addPropertyChangeListener(l),
						(c,l) -> c.removePropertyChangeListener(l) );
	}

	protected void copyComponentOrientation( Component originalComponent, Component newComponent )
	{
		newComponent.setComponentOrientation( originalComponent.getComponentOrientation() );
	}
/*
	protected void copyAlignmentX( Component originalComponent, Component newComponent )
	{
		newComponent.setAlignmentX( originalComponent.getAlignmentX() );
	}

	protected void copyAlignmentY( Component originalComponent, Component newComponent )
	{
		newComponent.setAlignmentY( originalComponent.getAlignmentY() );
	}
*/
	protected void copyBounds( Component originalComponent, Component newComponent )
	{
		newComponent.setBounds( originalComponent.getBounds() );
	}

	protected void copyCursor( Component originalComponent, Component newComponent )
	{
		newComponent.setCursor( originalComponent.getCursor() );
	}

	protected void copyFont( Component originalComponent, Component newComponent )
	{
		newComponent.setFont( originalComponent.getFont() );
	}

	protected void copyForeground( Component originalComponent, Component newComponent )
	{
		newComponent.setForeground( originalComponent.getForeground() );
	}

	protected void copyLocale( Component originalComponent, Component newComponent )
	{
		newComponent.setLocale( originalComponent.getLocale() );
	}

	protected void copyMaximumSize( Component originalComponent, Component newComponent )
	{
		if( originalComponent.isMaximumSizeSet() )
			newComponent.setMaximumSize( originalComponent.getMaximumSize() );
	}

	protected void copyMinimumSize( Component originalComponent, Component newComponent )
	{
		if( originalComponent.isMinimumSizeSet() )
			newComponent.setMinimumSize( originalComponent.getMinimumSize() );
	}

	protected void copyName( Component originalComponent, Component newComponent )
	{
		newComponent.setName( originalComponent.getName() );
	}

	protected void copyPreferredSize( Component originalComponent, Component newComponent )
	{
		if( originalComponent.isPreferredSizeSet() )
			newComponent.setPreferredSize( originalComponent.getPreferredSize() );
	}

	protected void copyEnabled( Component originalComponent, Component newComponent )
	{
		newComponent.setEnabled( originalComponent.isEnabled() );
	}

	protected void copyFocusable( Component originalComponent, Component newComponent )
	{
		newComponent.setFocusable( originalComponent.isFocusable() );
	}

	protected void copyVisible( Component originalComponent, Component newComponent )
	{
		newComponent.setVisible( originalComponent.isVisible() );
	}

	protected void copyBackground( Component originalComponent, Component newComponent )
	{
		newComponent.setBackground( originalComponent.getBackground() );
	}
}

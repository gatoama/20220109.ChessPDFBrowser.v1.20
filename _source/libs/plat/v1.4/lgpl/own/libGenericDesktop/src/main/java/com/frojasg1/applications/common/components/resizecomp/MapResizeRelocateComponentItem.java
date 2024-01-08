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
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.general.collection.impl.ThreadSafeGenListWrapper;
import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Component;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Usuario
 */
public class MapResizeRelocateComponentItem extends Hashtable< Component, ResizeRelocateItem > implements ResizeRelocateItem_parent
{
	protected ResizeRelocateItem_parent _parent = null;

	protected Map<Component, Component> _lastSwitchedMap = null;

	protected ThreadSafeGenListWrapper<JPopupMenu> _jPopupMenuList = new ThreadSafeGenListWrapper<>();


	public MapResizeRelocateComponentItem()
	{}

/*
	public MapResizeRelocateComponentItem( Component comp )
	{
		JFrameInternationalization.setInitialSizesForContentPanes( comp );	// it sets the content panes at the right size
	}
*/	
	@Override
	public ResizeRelocateItem getResizeRelocateComponentItem( Component comp )
	{
		return( ( _parent == null ? null : _parent.getResizeRelocateComponentItem( comp ) ) );
	}

	public void setParent( ResizeRelocateItem_parent parent )
	{
		_parent = parent;
	}

	protected Component getParent( Component comp )
	{
		Component result = null;
		
		if( comp != null )
			result = comp.getParent();
		
		if( result instanceof JViewport )
			result = result.getParent();

		return( result );
	}

	public boolean calculatePostponeInitialization( Component comp ) throws InternException
	{
		boolean postpone_initialization = false;
/*
		if( ( comp!= null ) && ( comp.getParent() != null ) &&
			( comp.getParent().getName() != null ) &&
			( comp.getParent().getName().equals( "null.contentPane" ) ) )
*/
		if( comp!= null )
		{
			Component ancestror = ViewFunctions.instance().getRootAncestor(comp);

			Component parent = getParent( comp );

			if( ( parent == null ) ||
				( parent == ViewFunctions.instance().getContentPane(ancestror) )
				)
				postpone_initialization = true;
		}

		return( postpone_initialization );
	}

	public ResizeRelocateItem putResizeRelocateComponentItem( Component comp, int flags ) throws InternException
	{
		ResizeRelocateItem result = createResizeRelocateItem( comp, flags );
		putResizeRelocateComponentItem( result );

		return( result );
	}

	public ResizeRelocateItem putResizeRelocateComponentItem( Component comp, int flags,
										boolean postpone_initialization ) throws InternException
	{
		boolean isAlreadyZoomed = true;
		return( putResizeRelocateComponentItem( comp, flags, postpone_initialization, isAlreadyZoomed ) );
	}

	// public function to add a resize and relocate policy for a particular component
	// an example of invocation for this function:
	// a_intern.putResizeRelocateComponentItem(jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
	public ResizeRelocateItem putResizeRelocateComponentItem( Component comp, int flags,
															boolean postpone_initialization,
															Boolean isAlreadyZoomed ) throws InternException
	{
		ResizeRelocateItem rri = createResizeRelocateItem(comp, flags, postpone_initialization,
															isAlreadyZoomed );
		putResizeRelocateComponentItem(rri);

		return( rri );
	}

	public ResizeRelocateItem createResizeRelocateItem( Component comp, int flags,
														boolean postpone_initialization ) throws InternException
	{
		boolean isAlreadyZoomed = false;
		return( createResizeRelocateItem( comp, flags, postpone_initialization,
											isAlreadyZoomed ) );
	}
	public ResizeRelocateItem createResizeRelocateItem( Component comp, int flags,
														boolean postpone_initialization,
														Boolean isAlreadyZoomed ) throws InternException
	{
		return( ResizeRelocateItem.buildResizeRelocateItem(comp, flags, this,
														postpone_initialization, isAlreadyZoomed ) );
	}

	public synchronized void putAll(Map<? extends Component, ? extends ResizeRelocateItem> t)
	{
		for (Map.Entry<? extends Component, ? extends ResizeRelocateItem> e : t.entrySet())
		{
			e.getValue().setParent( this );
			put(e.getKey(), e.getValue());
		}

		switchComponents();
	}


	public ResizeRelocateItem createResizeRelocateItem( Component comp, int flags ) throws InternException
	{
		boolean postpone_initialization = calculatePostponeInitialization( comp );
		return( createResizeRelocateItem(comp, flags, postpone_initialization ) );
	}

	// public function to add a resize and relocate policy for a particular component
	// an example of invocation for this function:
	// a_intern.putResizeRelocateComponentItem(jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
	public ResizeRelocateItem putResizeRelocateComponentItem( ResizeRelocateItem rri ) throws InternException
	{
		ResizeRelocateItem result = null;
		Component comp = rri.getComponent();
		put( comp, rri);

		if( comp instanceof JScrollPane )
		{
			JScrollPane pane = (JScrollPane) comp;
			boolean postpone_initialization = !rri.isInitialized();
			result = putResizeRelocateComponentItem(pane.getViewport(), rri.getFlags(), postpone_initialization );
		}

		return( result );
	}

	public Component switchComponent( Component oldComp )
	{
		Component newComponent = null;

		if( _lastSwitchedMap != null )
			newComponent = _lastSwitchedMap.get( oldComp );

		if( newComponent == null )
			newComponent = oldComp;

		return( newComponent );
	}

	public void switchComponents()
	{
		switchComponents( _lastSwitchedMap );
	}

	public void switchComponents( Map<Component, Component> switchMap )
	{
		if( switchMap != null )
		{
			Iterator< Map.Entry< Component, Component >> it = switchMap.entrySet().iterator();

			while( it.hasNext() )
			{
				Map.Entry<Component, Component> entry = it.next();

				ResizeRelocateItem rri = get( entry.getKey() );

				if( rri != null )
				{
					remove( entry.getKey() );
					rri.setComponent( entry.getValue() );
					put( entry.getValue(), rri );
				}
			}
		}

		_lastSwitchedMap = switchMap;
	}

	@Override
	public boolean isResizeRelocateItemsResizeListenersBlocked()
	{
		return( ( _parent != null ) &&
				_parent.isResizeRelocateItemsResizeListenersBlocked() );
	}

	@Override
	public boolean isResizeDragging()
	{
		boolean result = false;
		if( _parent != null )
			result = _parent.isResizeDragging();

		return( result );
	}

	@Override
	public JPopupMenu getNonInheritedPopupMenu(JComponent jcomp)
	{
		return( ( _parent == null ? null : _parent.getNonInheritedPopupMenu(jcomp) ) );
	}

	@Override
	public void executeResizeRelocateItemRecursive(Component comp)
	{
		if( _parent != null )
			_parent.executeResizeRelocateItemRecursive(comp);
	}

	@Override
	public InternationalizedWindow getInternationalizedWindow() {
		return( ( _parent != null ) ?
				_parent.getInternationalizedWindow() :
				null );
	}

	@Override
	public ResizeRelocateItem getResizeRelocateComponentItemOnTheFly( Component comp )
	{
		return( ( _parent == null ? null : _parent.getResizeRelocateComponentItemOnTheFly( comp ) ) );
	}

	public List<JPopupMenu> getPopupMenus()
	{
		return( _jPopupMenuList.getListCopy() );
	}

	public void addPopupMenu( JPopupMenu jPopupMenu )
	{
		_jPopupMenuList.add(jPopupMenu);
	}

	public void addAllPopupMenus( Collection<? extends JPopupMenu> col )
	{
		_jPopupMenuList.addAll(col);
	}
}

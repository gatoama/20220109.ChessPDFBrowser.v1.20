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
package com.frojasg1.applications.common.components.data;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem_parent;
import com.frojasg1.general.CallStackFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import org.slf4j.LoggerFactory;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.OriginalColorThemeAttribute;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MapOfComponents
{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MapOfComponents.class);

	protected Map< Component, ComponentData > _map;

	ResizeRelocateItem_parent _rriParent;

	public MapOfComponents(ResizeRelocateItem_parent rriParent)
	{
		_rriParent = rriParent;
		_map = new ConcurrentHashMap< Component, ComponentData >();
	}

	public ComponentData get( Component comp )
	{
		ComponentData result = null;
		if( comp != null )
			result = _map.get(comp);

		return( result );
	}

	public void put( Component comp, ComponentData cd )
	{
		if( comp != null )
			_map.put( comp, cd );
	}

	protected InternationalizedWindow getInternationalizedWindow()
	{
		InternationalizedWindow result = null;
		if( _rriParent != null )
			result = _rriParent.getInternationalizedWindow();

		return( result );
	}

	protected ComponentData createComponentDataOnTheFly( Component comp )
	{
		return( createComponentDataGen( comp, true ) );
	}

	protected ComponentData createComponentDataNormal( Component comp )
	{
		return( createComponentDataGen( comp, false ) );
	}

	protected ComponentData createComponentDataGen( Component comp, boolean isOnTheFly )
	{
		ComponentData result = new ComponentData(comp);

		if( comp instanceof ColorThemeChangeableStatusBuilder )
			result.setColorThemeChangeable( ( (ColorThemeChangeableStatusBuilder) comp ).createColorThemeChangeableStatus() );

		if( comp instanceof ColorThemeChangeableStatus )
			result.setColorThemeChangeable( (ColorThemeChangeableStatus) comp );
		else
		{
			InternationalizedWindow iw = getInternationalizedWindow();
			if( iw != null )
			{
				Boolean initialDarkMode = getOriginalColorMode(comp);
				if( initialDarkMode == null )
				{
					if( !isOnTheFly )
						initialDarkMode = iw.wasLatestModeDark();
					else
						initialDarkMode = false;
				}

				result.getColorThemeChangeableStatus().setDarkMode(initialDarkMode, iw.getColorInversor() );
				result.getColorThemeChangeableStatus().setLatestWasDark(initialDarkMode);
			}
		}

		return( result );
	}

	protected Boolean getOriginalColorMode( Component comp )
	{
		Boolean result = NullFunctions.instance().getIfNotNull(  ComponentFunctions.instance().getFirstParentInstanceOf(
																		OriginalColorThemeAttribute.class, comp),
																OriginalColorThemeAttribute::getOriginallyWasDarkMode );

		return( result );
	}

	public ComponentData getOrCreate( Component comp )
	{
		return( getOrCreateGen(comp, false) );
	}

	public ComponentData getOrCreateOnTheFly( Component comp )
	{
		return( getOrCreateGen(comp, true) );
	}

	public ComponentData getOrCreateGen( Component comp, boolean isOnTheFly )
	{
		ComponentData result = null;
		if( comp != null )
		{
			result = get(comp);
			if( result == null )
			{
				synchronized( this )
				{
					result = get(comp);
					if( result == null )
					{
						result = createComponentDataGen(comp, isOnTheFly);
						put( comp, result );
					}
				}
			}
		}
		else
			CallStackFunctions.instance().dumpCallStack( "Component is null (is the key). Cannot create element." );

		return( result );
	}

	public List<ResizeRelocateItem> getListOfResizeRelocateItems()
	{
		return( _map.values().stream()
					.map( cd -> cd.getResizeRelocateItem() )
					.filter( rri -> ( rri != null ) )
					.collect( Collectors.toList() ) );
	}

	public ResizeRelocateItem getResizeRelocateItem( Component comp )
	{
		ResizeRelocateItem result = null;
		ComponentData cd = getOrCreate( comp );
		if( cd != null )
			result = cd.getResizeRelocateItem();

		return( result );
	}

	public ResizeRelocateItem getResizeRelocateItemOnTheFly( Component comp )
	{
		ResizeRelocateItem result = null;
		ComponentData cd = getOrCreateOnTheFly( comp );
		if( cd != null )
			result = cd.getResizeRelocateItem();

		return( result );
	}

	public InfoForResizingPanels getInfoForResizingPanels( Component comp )
	{
		InfoForResizingPanels result = null;
		ComponentData cd = get( comp );
		if( cd != null )
			result = cd.getInfoForResizingPanels();

		return( result );
	}

	public boolean getResizeParents( Component comp )
	{
		boolean result = false;
		ComponentData cd = get( comp );
		if( cd != null )
			result = cd.getResizeParents();

		return( result );
	}

	public TextUndoRedoInterface getTextUndoRedoManager( Component comp )
	{
		TextUndoRedoInterface result = null;
		ComponentData cd = get( comp );
		if( cd != null )
			result = cd.getTextUndoRedoManager();

		return( result );
	}

	public TextCompPopupManager getTextCompPopupManager( Component comp )
	{
		TextCompPopupManager result = null;
		ComponentData cd = get( comp );
		if( cd != null )
			result = cd.getTextCompPopupManager();

		return( result );
	}

	public void setResizeRelocateItem( Component comp, ResizeRelocateItem rri )
	{
		ComponentData cd = getOrCreate( comp );
		cd.setResizeRelocateItem( rri );
	}

	public void setInfoForResizingPanels( Component comp, InfoForResizingPanels ifrp )
	{
		ComponentData cd = getOrCreate( comp );
		cd.setInfoForResizingPanels( ifrp );
	}

	public void setResizeParents( Component comp, boolean resizeParents )
	{
		ComponentData cd = getOrCreate( comp );
		cd.setResizeParents( resizeParents );
	}

	public void setTextCompPopupManager( Component comp, TextCompPopupManager tpmm )
	{
		boolean isOnTheFly = false;
		setTextCompPopupManager( comp, tpmm, isOnTheFly);
	}

	public void setTextCompPopupManager( Component comp, TextCompPopupManager tpmm,
										boolean isOnTheFly)
	{
		ComponentData cd = getOrCreateGen( comp, isOnTheFly );
		cd.setTextCompPopupManager(tpmm);
	}

	public Iterator<Map.Entry<Component, ComponentData>> getEntrySetIterator()
	{
		return( _map.entrySet().iterator() );
	}

	public void removeResizeRelocateComponentItem( Component comp )
	{
		ComponentData cd = get( comp );
		if( cd != null )
		{
			cd.setResizeRelocateItem( null );
			if( cd.isEmpty() )
				_map.remove( comp );
		}
	}

	public void addMapResizeRelocateComponents( MapResizeRelocateComponentItem map )
	{
		addMapResizeRelocateComponents( map, 1.0D );
	}

	public void addMapResizeRelocateComponents( MapResizeRelocateComponentItem map,
												double zoomFactor )
	{
		Iterator<Map.Entry< Component, ResizeRelocateItem >> it = map.entrySet().iterator();
		
		while( it.hasNext() )
		{
			Map.Entry< Component, ResizeRelocateItem > entry = it.next();

			Component comp = entry.getKey();
			Component newComp = map.switchComponent(comp);

			ResizeRelocateItem rri = entry.getValue();

			if( newComp != comp )
			{
				rri.setComponent( newComp );
			}

			try
			{
				if( !rri.isInitialized() )
					rri.initialize( zoomFactor );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			setResizeRelocateItem( newComp, rri );
		}
	}

	protected ColorThemeChangeableStatus getNewColorThemeChangeableStatus( Component comp )
	{
		ColorThemeChangeableStatus result = null;
		
		if( comp instanceof ColorThemeChangeableStatus )
			result = (ColorThemeChangeableStatus) comp;
		else if( comp instanceof ColorThemeChangeableStatusBuilder )
			result = ( (ColorThemeChangeableStatusBuilder) comp ).createColorThemeChangeableStatus();

		return( result );
	}

	protected void switchComponent( Component oldComp, Component newComp )
	{
		ComponentData cd = get( oldComp );
		if( cd != null )
		{
			ResizeRelocateItem rri = cd.getResizeRelocateItem();

			_map.remove( oldComp );
			_map.put( newComp, cd );
			cd.setComponent( newComp );
			if( rri != null )
				rri.setComponent(newComp);

			ColorThemeChangeableStatus ctc = cd.getColorThemeChangeableStatus();
			ColorThemeChangeableStatus newCtc = getNewColorThemeChangeableStatus( newComp );
			if( ( newCtc != null ) && ( ctc != null ) )
				newCtc.setDarkMode( ctc.isDarkMode(), getColorInversor() );

			if( newCtc != null )
				cd.setColorThemeChangeable(newCtc);
		}
	}

	public void switchComponents( Map< Component, Component > switchMap )
	{
		switchComponentsGen( switchMap, false );
	}

	public void switchComponentsGen( Map< Component, Component > switchMap, boolean isOnTheFly )
	{
		Iterator< Map.Entry< Component, Component > > it = switchMap.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry< Component, Component > entry = it.next();

			if( entry.getValue() == null )
				this.removeResizeRelocateComponentItem( entry.getValue() );
			else
			{
				switchComponent( entry.getKey(), entry.getValue() );
				ResizeRelocateItem rri = isOnTheFly ?
										getResizeRelocateItemOnTheFly( entry.getKey() ) :
										getResizeRelocateItem( entry.getKey() );
				if( rri != null )
					rri.setComponent( entry.getValue() );
			}
		}
	}

	protected boolean hasToCreateResizeRelocateItem( Component comp )
	{
		boolean result = false;

		result = !( comp instanceof BasicSplitPaneDivider );

		return( result );
	}

	public void createAndStoreNewResizeRelocateItem( Component comp ) throws InternException
	{
		if( hasToCreateResizeRelocateItem( comp ) )
		{
			ResizeRelocateItem rri = createDefaultResizeRelocateItem( comp );

			setResizeRelocateItem(comp, rri);
		}
	}

	public ResizeRelocateItem createDefaultResizeRelocateItem( Component comp ) throws InternException
	{
		int flags = 0;
		boolean postpone_initialization = false;

		boolean isAlreadyZoomed = false;
		ResizeRelocateItem rri = ResizeRelocateItem.buildResizeRelocateItem(comp, flags,
													_rriParent, postpone_initialization,
													isAlreadyZoomed );

		return( rri );
	}

	public void createComponents( Collection<Component> col )
	{
		Iterator< Component > it = col.iterator();
		while( it.hasNext() )
		{
			try
			{
				createAndStoreNewResizeRelocateItem( it.next() );
			}
			catch( Exception ex )
			{}
		}
	}
	
	protected ColorInversor getColorInversor()
	{
		ColorInversor result = null;

		for( Component comp: _map.keySet() )
		{
			result = FrameworkComponentFunctions.instance().getColorInversor(comp);
			if( result != null )
				break;
		}

		return( result );
	}
/*
	public int getFactoredTextSize( Component comp, double factor )
	{
		int result = -1;
		
		ComponentData cd = get( comp );
		if( cd != null )
			result = cd.getFactoredTextSize( factor );

		return( result );
	}

	public void setOriginalTextSize( Component comp, double originalTextSize )
	{
		ComponentData cd = getOrCreate( comp );
		cd.setOriginalTextSize( originalTextSize );
	}
*/
}

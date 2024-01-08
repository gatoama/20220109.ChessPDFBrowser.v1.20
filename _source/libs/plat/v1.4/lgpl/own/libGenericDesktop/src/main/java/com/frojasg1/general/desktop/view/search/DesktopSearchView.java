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
package com.frojasg1.general.desktop.view.search;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.search.imp.SearchReplaceContext;
import com.frojasg1.general.desktop.generic.view.DesktopViewWindow;
import com.frojasg1.general.search.SearchReplaceContextInterface;
import com.frojasg1.general.search.SearchReplaceFactory;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import java.awt.Component;
import com.frojasg1.general.search.SearchReplaceForWindowInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopSearchView implements SearchReplaceForWindowInterface, DesktopViewWindow
{
	protected static SearchReplaceFactory _srFactory = new DesktopSearchReplaceFactory();
	protected static DesktopSearchView _instance = null;

	protected DesktopSearchAndReplaceWindow _srw = null;

	public static void setSearchReplaceFactory( SearchReplaceFactory srf )
	{
		_srFactory = srf;
	}

	public static DesktopSearchView createSearchView( BaseApplicationConfigurationInterface appConf,
												String configurationBaseFileName,
												SearchReplaceContext src )
	{
		DesktopSearchAndReplaceWindow srw = new DesktopSearchAndReplaceWindow( appConf, configurationBaseFileName );
//		srw.setTextComponent( vtc );
		_instance = new DesktopSearchView( srw );
		if( src != null )
			_instance.setSearchReplaceContext( src );
		return( _instance );
	}

	public static DesktopSearchView instance()
	{
		return( _instance );
	}

	@Override
	public void setSearchReplaceContext( SearchReplaceContextInterface src )
	{
		_srw.setSearchReplaceContext( src );
	}

	public static SearchReplaceContextInterface createSearchReplaceContext( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		SearchReplaceContextInterface result = _srFactory.createSearchReplaceContext( undoRedoManagerOfTextComp );
		result.initialize();
		return( result );
	}
/*
	public void setTextComponent( ViewTextComponent vtc )
	{
		_srw.setTextComponent( vtc );
	}
*/
	public DesktopSearchView( DesktopSearchAndReplaceWindow srw )
	{
		_srw = srw;
	}

	@Override
	public void setVisible(boolean value)
	{
		_srw.setVisible( value );
	}

	@Override
	public Component getComponent()
	{
		return( _srw );
	}

	public DesktopSearchAndReplaceWindow getSearchAndReplaceWindow()
	{
		return( _srw );
	}

	public TextUndoRedoInterface getUndoRedoManager()
	{
		return( _srw.getUndoRedoManager() );
	}

	@Override
	public void deiconify()
	{
		_srw.deiconify();
	}

	@Override
	public void iconify()
	{
		_srw.iconify();
	}

	@Override
	public boolean isIconified()
	{
		return( _srw.isIconified() );
	}

	@Override
	public DesktopViewComponent getParentViewComponent()
	{
		return( DesktopGenericFunctions.instance().getViewFacilities().getParentViewComponent(this) );
	}

	@Override
	public void requestFocus()
	{
		_srw.requestFocus();
	}

	@Override
	public boolean isFocusable()
	{
		return( _srw.isFocusable() );
	}

	@Override
	public boolean hasFocus()
	{
		return( _srw.hasFocus() );
	}

	@Override
	public void releaseResources() {
		_srw = null;
	}

	@Override
	public void closeWindow()
	{
		_srw.closeWindow();
	}
}

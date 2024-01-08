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
package com.frojasg1.general.desktop.view.labels;

import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.listeners.GenericListOfListeners;
import com.frojasg1.general.listeners.GenericListOfListenersImp;
import com.frojasg1.general.listeners.GenericNotifier;
import com.frojasg1.general.listeners.GenericObserved;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UrlJLabel extends JLabel implements MouseListener, GenericObserved<UrlLabelListener>
{
	protected GenericNotifier< UrlLabelListener > _genericNotifier = null;
	protected String _url = null;
	protected GenericListOfListeners<UrlLabelListener> _listOfListeners = null;

	public UrlJLabel()
	{
		super();
	}

	protected GenericListOfListeners<UrlLabelListener> createListOfListeners()
	{
		return( new GenericListOfListenersImp<UrlLabelListener>() );
	}

	protected GenericNotifier< UrlLabelListener > createGenericNotifier()
	{
		return( (lis) -> lis.urlLabelClicked(this) );
	}

	public void init()
	{
		_listOfListeners = createListOfListeners();
		_genericNotifier = createGenericNotifier();

		addMouseListener(this);

		Cursor currentCursor = getCursor();
		if( ( currentCursor != null ) &&
			( currentCursor.getName().equals(CursorFunctions._defaultCursor.getName() ) ) )
		{
			setCursor(CursorFunctions._handCursor );
		}
	}
	
	public String getUrl()
	{
		return( _url );
	}

	public void setUrl( String url )
	{
		_url = url;
	}

	public void setUnderlineFont( boolean value )
	{
		Font newFont = null;
		if( value )
		{
			newFont = FontFunctions.instance().getUnderlinedFont( getFont() );
		}
		else
		{
			newFont = FontFunctions.instance().getUnunderlinedFont( getFont() );
		}

		setFont( newFont );
	}

	protected String getFinalUrl()
	{
		return( GenericFunctions.instance().getApplicationFacilities().buildResourceCounterUrl( getUrl() ) );
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if( ( _url != null ) && SwingUtilities.isLeftMouseButton( e ) )
		{
			GenericFunctions.instance().getSystem().browse( getFinalUrl() );
			informListeners();
		}
	}

	protected void informListeners()
	{
		_listOfListeners.notifyListeners( _genericNotifier );
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void addListenerGen(UrlLabelListener listener)
	{
		_listOfListeners.add(listener);
	}

	@Override
	public void removeListenerGen(UrlLabelListener listener)
	{
		_listOfListeners.remove(listener);
	}

	
}

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
package com.frojasg1.general.desktop.view.zoom.imp.listeners;

import com.frojasg1.general.desktop.keyboard.listener.imp.GenericKeyListenerImp;
import com.frojasg1.general.desktop.keyboard.listener.imp.KeyImp;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CursorKeyListener {
	protected static final int LEFT_ARROW = 0;
	protected static final int RIGHT_ARROW = 1;
	protected GenericKeyListenerImp _listener = null;
	protected JTextComponent _textComp = null;

	public CursorKeyListener( JTextComponent textComp )
	{
		_textComp = textComp;
	}

	public void init()
	{
		_listener = createKeyListener();
		addListener();
		createActions();
	}

	protected GenericKeyListenerImp createKeyListener()
	{
		return( new GenericKeyListenerImp() );
	}

	protected void createActions()
	{
//		_listener.addKey(LEFT_ARROW, new KeyImp( KeyEvent.VK_LEFT, 0 ), () -> addCaretPosition( -1 ) );
//		_listener.addKey(RIGHT_ARROW, new KeyImp( KeyEvent.VK_RIGHT, 0 ), () -> addCaretPosition( 1 ) );
	}

	protected void addCaretPosition( int delta )
	{
		if( _textComp.isEnabled() && _textComp.isEditable() && _textComp.hasFocus() )
		{
			int pos = _textComp.getCaretPosition() + delta;

			if( ( pos >= 0 ) && ( pos <= _textComp.getDocument().getLength() ) )
				_textComp.setCaretPosition(pos);
		}
	}

	protected void addListener()
	{
		_textComp.addKeyListener(_listener);
	}

	protected void removeListener()
	{
		_textComp.removeKeyListener(_listener);
	}

	public void releaseResources()
	{
		removeListener();

		_listener = null;
		_textComp = null;
	}
}

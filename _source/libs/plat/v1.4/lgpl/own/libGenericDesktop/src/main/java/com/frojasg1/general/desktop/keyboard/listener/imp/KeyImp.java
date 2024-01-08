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
package com.frojasg1.general.desktop.keyboard.listener.imp;

import com.frojasg1.general.desktop.keyboard.listener.KeyInterface;
import java.awt.event.KeyEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class KeyImp implements KeyInterface
{
	protected int _keyCode = -1;
	protected int _modifiers = 0;

	public KeyImp( int keyCode, int modifiers )
	{
		_keyCode = keyCode;
		_modifiers = modifiers;
	}

	@Override
	public void setKeyCode(int keyCode)
	{
		_keyCode = keyCode;
	}

	@Override
	public int getKeyCode()
	{
		return( _keyCode );
	}

	@Override
	public void setModifiers(int modifiers)
	{
		_modifiers = modifiers;
	}

	@Override
	public int getModifiers()
	{
		return( _modifiers );
	}

	@Override
	public boolean matches(KeyEvent evt)
	{
		boolean result = matches( evt.getKeyCode(), evt.getModifiers() );
		return( result );
	}

	protected boolean matchesFlag( int modifiers, int flag )
	{
		boolean and1 = ( _modifiers & flag ) != 0;
		boolean and2 = ( modifiers & flag ) != 0;

		boolean result = !( and1 ^ and2 );

		return( result );
	}

	@Override
	public boolean matches(int keyCode, int modifiers)
	{
		boolean result = ( keyCode == _keyCode ) &&
							( matchesFlag( modifiers, KeyEvent.CTRL_MASK ) ) &&
							( matchesFlag( modifiers, KeyEvent.SHIFT_MASK ) ) &&
							( matchesFlag( modifiers, KeyEvent.ALT_MASK ) ) &&
							( matchesFlag( modifiers, KeyEvent.ALT_GRAPH_MASK ) );

		return( result );
	}
}


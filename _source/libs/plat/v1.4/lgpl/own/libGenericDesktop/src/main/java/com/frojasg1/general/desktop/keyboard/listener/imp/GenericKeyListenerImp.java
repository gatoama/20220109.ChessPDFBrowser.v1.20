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

import com.frojasg1.general.desktop.keyboard.listener.GenericKeyListenerInterface;
import com.frojasg1.general.desktop.keyboard.listener.KeyInterface;
import com.frojasg1.general.executor.GenericExecutor;
import java.awt.event.KeyEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericKeyListenerImp implements GenericKeyListenerInterface
{
	protected GenericKeyDispatcherContainer _container = new GenericKeyDispatcherContainer();

	public GenericKeyListenerImp()
	{}

	@Override
	public void addKey(int typeOfKey, KeyInterface key, GenericExecutor executor)
	{
		_container.addKey( typeOfKey, key, executor );
	}

	@Override
	public void removeKey(int typeOfKey )
	{
		_container.removeKey( typeOfKey );
	}

	@Override
	public void dispatchKeyEvent(KeyEvent evt)
	{
		_container.dispatchKeyEvent( evt );
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		dispatchKeyEvent( e );
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void changeKey(int typeOfKey, KeyInterface key)
	{
		_container.changeKey( typeOfKey, key );
	}

	@Override
	public void changeExecutor(int typeOfKey, GenericExecutor executor)
	{
		_container.changeExecutor( typeOfKey, executor );
	}
}

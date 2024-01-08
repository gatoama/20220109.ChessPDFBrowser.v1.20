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

import com.frojasg1.general.desktop.keyboard.listener.GenericKeyDispatcherInterface;
import com.frojasg1.general.desktop.keyboard.listener.KeyInterface;
import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.GenericExecutor;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericKeyDispatcherContainer implements GenericKeyDispatcherInterface
{
	protected Map< Integer, KeyActionData > _map = new HashMap< Integer, KeyActionData >();

	public GenericKeyDispatcherContainer()
	{	}
	
	protected KeyActionData createKeyActionData( KeyInterface ki, GenericExecutor ei )
	{
		return( new KeyActionData( ki, ei ) );
	}

	@Override
	public synchronized void addKey(int typeOfKey, KeyInterface key, GenericExecutor executor)
	{
		_map.put( typeOfKey, createKeyActionData( key, executor ) );
	}

	@Override
	public synchronized void removeKey(int typeOfKey)
	{
		_map.remove( typeOfKey );
	}

	@Override
	public synchronized void dispatchKeyEvent(KeyEvent evt)
	{
		Iterator< Map.Entry< Integer, KeyActionData > > it = _map.entrySet().iterator();
		
//		System.out.println( "keyPressed: " + evt.getKeyCode() );
		while( it.hasNext() )
		{
			Map.Entry< Integer, KeyActionData > entry = it.next();

			KeyActionData data = entry.getValue();

//			System.out.println( "comparing with: " + data.getKeyInterface().getKeyCode() );

			if( (data != null ) &&
				(data.getKeyInterface() != null ) &&
				data.getKeyInterface().matches( evt ) )
			{
				data.getExecutor().execute();
			}
		}
	}

	@Override
	public synchronized void changeKey(int typeOfKey, KeyInterface key)
	{
		KeyActionData value = _map.get( typeOfKey );

		if( value != null )
			value.setKeyInterface(key);
	}

	@Override
	public synchronized void changeExecutor(int typeOfKey, GenericExecutor executor)
	{
		KeyActionData value = _map.get( typeOfKey );

		if( value != null )
			value.setExecutor(executor);
	}

	protected static class KeyActionData
	{
		protected KeyInterface _ki;
		protected GenericExecutor _ei;

		public KeyActionData( KeyInterface ki, GenericExecutor ei )
		{
			_ki = ki;
			_ei = ei;
		}

		public void setKeyInterface( KeyInterface ki )
		{
			_ki = ki;
		}
		
		public KeyInterface getKeyInterface()
		{
			return( _ki );
		}

		public void setExecutor( GenericExecutor ei )
		{
			_ei = ei;
		}

		public GenericExecutor getExecutor()
		{
			return( _ei );
		}
	}
}

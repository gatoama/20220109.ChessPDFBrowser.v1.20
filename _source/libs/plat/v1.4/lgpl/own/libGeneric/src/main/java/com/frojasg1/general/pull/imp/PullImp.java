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
package com.frojasg1.general.pull.imp;

import com.frojasg1.general.pull.Factory;
import com.frojasg1.general.pull.Pull;
import com.frojasg1.general.pull.Pullable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PullImp< AA extends Pullable > implements Pull<AA>
{
	protected LinkedList<AA> _list = new LinkedList<AA>();
	protected Factory<AA> _factory = null;

	public PullImp( Factory<AA> fac )
	{
		_factory = fac;
	}

	@Override
	public synchronized AA _new()
	{
		AA result = null;
		if( _list.size() > 0 )
			result = _list.removeFirst();
		else
			result = _factory.create();

		return( result );
	}

	@Override
	public synchronized void _delete( AA obj )
	{
		obj._reset();
		_list.addLast( obj );
	}

	@Override
	public synchronized void _destroy()
	{
		Iterator<AA> it = _list.iterator();
		while( it.hasNext() )
			it.next()._destroy();

		_list.clear();
		_list = null;
		_factory = null;
	}
}
